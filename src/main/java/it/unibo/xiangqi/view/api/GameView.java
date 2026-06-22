package it.unibo.xiangqi.view.api;

import java.util.List;

import it.unibo.xiangqi.common.Color;
import it.unibo.xiangqi.common.Move;
import it.unibo.xiangqi.common.Position;
import it.unibo.xiangqi.controller.api.InputHandler;
import it.unibo.xiangqi.model.api.Board;

public interface GameView {
    void updateBoard(Board board); 
    void setPlayerEnabled(Color c); 
    void setPlayerDisabled(Color c); 
    void setHintButtonEnabled(); 
    void setHintButtonDisabled(); 
    void highlightCells(List<Position> cells); 
    void showSuggestedMove(Move move); 
    void setInputHandler(InputHandler inputHandler); 
}