package it.unibo.xiangqi.controller.api;

import it.unibo.xiangqi.common.api.GameModeType;

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
     * Handles the turn of a bot player by computing the best
     * legal move and advancing the game flow.
     */
    void botTurn();

    /**
     * Handles the turn of a human player by allowing him to
     * select and perform a move
     */
    void playerTurn();

    /**
     * Advances the game to the next turn and updates the game
     * flow accordingly
     */
    void nextTurn();

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
    public boolean isResumeAvailable();

    /**
     * Loads a previously saved game state from persistent storage
     */
    void load();

    /**
     * Provides a suggested move for the current human player.
     */
    void hint();
    
}