package it.unibo.xiangqi.model.api;

/**
 * The class rapresenting a move in the game.
 * Move
 */
public class Move {
    private final Position from; 
    private final Position to;
    /**
     * Constructor.
     * 
     * @param from the initial position
     * @param to the final position
     */
    public Move(final Position from, final Position to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the starting position of the move.
     * 
     * @return the position where the piece moves from
     */
    public Position getFrom() {
        return from;
    }

    /**
     * Returns the destination position of the move.
     * 
     * @return the position where the piece moves to
     */
    public Position getTo() {
        return to;
    } 
}
