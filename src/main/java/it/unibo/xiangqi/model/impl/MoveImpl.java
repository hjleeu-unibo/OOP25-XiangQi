package it.unibo.xiangqi.model.impl;

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
    public String toString() {
        return from + " -> " + to;
    }
}