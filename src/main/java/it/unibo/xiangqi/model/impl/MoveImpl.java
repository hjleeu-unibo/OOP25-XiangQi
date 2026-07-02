package it.unibo.xiangqi.model.impl;

import java.util.Objects;

import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Position;

/**
 * Implementation of the Move interface.
 * Represents a single move made by a piece on the Xiangqi board.
 */
public class MoveImpl implements Move {

    private final Position from;
    private final Position to;

    /**
     * Creates a new move.
     *
     * @param from          the starting position
     * @param to            the destination position
     */
    public MoveImpl(final Position from, final Position to) {
        this.from = from;
        this.to = to;
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
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MoveImpl)) return false;
        final MoveImpl other = (MoveImpl) o;
        return this.from.equals(other.from)
            && this.to.equals(other.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return from + " -> " + to;
    }
}