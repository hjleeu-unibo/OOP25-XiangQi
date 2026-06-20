package it.unibo.xiangqi.ai.impl;

/**
 * LOGIC OF THIS SYSTEM | HOW THE BOARD SCORES ARE CALCULATED:
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
 *  - CANNON    ->  when its position is on the same line or column of the general, its values changes to 50,
 *                  but this real value is: own_value (45 -> 50) -> 1 per dead piece (of all players).
 *                  The variable factor is because it became useless when it has less "stepping stones".
 *                  However, its minimum value is 4.0 -> max(45/50 - x, 40).
 *  - CHARIOT   ->  when its position is ont he same line or column of the enemy's general,
 *                  its value changes to 100.
 *  - HORSE     ->  over the river, its value changes to 50.
 *                  The horse became more useful when there are less "obstacles" (pieces) on the board.
 *                  The formula is: own_value (40 -> 50) + 1 per dead piece (of all players).
 *                  The maximum value is 65 -> min(40/50 + x, 65).
 *  - ELEPHANT  ->  out of its initial position, its value changes to 30.
 *  - ADVISOR   ->  out of its initial position, its value changes to 30.
 *  - GENERAL   ->  its value will not be changed.
 * 
 * Above, these are the value of each piece during the game.
 * But they can get some extra value:
 *  - when they are protecting some ally pieces: own_value + ally_pieces_values
 *  - when they are threatening some enemy pieces: own_value + 1.5 x enemy_pieces_value
 * With these logics, this system will prefer attack instead of protect, make the game flow faster.
 */

import java.util.ArrayList;
import java.util.List;

import it.unibo.xiangqi.ai.api.MoveCalculator;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameState;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.RuleEngine;

public class MoveCalculatorImpl implements MoveCalculator {
    private final RuleEngine ruleEngine;

    public MoveCalculatorImpl(final RuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    @Override
    public int calculateBoardScore(GameState gameState) {
        final Board board = gameState.getBoard();
        final Player currentPlayer = gameState.getCurrentPlayer();
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
    public Move getBestMove(Board board) {
        return null;
    }

    /**
     * Update the value of a piece due to the state of the game.
     * @param piece the piece to update
     * @param board the game board
     */
    private void updatePieceValue(Piece piece, Board board) {
        int newValue = 0;
        Player currentPlayer = board.getCurrentPlayer();
        List<Piece> enemyPieces = new ArrayList<>();

        for (Piece p : board.getPieces()) {
            if (!p.getOwner().equals(currentPlayer)) {
                enemyPieces.add(p);
            }
        }

        /* THREATENING PIECES. */
        for (Move move : ruleEngine.getLegalMoves(piece, board)) {
            Piece capturedPiece = board.getPieceAt(move.getTo());
            boolean isProtected = false;
            if (capturedPiece != null && !capturedPiece.getOwner(currentPlayer)) {
                for (Piece p : enemyPieces) {
                    for (Move m : ruleEngine.getLegalMoves(p, board)) {
                        if (m.getTo() == capturedPiece.getPosition()) {
                            isProtected = true;
                            break;
                        }
                    }
                    if (isProtected) {
                        break;
                    }
                }

                if (!isProtected) {
                    newValue += 15 * capturedPiece.getInitialValue();
                }
            } else if (capturedPiece != null && capturedPiece.getOwner().equals(currentPlayer)) {
                newValue += capturedPiece.getInitialValue();
            }
        }
    }
}