package it.unibo.xiangqi.view.test;

import java.util.List;

import it.unibo.xiangqi.common.Position;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Piece;

public class FakeBoard implements Board{

    private List<Piece> pieces;

    public FakeBoard(List<Piece> pieces){
        this.pieces = pieces; 
    }

    @Override
    public List<Piece> getPieces() {
        return this.pieces; 
    }

    @Override
    public Piece getPieceAt(Position pos) {

        Piece piece = null; 

        for (Piece p : this.pieces){
            if(p.getPosition().equals(pos))
                piece = p; 
        }

        return piece; 
    } 
    
}
