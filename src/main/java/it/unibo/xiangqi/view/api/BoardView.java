package it.unibo.xiangqi.view.api;

import java.util.List;

import it.unibo.xiangqi.common.Move;
import it.unibo.xiangqi.common.Position;
import it.unibo.xiangqi.model.api.Board;

public interface BoardView {
    void updateBoard(Board board);
    void highlightCells(List<Position> positions);
    void showSuggestedMove(Move move);
}
