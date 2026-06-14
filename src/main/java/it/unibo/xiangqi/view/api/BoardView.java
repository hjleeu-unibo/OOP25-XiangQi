package it.unibo.xiangqi.view.api;

import java.util.List;

import it.unibo.xiangqi.common.api.Position;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;

public interface BoardView {
    void updateBoard(Board board);
    void highlightCells(List<Position> positions);
    void showSuggestedMove(Move move);
}
