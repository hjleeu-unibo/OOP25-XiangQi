package it.unibo.xiangqi.model.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

public class Soldier extends AbstractPiece{

    private static final int VALUE = 10;
    private static final int RIVER_ROW_RED = 4;
    private static final int RIVER_ROW_BLACK = 5;

    protected Soldier(final Player owner,final Position position) {
        super(PieceType.SOLDIER, owner, position, VALUE);
    }

    @Override
    public List<Move> getMoves(final Board board) {
        final List<Move> moves = new ArrayList<>();
        final Position current = getPosition();
        final int row = current.getRow();
        final int col = current.getCol();
        final boolean isRed = getOwner().getColor() == Color.RED;

        final int forward = isRed ? -1 : 1;
        tryAddMove(moves, board, current, row + forward, col);

        if (hasCrossedRiver(row, isRed)) {
            tryAddMove(moves, board, current, row, col + 1);
            tryAddMove(moves, board, current, row, col - 1);
        }
        return moves;
    }

    private boolean hasCrossedRiver(final int row, final boolean isRed) {
        return isRed ? row <= RIVER_ROW_RED : row >= RIVER_ROW_BLACK;
    }
}
