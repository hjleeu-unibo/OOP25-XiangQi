package it.unibo.xiangqi.controller.api;

import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Position;

/**
 * Handles user input coming from the view.
 * Act as the bridge between the GUI and the game logic.
 */
public interface InputHandler {

     /**
     * Handles the request to start a new game in the given mode.
     * 
     * @param mode the choosen game mode (PVP or PVE)
     */

    void onStart(GameModeType mode);

    /**
     * Handles the selection of a cell on the board.
     * If the selection is valid, computes and displays the 
     * corresponding legal moves.
     * 
     * @param position the position of the selcted piece
     */
    void onSelect(Position position);

    /**
     * Handles the confirmation of a move by applying it
     * and advancing the game flow.
     * 
     * @param move
     */

    void onMove(Move move);

    /**
     * Handles the request to resume a previously saved game.
     */
    void onResume();

    /**
     * Handles the request for a move suggestion
     */
    void onHint();

    
    /**
     * Handles the request to exit the game.
     */
    void onExit();
}