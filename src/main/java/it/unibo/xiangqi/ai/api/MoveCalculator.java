package it.unibo.xiangqi.ai.api;

import it.unibo.xiangqi.model.api.GameModel;
import it.unibo.xiangqi.model.api.GameState;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Player;

public interface MoveCalculator {
    /**
     * Calculate the total score of the board for the current player.
     * The score is the sum of the current values of the player's pieces.
     * @param gameState current game state
     * @param currentPlayer the current player
     * @return board score
     */
    public int calculateBoardScore(final GameState gameState, final Player currentPlayer);

    /**
     * Return the best move for the current player.
     * @param gameModel Game model.
     * @return the best move
     */
    public Move getBestMove(final GameModel gameModel);
}