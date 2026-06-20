package it.unibo.xiangqi.ai.api;

import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameState;
import it.unibo.xiangqi.model.api.Move;

public interface MoveCalculator {
    /**
     * Calculate the total score of the board for the current player.
     * The score is the sum of the current values of the player's pieces.
     * @param gameState current game state
     * @return board score
     */
    public int calculateBoardScore(GameState gameState);

    /**
     * Return the best move for the current player.
     * @param board Game Board.
     * @return the best move
     */
    public Move getBestMove(Board board);
}