package it.unibo.xiangqi.model.api;

/**
 * temp
 * 
 * @hidden
 */
public interface Move {

    /**
     * Returns the starting position of the move.
     * 
     * @return the position where the piece moves from
     */
    public Position getFrom();

    /**
     * Returns the destination position of the move.
     * 
     * @return the position where the piece moves to
     */
    public Position getTo();
}