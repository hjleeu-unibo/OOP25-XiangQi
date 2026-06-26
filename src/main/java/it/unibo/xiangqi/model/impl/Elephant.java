package it.unibo.xiangqi.model.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

public class Elephant extends AbstractPiece {

    private static final int VALUE = 20;
    private static final int RIVER_ROW_RED = 4;
    private static final int RIVER_ROW_BLACK = 5;
    private static final int[][] DIRECTIONS = {{2, 2}, {2, -2}, {-2, 2}, {-2, -2}};

    protected Elephant(Player owner, Position position) {
        super(PieceType.ELEPHANT, owner, position, VALUE);
    }

    @Override
    public Boolean isDefensor() {
        return true;
    }

    @Override
    public List<Move> getMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        final Position current = getPosition();
        final int row = current.getRow();
        final int col = current.getCol();
        final boolean isRed = getOwner().getColor() == Color.RED;

        for (final int[] dir : DIRECTIONS) {
            final int toRow = row + dir[0];
            final int toCol = col + dir[1];
            final int eyeRow = row + dir[0] / 2;
            final int eyeCol = col + dir[1] / 2;
        }
        return moves;
    }
}
