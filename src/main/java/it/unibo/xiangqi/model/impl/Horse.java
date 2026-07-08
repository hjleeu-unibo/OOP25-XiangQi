package it.unibo.xiangqi.model.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

/**
 * Rapresenting the horse piece in Xiangqi.
 * Horse
 */
public final class Horse extends AbstractPiece {
    private static final int VALUE = 40;
    // 8 possible L-shaped moves: the axis with magnitude 2 is the straight step (leg),
    // the axis with magnitude 1 is the diagonal step.
    private static final int[][] DIRECTIONS = {
        {-2, +1}, {-2, -1}, // up 2
        {+2, +1}, {+2, -1}, // down 2
        {+1, +2}, {-1, +2}, // right 2
        {+1, +2}, {-1, -2}, // left 2
    };

    /**
     * Constructor.
     * 
     * @param owner the owner
     * @param position initial position
     */
    protected Horse(final Player owner, final Position position) {
        super(PieceType.HORSE, owner, position, VALUE);
    }

    @Override
    public List<Move> getMoves(final Board board) {
        final List<Move> moves = new ArrayList<>();
        final Position current = getPosition();
        final int row = current.getRow();
        final int col = current.getCol();

        for (final int[] dir : DIRECTIONS) {
            final int toRow = row + dir[0];
            final int toCol = col + dir[1];
            // the leg is one step along the axis with magnitude 2 (the straight step),
            // the other axis contributes 0 — mirrors Elephant's eye check with dir/2
            final int legRow = row + (Math.abs(dir[0]) == 2 ? dir[0] / 2 : 0);
            final int legCol = col + (Math.abs(dir[1]) == 2 ? dir[1] / 2 : 0);

            // leg must be on the board and unoccupied (no blocking piece)
            if (legRow >= 0 && legRow < Position.ROWS
                && legCol >= 0 && legCol < Position.COLS
                && board.getPieceAt(new Position(legRow, legCol)) == null) {
                    tryAddMove(moves, board, current, toRow, toCol);
                }
        }
        return moves;
    }

    @Override
    public boolean isDefensor() {
        return false;
    }
}
