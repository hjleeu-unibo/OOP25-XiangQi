package it.unibo.xiangqi.model.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

/**
 * Rapresenting the elephant piece in Xiangqi.
 * Elephant
 */
public class Elephant extends AbstractPiece {
    private static final int VALUE = 20;
    
    // river boundary: red stays on rows 5-9, black stays on rows 0-4
    private static final int RIVER_ROW_RED = 4;
    private static final int RIVER_ROW_BLACK = 5;

    // 4 diagonal moves of magnitude 2; the eye is exactly halfway (dir/2)
    private static final int[][] DIRECTIONS = {{2, 2}, {2, -2}, {-2, 2}, {-2, -2}};

    protected Elephant(final Player owner, final Position position) {
        super(PieceType.ELEPHANT, owner, position, VALUE);
    }

    @Override
    public Boolean isDefensor() {
        return true;    // Elephant is a defensive piece protecting the General
    }

    @Override
    public List<Move> getMoves(final Board board) {
        final List<Move> moves = new ArrayList<>();
        final Position current = getPosition();
        final int row = current.getRow();
        final int col = current.getCol();
        final boolean isRed = getOwner().getColor() == Color.RED;

        for (final int[] dir : DIRECTIONS) {
            final int toRow = row + dir[0];
            final int toCol = col + dir[1];

            // the eye is the diagonal cell halfway between start and destination
            final int eyeRow = row + dir[0] / 2;
            final int eyeCol = col + dir[1] / 2;

            // the elephant must not cross the river
            // the eye must be on the board and unoccupied (no blocking piece)
            if ((isRed ? toRow > RIVER_ROW_RED : toRow < RIVER_ROW_BLACK)
                && eyeRow >= 0 && eyeRow < Position.ROWS
                && eyeCol >= 0 && eyeCol < Position.COLS
                && board.getPieceAt(new Position(eyeRow, eyeCol)) == null) { // there isn't piece in the eye cell
                    tryAddMove(moves, board, current, toRow, toCol);
                }
        }
        return moves;
    }
}
