package it.unibo.xiangqi.model.impl;

import java.util.List;

import it.unibo.xiangqi.common.Position;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Piece;

public class BoardImpl implements Board{

    private List<Piece> pieces; 

    public BoardImpl(List<Piece> pieces) {
        this.pieces = pieces;
    }

    @Override
    public List<Piece> getPieces() {
        return this.pieces; 
    }

    @Override
    public Piece getPieceAt(Position position) {
        Piece piece = null; 
        for (Piece p : this.pieces){
            if(p.getPosition().equals(position)){
                piece = p; 
            }
        }
        return piece; 
    }

    @Override
    public void deletePiece(Piece piece) {
        this.pieces.remove(piece); 
    }
    
}
