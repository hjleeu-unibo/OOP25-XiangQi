package it.unibo.xiangqi.ai.api;

import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameState;
import it.unibo.xiangqi.model.api.Move;

public interface MoveCalculator {
    /**
     * Calculate the score of the board at that moment.
     * @ gm State of the board, as a simulation of the game.
     * @return Score of the board.
     */
    public double calculateBoardScore(GameState gm);

    /**
     * Return the best Move for the current player.
     * @param board Game Board.
     * @return The Best Move.
     */
    public Move getBestMove(Board board);
}