package it.unibo.xiangqi.model.api;

/**
 * Responsible for storing and restoring the state of a game
 */
public interface GameLoader {

    /**
     * Stores the current state of the given game.
     * Any previously stored state is overwritten.
     * 
     * @param gameModel the model whose state is to be stored
     */
    void store(GameModel gameModel);

    /**
     * Restore a previously stored game into the given model.
     * The model is updated in place so that existing references 
     * remain valid after the operation.
     * 
     * @param gameModel the model that is filled with the stored state
     */
    void restore(GameModel gameModel);

    /**
     * Deletes any previously stored game state.
     */
    void discardSave();

    /**
     * Reports whether a stored game exists and can be restored.
     * 
     * @return true if a stored game exists
     */
    boolean hasStoredGame();
    
    
}