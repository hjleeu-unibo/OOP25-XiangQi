package it.unibo.xiangqi.view.api;

import java.util.List;

import it.unibo.xiangqi.common.Color;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.model.api.Board;

/**
 * Represents the main view of the game.
 *
 * <p>This interface provides the operations needed to update the graphical
 * representation of the game and to handle user interactions.</p>
 */
public interface GameView {
    /**
     * Updates the displayed game board.
     *
     * @param board the new board configuration to display
     * @throws NullPointerException if board is null
     */
    void updateBoard(Board board);
    
    /**
     * Enables the interaction with pieces owned by the specified player.
     *
     * @param c the color of the player to enable
     * @throws NullPointerException if {@code c} is null
     */
    void setPlayerEnabled(Color c); 

    /**
     * Disables the interaction with pieces owned by the specified player.
     *
     * @param c the color of the player to disable
     * @throws NullPointerException if {@code c} is null
     */
    void setPlayerDisabled(Color c); 

    /**
     * Enables the hint button.
     */
    void setHintButtonEnabled(); 

    /**
     * Disables the hint button.
     */
    void setHintButtonDisabled(); 

    /**
     * Highlights the specified board cells.
     * It also disables all board cells except the highlighted ones
     * and the last cell selected by the user.
     *
     * @param positions the list of positions to highlight
     * @throws NullPointerException if {@code positions} is null
     */
    void highlightCells(List<Position> positions); 

    /**
     * Displays a suggested move.
     *
     * @param move the move to display
     * @throws NullPointerException if {@code move} is null
     */
    void showSuggestedMove(Move move); 

    /**
     * Sets the input handler used to manage user actions.
     *
     * @param inputHandler the input handler associated with this view
     * @throws NullPointerException if {@code inputHandler} is null
     */
    void setInputHandler(InputHandler inputHandler);

    /**
     * Displays the check notification.
     */
    void showCheck(); 

    /**
     * Displays the draw notification.
     */
    void showDraw(); 

    /**
     * Removes the check notification.
     */
    void resetCheck(); 

    /**
     * Displays the winner notification.
     *
     * @param color the color of the winning player
     */
    void showWinner(Color color);
    void showGamePanel();
}