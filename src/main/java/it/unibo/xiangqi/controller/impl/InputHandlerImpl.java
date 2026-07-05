package it.unibo.xiangqi.controller.impl;

import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.controller.api.GameController;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Position;

/**
 * Implementation of the Input Handler.
 * 
 * Receives user interactions from the view and delegates the
 * corresponding actions to the appropriate components.
 */
public final class InputHandlerImpl implements InputHandler{

    private final GameController gameController;

    /**
     * Builds a new InputHandler with the given collaborators.
     * 
     * @param gameController the controller coordinating the game flow
     */
    public InputHandlerImpl(final GameController gameController) {

        this.gameController = gameController;
  
    }

    @Override
    public void onStart(final GameModeType mode) {
        gameController.start(mode);
    }

    @Override
    public void onSelect(final Position position) {
        gameController.select(position);
    }

    @Override
    public void onMove(final Move move) {
        gameController.applyMove(move);
    }

    @Override
    public void onResume() {
        gameController.load();
    }

    @Override
    public void onHint() {
        gameController.hint();
    }

    @Override
    public void onExit() {
        gameController.save();
    }

}
