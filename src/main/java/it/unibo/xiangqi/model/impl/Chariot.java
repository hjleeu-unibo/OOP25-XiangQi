package it.unibo.xiangqi.model.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

/**
 * Rapresenting the chariot piece in Xiangqi.
 * Chariot
 */
public final class Chariot extends AbstractPiece {

    private static final int VALUE = 90;

    // Up, down, left, right
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    /**
     * Constructor.
     * 
     * @param owner the owner
     * @param position the initial position
     */
    protected Chariot(final Player owner, final Position position) {
        super(PieceType.CHARIOT, owner, position, VALUE);
    }

    @Override
    public List<Move> getMoves(final Board board) {
        final List<Move> moves = new ArrayList<>();
        final Position current = this.getPosition();

        // Soldiers always move one step forward
        for (final int[] dir : DIRECTIONS) {
            int row = current.getRow() + dir[0];
            int col = current.getCol() + dir[1];

            /*
            Continue moving in the same direction until blocked,
            when the block is occupied by Enemy -> capture and stop
            when the block is occupiede by Friendly -> stop
            */ 
            while (tryAddMove(moves, board, current, row, col)) {
                row += dir[0];
                col += dir[1];
            }
        }
        return moves;
    }

    @Override
    public boolean isDefensor() {
        return false;
    }
}
