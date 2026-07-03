package it.unibo.xiangqi.model.api;

/**
 * temp
 * 
 * @hidden
 */
public class Move {
    
    private Position from; 
    private Position to;
    
    public Move(Position from, Position to) {
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