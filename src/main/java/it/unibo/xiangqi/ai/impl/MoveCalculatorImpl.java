package it.unibo.xiangqi.ai.impl;

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
public final class MoveCalculatorImpl implements MoveCalculator {
    private static final int RED_RIVER_ROW = 4; /* The row that rapresents that the red pieces is over the river. */
    private static final int BLACK_RIVER_ROW = 5; /* The row that rapresents that the black pieces is over the river. */
    private static final int BLACK_BOTTOM_ROW = 0; /* The bottom row for the black player. */
    private static final int RED_BOTTOM_ROW = 9; /* The bottom row for the red player. */
    private static final int MAX_PIECES = 32; /* The max number of the pieces in the game. */

    private static final double PROTECTED_PENALTY = 0.8; /* Penalty multiplier for threatening protected. */
    private static final double ATTACK_PROTECTED_GREATER = 0.7; /* Multiplier for attack a target with greater value. */
    private static final double ATTACK_PROTECTED_LOWER = 0.5; /* Multiplier for attack a target with lower value. */
    /* Threatening multiplier. */
    private static final double THREATENING_DEFAULT = 2.0;
    private static final double THREATENING_GENERAL = 6.0;
    private static final double THREATENING_CHARIOT = 4.0;
    private static final double THREATENING_CANNON = 3.0;
    private static final double THREATENING_HORSE = 3.0;

    /* Positional value. */
    private static final int SOLDIER_OVER_RIVER = 20;
    private static final int CANNON_ALIGNED_GENERAL = 50;
    private static final int CANNON_LOWER_LIMIT = 40;
    private static final int CHARIOT_ALIGNED_GENERAL = 100;
    private static final int HORSE_OVER_RIVER = 50;
    private static final int HORSE_UPPER_LIMIT = 65;
    private static final int ELE_ADV_OUT_BOTTOM = 30;

    private final RuleEngine ruleEngine;

    /**
     * The constructor.
     * 
     * @param ruleEngine rule engine for getting legal moves
     */
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
                        captureBonus = (int) (getThreateningMultiplier(capturedPiece.getType())
                                        * calculatePositionalValue(capturedPiece, board));
                    }

                    final GameState simulation = currentState.applyTurn(m);
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
     * 
     * @param piece the piece to update
     * @param board the game board
     */
    private void updatePieceValue(final Piece piece, final Board board) {
        int newValue = 0; /* The returning variable. */
        final Player currentPlayer = piece.getOwner();
        final List<Piece> myPieces = new ArrayList<>();
        final List<Piece> enemyPieces = new ArrayList<>();

        for (final Piece p : board.getPieces()) {
            if (p.getOwner().equals(currentPlayer)) {
                myPieces.add(p);
            } else {
                enemyPieces.add(p);
            }
        }

        /* UPDATE CURRENT OWN VALUE. SEE THE DOC AT THE TOP. */
        final int positionalValue = calculatePositionalValue(piece, board);

        newValue += positionalValue;

        /* THREATENING PIECES. */
        for (final Move move : ruleEngine.getLegalMoves(piece, board)) {
            final Piece capturedPiece = board.getPieceAt(move.getTo());
            boolean isProtected = false;
            if (capturedPiece != null && !capturedPiece.getOwner().equals(currentPlayer)) {
                double threateningMultiplier = getThreateningMultiplier(capturedPiece.getType());

                /* IS PROTECTED? */
                for (final Piece p : enemyPieces) {
                    for (final Move m : ruleEngine.getLegalMoves(p, board)) {
                        if (m.getTo().equals(capturedPiece.getPosition())) {
                            isProtected = true;
                            break;
                        }
                    }
                    if (isProtected) {
                        threateningMultiplier *= PROTECTED_PENALTY;
                        break;
                    }
                }

                final int targetValue = calculatePositionalValue(capturedPiece, board);
                int attackBonus = (int) (threateningMultiplier * targetValue);

                boolean isSafeMove = true;
                final Position newPos = capturedPiece.getPosition();

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
                    final int myValue = calculatePositionalValue(piece, board);

                    if (targetValue > myValue) {
                        /* If this is a more valuable piece. */
                        attackBonus = (int) (attackBonus * ATTACK_PROTECTED_GREATER);
                    } else if (targetValue <= myValue) {
                        attackBonus = (int) (attackBonus * ATTACK_PROTECTED_LOWER);
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

    /**
     * Returns the positional value of a piece.
     * 
     * @param piece the piece
     * @param board the board
     * @return the positional value
     */
    private int calculatePositionalValue(final Piece piece, final Board board) {
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
                    positionalValue = SOLDIER_OVER_RIVER;
                } else if (currentPlayerColor.equals(Color.BLACK)
                            && piece.getPosition().getRow() >= BLACK_RIVER_ROW) {
                    positionalValue = SOLDIER_OVER_RIVER;
                } else {
                    positionalValue = piece.getInitialValue();
                }
                break;
            case CANNON:
                if (enemyGeneral != null
                    && (piece.getPosition().getCol() == enemyGeneral.getPosition().getCol()
                        || piece.getPosition().getRow() == enemyGeneral.getPosition().getRow())) {
                    positionalValue = CANNON_ALIGNED_GENERAL;
                } else {
                    positionalValue = piece.getInitialValue();
                }
                positionalValue -= MAX_PIECES - board.getPieces().size();
                positionalValue = Math.max(positionalValue, CANNON_LOWER_LIMIT);
                break;
            case CHARIOT:
                if (enemyGeneral != null
                    && (piece.getPosition().getCol() == enemyGeneral.getPosition().getCol()
                        || piece.getPosition().getRow() == enemyGeneral.getPosition().getRow())) {
                    positionalValue = CHARIOT_ALIGNED_GENERAL;
                } else {
                    positionalValue = piece.getInitialValue();
                }
                break;
            case HORSE:
                if (currentPlayerColor.equals(Color.RED) && piece.getPosition().getRow() <= RED_RIVER_ROW) {
                    positionalValue = HORSE_OVER_RIVER;
                } else if (currentPlayerColor.equals(Color.BLACK)
                            && piece.getPosition().getRow() >= BLACK_RIVER_ROW) {
                    positionalValue = HORSE_OVER_RIVER;
                } else {
                    positionalValue = piece.getInitialValue();
                }
                positionalValue += MAX_PIECES - board.getPieces().size();
                positionalValue = Math.min(positionalValue, HORSE_UPPER_LIMIT);
                break;
            case ELEPHANT:
            case ADVISOR:
                if (currentPlayerColor.equals(Color.RED) && piece.getPosition().getRow() != RED_BOTTOM_ROW) {
                    positionalValue = ELE_ADV_OUT_BOTTOM;
                } else if (currentPlayerColor.equals(Color.BLACK)
                            && piece.getPosition().getRow() != BLACK_BOTTOM_ROW) {
                    positionalValue = ELE_ADV_OUT_BOTTOM;
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
     * Returns the threatening multiplier due to piece type.
     * 
     * @param type the piece type
     * @return the threatening multiplier
     */
    private double getThreateningMultiplier(final PieceType type) {
        switch (type) {
            case GENERAL:
                return THREATENING_GENERAL;
            case CHARIOT:
                return THREATENING_CHARIOT;
            case CANNON:
                return THREATENING_CANNON;
            case HORSE:
                return THREATENING_HORSE;
            default:
                return THREATENING_DEFAULT;
        }
    }
}
