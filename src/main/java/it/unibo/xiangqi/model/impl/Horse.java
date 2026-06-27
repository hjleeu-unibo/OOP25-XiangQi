package it.unibo.xiangqi.model.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

public class Horse extends AbstractPiece {

    private static final int VALUE = 40;
    private static final int[][] DIRECTIONS = {
        {-2,  1}, {-2, -1},  // up 2
        { 2,  1}, { 2, -1},  // down 2
        { 1,  2}, {-1,  2},  // right 2
        { 1, -2}, {-1, -2},  // left 2
    };


    protected Horse(Player owner, Position position) {
        super(PieceType.HORSE, owner, position, VALUE);
    }

    @Override
    public List<Move> getMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        final Position current = getPosition();
        final int row = current.getRow();
        final int col = current.getCol();

        for (final int[] dir : DIRECTIONS) {
            final int toRow = row + dir[0];
            final int toCol = col + dir[1];
            final int legRow = dir[0] / 2;
            final int legCol = dir[1] / 2;

            if(legRow >= 0 && legRow < Position.ROWS
                && legCol >= 0 && legCol < Position.COLS
                && board.getPieceAt(new Position(legRow, legCol)) == null) {
                    tryAddMove(moves, board, current, toRow, toCol);
                }
        }
        return moves;
    }
}
