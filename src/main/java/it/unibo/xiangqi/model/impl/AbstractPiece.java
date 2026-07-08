package it.unibo.xiangqi.model.impl;

import java.util.List;

import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

/**
 * Base class for all Xiangqi pieces.
 * Contains the common state (type, owner, position) and shared logic.
 * Each concrete piece only needs to implement getMoves().
 */
public abstract class AbstractPiece implements Piece {
    private final PieceType type;
    private final Player owner;
    private Position position;
    private final int initialvalue;
    private int currentvalue;

    /**
     * @param type          the type of this piece
     * @param owner         the player who owns this piece
     * @param position      the starting position
     * @param initialvalue  the initial value of this piece
     */
    protected AbstractPiece(final PieceType type, 
                            final Player owner, 
                            final Position position, 
                            final int initialvalue) {
        this.type = type;
        this.owner = owner;
        this.position = position;
        this.initialvalue = initialvalue;
        this.currentvalue = initialvalue;
    }

    @Override
    public final PieceType getType() {
        return type;
    }

    @Override
    public final Player getOwner() {
        return owner;
    }

    @Override
    public final Position getPosition() {
        return position;
    }

    @Override
    public final void setPosition(final Position position) {
        this.position = position;
    }

    @Override
    public abstract List<Move> getMoves(Board board);

    @Override
    public abstract Boolean isDefensor();

    @Override
    public final int getInitialValue() {
        return this.initialvalue;
    }

    @Override
    public final int getCurrentValue() {
        return this.currentvalue;
    }

    @Override
    public final void setValue(final int value) {
        this.currentvalue = value;
    }

    @Override
    public final String toString() {
        return this.type + "(" + this.owner + ")@" + this.position + "[" + this.currentvalue + "]";
    }

    /**
     * Tries to add a move.
     * 
     * @param moves the list to add the move to if valid
     * @param board the current board state
     * @param from the starting position of the piece
     * @param toRow the destination row
     * @param toCol the destination column
     * @return true only if the destination was empty.
     *         false if out of bounds, occupied by friendly piece or a capture occurred (stop)
     */
    protected boolean tryAddMove(final List<Move> moves, final Board board, 
        final Position from, final int toRow, final int toCol) {
        if (toRow < 0 || toRow >= Position.ROWS || toCol < 0 || toCol >= Position.COLS) {
            return false;                                  // out of bounds
        }
        final Position to = new Position(toRow, toCol);
        final Piece target = board.getPieceAt(to);
        if (target == null) {
            moves.add(new Move(from, to));
            return true;                                   // empty → keep sliding
        } else if (!target.getOwner().equals(this.getOwner())) {
            moves.add(new Move(from, to));
            return false;                                  // capture → stop
        }
        return false;                                      // friendly → stop
    }
}
