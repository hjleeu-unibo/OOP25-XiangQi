package it.unibo.xiangqi.model.impl;

import java.util.Objects;

import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Position;

/**
 * Implementation of the Move interface.
 * Represents a single move made by a piece on the Xiangqi board.
 */
public class MoveImpl implements Move {

    private final Position from;
    private final Position to;
    private final Piece piece;
    private final Piece capturedPiece;

    /**
     * Creates a new move.
     *
     * @param from          the starting position
     * @param to            the destination position
     * @param piece         the piece that is moving
     * @param capturedPiece the piece that is captured, or null if none
     */
    public MoveImpl(final Position from, final Position to, final Piece piece, final Piece capturedPiece) {
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
    }

    @Override
    public Position getFrom() {
        return from;
    }

    @Override
    public Position getTo() {
        return to;
    }

    @Override
    public Piece getPiece() {
        return piece;
    }

    @Override
    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    @Override
    public boolean isCapture() {
        return capturedPiece != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MoveImpl)) return false;
        final MoveImpl other = (MoveImpl) o;
        return this.from.equals(other.from)
            && this.to.equals(other.to)
            && this.piece.equals(other.piece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, piece);
    }

    @Override
    public String toString() {
        return piece.getType() + ": " + from + " -> " + to
            + (isCapture() ? " captures " + capturedPiece.getType() : "");
    }
}