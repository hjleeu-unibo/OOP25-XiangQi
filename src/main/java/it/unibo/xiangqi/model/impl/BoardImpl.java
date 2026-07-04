package it.unibo.xiangqi.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Position;

/**
 * Concrete implementation of the {@link Board} interface.
 * 
 * This class stores the pieces currently placed on the board
 * and provides operations to access and modify them.
 */
public class BoardImpl implements Board{

    private List<Piece> pieces; 

    /**
     * Creates a new board containing the specified pieces.
     *
     * @param pieces the initial list of pieces on the board
     * @throws NullPointerException if {@code pieces} is {@code null}
     */
    public BoardImpl(List<Piece> pieces) {
        Objects.requireNonNull(pieces);
        this.pieces = new ArrayList<>(pieces);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Piece> getPieces() {
        return new ArrayList<>(this.pieces);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Piece getPieceAt(Position position) {
        Objects.requireNonNull(position); 
        Piece piece = null; 
        for (Piece p : this.pieces){
            if(p.getPosition().equals(position)){
                piece = p; 
            }
        }
        return piece; 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deletePiece(Piece piece) {
        Objects.requireNonNull(piece); 
        this.pieces.remove(piece); 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPiece(Piece piece) {
        Objects.requireNonNull(piece);
        this.pieces.add(piece); 
    }
    
}
