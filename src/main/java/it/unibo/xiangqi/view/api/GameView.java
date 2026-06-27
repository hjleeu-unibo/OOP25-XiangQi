package it.unibo.xiangqi.view.api;

import java.util.List;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.model.api.Board;

public interface GameView {
    void updateBoard(Board board); 
    void setPlayerEnabled(Color c); 
    void setPlayerDisabled(Color c); 
    void setHintButtonEnabled(); 
    void setHintButtonDisabled(); 
    void highlightCells(List<Position> cells); 
    void showSuggestedMove(Move move); 
    void setInputHandler(InputHandler inputHandler);
    void showCheck(); 
    void resetCheck(); 
    void showWinner(Color color);
    void showGamePanel();

    /* Haojie-Liu | Game timer section. */
    /** Show the updated timer on view.
     * @param player the player refers to
     * @param turnRemaining seconds remaining for the current turn
     * @param gameRemaining seconds remaing for the entire game
     */
    void updateTimer(Player player, long turnRemaining, long gameRemaining);

    /**
     * Show a message indicating that the time is expired, so the player lost.
     * @param player the player refers to
     */
    void showExpiredTime(Player player);
}