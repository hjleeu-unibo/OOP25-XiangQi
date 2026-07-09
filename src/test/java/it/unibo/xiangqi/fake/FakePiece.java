package it.unibo.xiangqi.fake;

import java.util.List;
import java.util.Objects;

import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

/**
 * A simulation of the Piece class.
 * FakePiece
 */
public final class FakePiece implements Piece {
    private final PieceType type;
    private final Player owner;
    private final Position position;

    /**
     * Constructor.
     * 
     * @param type the piece type
     * @param owner the owner
     * @param position the position
     */
    public FakePiece(final PieceType type, final Player owner, final Position position) {
        this.type = type;
        this.owner = owner;
        this.position = position;
    }

    @Override
    public PieceType getType() {
        return this.type; 
    }

    @Override
    public Player getOwner() {
        return this.owner; 
    }

    @Override
    public Position getPosition() {
        return this.position; 
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof FakePiece)) {
            return false;
        }

        final FakePiece other = (FakePiece) obj; 

        return Objects.equals(this.type, other.type)
            && Objects.equals(this.owner, other.owner)
            && Objects.equals(this.position, other.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, owner, position);
    }

    @Override
    public void setPosition(final Position position) {
        throw new UnsupportedOperationException("Unimplemented method 'setPosition'");
    }

    @Override
    public List<Move> getMoves(final Board board) {
        throw new UnsupportedOperationException("Unimplemented method 'getMoves'");
    }

    @Override
    public boolean isDefensor() {
        throw new UnsupportedOperationException("Unimplemented method 'isDefensor'");
    }

    @Override
    public int getInitialValue() {
        throw new UnsupportedOperationException("Unimplemented method 'getInitialValue'");
    }

    @Override
    public int getCurrentValue() {
        throw new UnsupportedOperationException("Unimplemented method 'getCurrentValue'");
    }

    @Override
    public void setValue(final int value) {
        throw new UnsupportedOperationException("Unimplemented method 'setValue'");
    }
}
