package it.unibo.xiangqi.view.test;

import java.util.List;

import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.view.api.GameView;

/**
 * test class.
 * 
 * @hidden
 */
public class FakeInputHandler implements InputHandler {

    private GameView view; 

    public FakeInputHandler(final GameView view) {
        this.view = view;
    }

    @Override
    public void onSelect(final Position position) {
        view.highlightCells(List.of(new Position(3, 3), new Position(6, 7)));
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
