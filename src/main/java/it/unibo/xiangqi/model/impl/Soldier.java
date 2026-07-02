package it.unibo.xiangqi.model.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
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

        // Forward direction: RED moves toward increasing rows, BLACK toward decreasing rows
        final int forward = isRed ? 1 : -1;
        tryAddMove(moves, board, current, row + forward , col);

        // Sideways moves are only allowed after crossing the river
        if (hasCrossedRiver(row, isRed)) {
            tryAddMove(moves, board, current, row, col + 1);
            tryAddMove(moves, board, current, row, col - 1);
        }
        return moves;
    }

    private boolean hasCrossedRiver(final int row, final boolean isRed) {
        return isRed ? row > RIVER_ROW_RED : row < RIVER_ROW_BLACK;
    }

    private void tryAddMove(final List<Move> moves, final Board board,
            final Position from, final int toRow, final int toCol) {
        // Manual bounds check before creating the Position
        if (toRow < 0 || toRow >= Position.ROWS || toCol < 0 || toCol >= Position.COLS) {
            return;
        }

        final Position to = new Position(toRow, toCol);
        final Piece target = board.getPieceAt(to);

        // Target cell must be empty or occupied by an enemy piece
        if (target == null || !target.getOwner().equals(this.getOwner())) {
            moves.add(new MoveImpl(from, to));
        }
    }
}
