package it.unibo.xiangqi.model.api;

public interface Move {

    /**
     * Returns the starting position of the move.
     * 
     * @return the position where the piece moves from
     */
    Position getFrom();

    /**
     * Returns the destination position of the move.
     * 
     * @return the position where the piece moves to
     */
    Position getTo();

    /**
     * Returns the piece that was captured during this move, if any.
     * 
     * @return the captured piece, or null if no piece was captured
     */
    Piece getCapturedPiece();

    /**
     * Returns the piece that is performing this move.
     *
     * @return the moving piece
     */
    Piece getPiece();

    /**
     * Returns whether this move captures an enemy piece.
     *
     * @return true if a piece is captured, false otherwise
     */
    boolean isCapture();
}