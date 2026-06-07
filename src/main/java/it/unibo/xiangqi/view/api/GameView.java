package it.unibo.xiangqi.view.api;

import java.util.List;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Position;

public interface GameView {
    void updateBoard(Board board); 
    void setPlayerEnabled(Color c); 
    void setPlayerDisabled(Color c); 
    void setHintButtonEnabled(); 
    void setHintButtonDisabled(); 
    void highlightCells(List<Position> cells); 
    void showSuggestedMove(Move move); 
}