package it.unibo.xiangqi.fake;

import java.util.List;

import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.view.api.GameView;
import it.unibo.xiangqi.view.impl.GameViewImpl;

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
        this.view = new GameViewImpl();
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

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onHint() {

    }
}
