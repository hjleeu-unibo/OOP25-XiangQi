package it.unibo.xiangqi.ai.impl;

/**
 * LOGIC OF THIS SYSTEM | HOW THE BOARD SCORES ARE CALCULATED:
 * The board score is the sum of my pieces current value.
 * Each piece has their own initial value (stored as int):
 *  - GENERAL   ->  1000
 *  - ADVISOR   ->  20
 *  - ELEPHANT  ->  20
 *  - HORSE     ->  40
 *  - CHARIOT   ->  90
 *  - CANNON    ->  45
 *  - SOLDIER   ->  10
 * For a more real game experience, some pieces get different values depending by some factors:
 *  - SOLDIER   ->  over the river, its value changes to 20.
 *  - CANNON    ->  when its position is on the same row or column of the general, its values changes to 50,
 *                  but this real value is: own_value (45 / 50) - number_of_dead_piece (of all players).
 *                  The variable factor is because it became useless when it has less "stepping stones".
 *                  However, its value can't be less than 40 -> max(45/50 - x, 40).
 *  - CHARIOT   ->  when its position is on the same row or column of the enemy's general,
 *                  its value changes to 100.
 *  - HORSE     ->  over the river, its value changes to 50.
 *                  The horse became more useful when there are less "obstacles" (pieces) on the board.
 *                  The formula is: own_value (40 -> 50) + number_of_dead_piece (of all players).
 *                  The maximum value it can reach is 65 -> min(40/50 + x, 65).
 *  - ELEPHANT  ->  out of its initial row (the bottom row), its value changes to 30.
 *  - ADVISOR   ->  out of its initial row (the bottom row), its value changes to 30.
 *  - GENERAL   ->  its own value will not be changed.
 * 
 * Above, these are the value of each piece during the game.
 * But they can get some extra value:
 *  - when they are protecting some ally pieces: own_positional_value + protecting_pieces_positional_value
 *  - when they are threatening some enemy pieces: own_positional_value + K x threatening_pieces_positional_value
 *  - if protected: own_value + (K * 0.8) x target_value
 *  - unsafe move: bonus reduced by 0.7 (target > attacker) or 0.5 (target <= attacker)
 *  - if it is an attack move, it will sum the attack bonus to the board score. The logic is the same of threatening.
 * The K changes due to the piece type.
 * The positional value depends only by their position in the board.
 * With these logics, this system will prefer attack instead of protect, make the game flow faster.
 */

import java.util.ArrayList;
import java.util.List;

import it.unibo.xiangqi.ai.api.MoveCalculator;
import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameModel;
import it.unibo.xiangqi.model.api.GameState;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.model.api.RuleEngine;

public class MoveCalculatorImpl implements MoveCalculator {
    private final RuleEngine ruleEngine;

