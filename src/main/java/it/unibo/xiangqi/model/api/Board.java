package it.unibo.xiangqi.model.api;

import java.util.List;
import java.util.Objects;

import it.unibo.xiangqi.common.Position;
import it.unibo.xiangqi.model.impl.BoardImpl;

/**
 * Represents the Xiangqi game board and provides methods to manage
 * the pieces placed on it.
 */
public interface Board {
    /**
     * Creates a new board containing the specified pieces.
     *
     * @param pieces the initial pieces to place on the board
     * @return a new board instance
     * @throws NullPointerException if {@code pieces} is {@code null}
     */
    static Board createBoard(List<Piece> pieces){
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
    static boolean isValidPosition(Position position){
        Objects.requireNonNull(position); 
        int col = position.getCol(); 
        int row = position.getRow(); 
        if(row >= 0 && row < 10 && col >= 0 && col < 9){
            return true; 
        }else{
            return false; 
        }
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
}