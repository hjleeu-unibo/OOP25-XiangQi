package it.unibo.xiangqi.view.test;

import java.util.List;
import java.util.Objects;

import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.view.api.GameView;

/**
 * Simulation of the InputHandler class.
 * FakeInputHandler
 */
public final class FakeInputHandler implements InputHandler {
    private static final int TEST_POSITION_ROW1 = 3;
    private static final int TEST_POSITION_COL1 = 3;
    private static final int TEST_POSITION_ROW2 = 6;
    private static final int TEST_POSITION_COL2 = 7;

    private final GameView view; 

    /**
     * Constructor.
     * 
     * @param view the view of the game
     */
    public FakeInputHandler(final GameView view) {
        this.view = Objects.requireNonNull(view);
    }

    @Override
    public void onSelect(final Position position) {
        view.highlightCells(List.of(new Position(TEST_POSITION_ROW1, TEST_POSITION_COL1),
                                    new Position(TEST_POSITION_ROW2, TEST_POSITION_COL2)));
    }

    @Override
    public void onMove(final Move move) {

    }

    @Override
    public void onStart(final GameModeType mode) {
        view.showGamePanel(); 
    }

    @Override
    public void onExit() {
        System.out.println("onExit");
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onHint() {

    }
}
