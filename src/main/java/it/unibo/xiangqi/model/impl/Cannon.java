package it.unibo.xiangqi.model.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

public class Cannon extends AbstractPiece{

    private static final int VALUE = 45;

    // Up, down, left, right
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    protected Cannon(Player owner, Position position) {
        super(PieceType.CANNON, owner, position, VALUE);
    }

    @Override
    public List<Move> getMoves(final Board board) {
        final List<Move> moves = new ArrayList<>();
        final Position current = getPosition();

        for (final int[] dir : DIRECTIONS) {
            int row = current.getRow() + dir[0];
            int col = current.getCol() + dir[1];
            boolean screenFound = false; // has the cannon platform been found?
        }
        return moves;
    }
}
