package it.unibo.xiangqi.controller.api;

import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Position;

/**
 * Manages the overall game flow and controls the progression of
 * the match from start to finish.
 * 
 * The controller does not store the game state, which is entirely 
 * managed by the model
 */
public interface GameController {

    /**
     * Starts a new game with the selected mode by rendering the initial 
     * board and initiating the first turn.
     * 
     * @param mode specifies the game mode (PVP or PVE)
     */
    void start(GameModeType mode);

    /**
     * Handles the selection of a cell on the board and asks the 
     * view to highlight them for the user
     * 
     * @param position the cell selected by the user
     */

    void select(Position position);

    /**
     * Concludes the current human player's turn: applies the given move,
     * closes the interaction on the view and advances the game.
     * 
     * @param move the move chosen by the human player 
     */
    void applyMove(Move move);

    /**
     * Saves the current game state to persistent storage.
     */
    void save();

    /**
     * Reports whether a previously saved game is avaible 
     * to be resumed.
     * 
     * @return true if a saved game exists and can be restored
     */
    boolean isResumeAvailable();

    /**
     * Loads a previously saved game state from persistent storage
     */
    void load();

    /**
     * Provides a suggested move for the current human player.
     */
    void hint();


    
}