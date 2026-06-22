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
     * @param type     the type of this piece
     * @param owner    the player who owns this piece
     * @param position the starting position
     */
    protected AbstractPiece(final PieceType type, final Player owner, final Position position, final int initialvalue) {
        this.type = type;
        this.owner = owner;
        this.position = position;
        this.initialvalue = initialvalue;
        this.currentvalue = initialvalue;
    }

    @Override
    public PieceType getType() {
        return type;
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(final Position position) {
        this.position = position;
    }

    @Override
    public abstract List<Move> getMoves(Board board);

    @Override
    public Boolean isDefensor() {
        return false;
    }

    @Override
    public int getInitialValue() {
        return this.initialvalue;
    }
    
    @Override
    public int getCurrentValue() {
        return this.currentvalue;
    }

    @Override
    public void setValue(final int value) {
        this.currentvalue = value;
    }

    @Override
    public String toString() {
        return this.type + "(" + this.owner + ")@" + this.position + "[" + this.currentvalue + "]";
    }


    //Tries to add a move. Returns true only if the cell was empty.
    protected boolean tryAddMove(final List<Move> moves, final Board board, 
        final Position from, final int toRow, final int toCol) {
    if (toRow < 0 || toRow >= Position.ROWS || toCol < 0 || toCol >= Position.COLS) {
        return false;                                  // out of bounds
    }
    final Position to = new Position(toRow, toCol);
    final Piece target = board.getPieceAt(to);
    if (target == null) {
        moves.add(new MoveImpl(from, to));
        return true;                                   // empty → keep sliding
    } else if (!target.getOwner().equals(this.getOwner())) {
        moves.add(new MoveImpl(from, to));
        return false;                                  // capture → stop
    }
    return false;                                      // friendly → stop
}
}