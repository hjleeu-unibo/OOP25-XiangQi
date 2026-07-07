package it.unibo.xiangqi.model.api;

import java.util.List;
import java.util.Objects;

import it.unibo.xiangqi.model.impl.BoardImpl;

/**
 * Represents the Xiangqi game board and provides methods to manage
 * the pieces placed on it.
 */
public interface Board {
    int ROWS = 10;
    int COLS = 9;

    /**
     * Creates a new board containing the specified pieces.
     *
     * @param pieces the initial pieces to place on the board
     * @return a new board instance
     * @throws NullPointerException if {@code pieces} is {@code null}
     */
    static Board createBoard(final List<Piece> pieces) {
        return new BoardImpl(pieces); 
    } 

    /**
     * Checks whether the specified position belongs to the board.
     *
     * @param position the position to validate
     * @return {@code true} if the position is inside the board,
     *         {@code false} otherwise
     * @throws NullPointerException if {@code position} is {@code null}
     */
    static boolean isValidPosition(final Position position) {
        Objects.requireNonNull(position); 
        final int col = position.getCol(); 
        final int row = position.getRow(); 

        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    /**
     * Returns all the pieces currently on the board.
     *
     * @return the list of pieces on the board
     */
    List<Piece> getPieces(); 

    /**
     * Returns the piece located at the specified position.
     *
     * @param position the board position
     * @return the piece at the specified position, or {@code null} if the
     *         position is empty
     * @throws NullPointerException if {@code position} is {@code null}
     */
    Piece getPieceAt(Position position); 

    /**
     * Removes the specified piece from the board.
     *
     * @param piece the piece to remove
     * @throws NullPointerException if {@code piece} is {@code null}
     */ 
    void deletePiece(Piece piece); 

    /**
     * Adds the specified piece to the board.
     *
     * @param piece the piece to add
     * @throws NullPointerException if {@code piece} is {@code null}
     */
    void addPiece(Piece piece);

    /**
     * Returns a new board representing the result of applying the given
     * move. 
     * {@code this} board is not modified: the
     * move is applied to an internal copy, which is returned instead.
     *
     * @param move the move to apply
     * @return the board after the move
     * @throws NullPointerException if {@code move} is {@code null}
     */
    Board afterMove(Move move);
}
