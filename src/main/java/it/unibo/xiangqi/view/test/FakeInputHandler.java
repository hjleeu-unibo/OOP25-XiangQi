package it.unibo.xiangqi.view.test;

import java.util.List;

import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.common.api.Move;
import it.unibo.xiangqi.common.api.Position;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.view.api.GameView;

public class FakeInputHandler implements InputHandler{

    private GameView view; 

    public FakeInputHandler(GameView view) {
        this.view = view;
    }

    @Override
    public void onSelect(Position position) {
        view.highlightCells(List.of(new Position(3,3), new Position(6,7)));
    }

    @Override
    public void onMove(Move move) {

    }

    @Override
    public void onStart(GameModeType mode) {

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