    public MoveCalculatorImpl(final RuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    @Override
    public int calculateBoardScore(final GameState gameState, final Player currentPlayer) {
        final Board board = gameState.getBoard();
        int boardScore = 0;

        for (final Piece piece : board.getPieces()) {
            if (piece.getOwner().equals(currentPlayer)) {
                updatePieceValue(piece, board);
                boardScore += piece.getCurrentValue();
            }
        }
        
        return boardScore;
    }

    @Override
    public Move getBestMove(final GameModel gameModel) {
        final Board board = gameModel.getBoard();
        final Player currentPlayer = gameModel.getCurrentPlayer();
        final GameState currentState = gameModel.copyState(); /* A state of the game. */
        int maxSimulatedScore = Integer.MIN_VALUE; /* Set to this value to handle negative board scores. */
        Move bestMove = null;
        
        for (final Piece p : board.getPieces()) {
            if (p.getOwner().equals(currentPlayer)) {
                for (final Move m : ruleEngine.getLegalMoves(p, board)) {
                    /* Added a capture bonus to encourage capture move instead of threatening moves. */
                    int captureBonus = 0;
                    final Piece capturedPiece = board.getPieceAt(m.getTo());
                    if (capturedPiece != null && !capturedPiece.getOwner().equals(currentPlayer)) {
                        captureBonus = (int)(getThreateningMultiplier(capturedPiece.getType()) * calculatePositionalValue(capturedPiece, board));
                    }

                    final GameState simulation = currentState.applyMove(m);
                    final int newBoardScore = calculateBoardScore(simulation, currentPlayer) + captureBonus;
                    if (newBoardScore >= maxSimulatedScore) {
                        maxSimulatedScore = newBoardScore;
                        bestMove = m;
                    }
                }
            }
        }

        return bestMove;
    }

    /**
     * Update the value of a piece due to the state of the game.
     * @param piece the piece to update
     * @param board the game board
     */
    private void updatePieceValue(final Piece piece, final Board board) {
        int newValue = 0; /* The returning variable. */
        final Player currentPlayer = piece.getOwner();
        List<Piece> myPieces = new ArrayList<>();
        List<Piece> enemyPieces = new ArrayList<>();

        for (final Piece p : board.getPieces()) {
            if (p.getOwner().equals(currentPlayer)) {
                myPieces.add(p);
            } else {
                enemyPieces.add(p);
            }
        }

        /* UPDATE CURRENT OWN VALUE. SEE THE DOC AT THE TOP. */
        int positionalValue = calculatePositionalValue(piece, board);

        newValue += positionalValue;

        /* THREATENING PIECES. */
        for (final Move move : ruleEngine.getLegalMoves(piece, board)) {
            final Piece capturedPiece = board.getPieceAt(move.getTo());
            boolean isProtected = false;
            if (capturedPiece != null && !capturedPiece.getOwner().equals(currentPlayer)) {
                double threatening_multiplier = getThreateningMultiplier(capturedPiece.getType());

                /* IS PROTECTED? */
                for (final Piece p : enemyPieces) {
                    for (final Move m : ruleEngine.getLegalMoves(p, board)) {
                        if (m.getTo().equals(capturedPiece.getPosition())) {
                            isProtected = true;
                            break;
                        }
                    }
                    if (isProtected) {
                        threatening_multiplier *= 0.8;
                        break;
                    }
                }

                int targetValue = calculatePositionalValue(capturedPiece, board);
                int attackBonus = (int)(threatening_multiplier * targetValue);

                boolean isSafeMove = true;
                Position newPos = capturedPiece.getPosition();

                /* Is this move take me to a dangerous state? */
                for (final Piece pp: enemyPieces) {
                    for (final Move mm : ruleEngine.getLegalMoves(pp, board)) {
                        if (mm.getTo().equals(newPos)) {
                            isSafeMove = false;
                            break;
                        }
                    }
                    if (!isSafeMove) {
                        break;
                    }
                }

                if (!isSafeMove) {
                    int myValue = calculatePositionalValue(piece, board);

                    if (targetValue > myValue) {
                        /* If this is a more valuable piece. */
                        attackBonus = (int)(attackBonus * 0.7);
                    } else if (targetValue <= myValue) {
                        attackBonus = (int)(attackBonus * 0.5);
                    }
                }

                newValue += attackBonus;
            }
        }

        /* IS THREATENED? */
        boolean isThreatened = false;
        boolean isProtected = false;
        for (final Piece p : enemyPieces) {
            for (final Move m : ruleEngine.getLegalMoves(p, board)) {
                if (m.getTo().equals(piece.getPosition())) {
                    isThreatened = true;
                    break;
                }
            }
            if (isThreatened) {
                break;
            }
        }

        /* IS PROTECTED? */
        if (isThreatened) {
            for (final Piece myP : myPieces) {
                for (final Move myM : ruleEngine.getLegalMoves(myP, board)) {
                    if (myM.getTo().equals(piece.getPosition())) {
                        isProtected = true;
                        break;
                    }
                }
                if (isProtected) {
                    break;
                }
            }

            if (!isProtected) {
                newValue -= positionalValue;
            }
        }

        /* Update the piece currentValue with the new calculated value. */
        piece.setValue(newValue);
    }

    private int calculatePositionalValue(final Piece piece, final Board board) {
        final int RED_RIVER_ROW = 4; /* The row that rapresents that the red pieces is over the river. */
        final int BLACK_RIVER_ROW = 5; /* The row that rapresents that the black pieces is over the river. */
        final int BLACK_BOTTOM_ROW = 0; /* The bottom row for the black player. */
        final int RED_BOTTOM_ROW = 9; /* The bottom row for the red player. */
        final int MAX_PIECES = 32; /* The max number of the pieces in the game. */

        final Player currentPlayer = piece.getOwner();
        final Color currentPlayerColor = currentPlayer.getColor();
        Piece enemyGeneral = null;
        int positionalValue = 0;

        for (final Piece p : board.getPieces()) {
            if (!p.getOwner().equals(currentPlayer) && p.getType().equals(PieceType.GENERAL)) {
                enemyGeneral = p;
                break;
            }
        }

        switch (piece.getType()) {
            case SOLDIER:
                if (currentPlayerColor.equals(Color.RED) && piece.getPosition().getRow() <= RED_RIVER_ROW) {
                    positionalValue = 20;
                } else if (currentPlayerColor.equals(Color.BLACK) && piece.getPosition().getRow() >= BLACK_RIVER_ROW) {
                    positionalValue = 20;
                } else {
                    positionalValue = piece.getInitialValue();
                }
                break;
            case CANNON:
                if (enemyGeneral != null && (piece.getPosition().getCol() == enemyGeneral.getPosition().getCol() || piece.getPosition().getRow() == enemyGeneral.getPosition().getRow())) {
                    positionalValue = 50;
                } else {
                    positionalValue = piece.getInitialValue();
                }
                positionalValue -= MAX_PIECES - board.getPieces().size();
                positionalValue = Math.max(positionalValue, 40);
                break;
            case CHARIOT:
                if (enemyGeneral != null && (piece.getPosition().getCol() == enemyGeneral.getPosition().getCol() || piece.getPosition().getRow() == enemyGeneral.getPosition().getRow())) {
                    positionalValue = 100;
                } else {
                    positionalValue = piece.getInitialValue();
                }
                break;
            case HORSE:
                if (currentPlayerColor.equals(Color.RED) && piece.getPosition().getRow() <= RED_RIVER_ROW) {
                    positionalValue = 50;
                } else if (currentPlayerColor.equals(Color.BLACK) && piece.getPosition().getRow() >= BLACK_RIVER_ROW) {
                    positionalValue = 50;
                } else {
                    positionalValue = piece.getInitialValue();
                }
                positionalValue += MAX_PIECES - board.getPieces().size();
                positionalValue = Math.min(positionalValue, 65);
                break;
            /* For Elephant and Advisor, the logic is the same. */
            case ELEPHANT:
            case ADVISOR:
                if (currentPlayerColor.equals(Color.RED) && piece.getPosition().getRow() != RED_BOTTOM_ROW) {
                    positionalValue = 30;
                } else if (currentPlayerColor.equals(Color.BLACK) && piece.getPosition().getRow() != BLACK_BOTTOM_ROW) {
                    positionalValue = 30;
                } else {
                    positionalValue = piece.getInitialValue();
                }
                break;
            case GENERAL:
                positionalValue = piece.getInitialValue();
                break;
        }
        return positionalValue;
    }

    /**
     * Return the threatening multiplier due to piece type.
     * @param type the piece type
     * @return the threatening multiplier
     */
    private double getThreateningMultiplier(PieceType type) {
        double threatening_multiplier = 2.0;
        switch (type) {
            case GENERAL:
                threatening_multiplier = 6.0;
                break;
            case CHARIOT:
                threatening_multiplier = 4.0;
                break;
            case CANNON:
            case HORSE:
                threatening_multiplier = 3.0;
                break;
            default:
                break;
        }
        return threatening_multiplier;
    }
}