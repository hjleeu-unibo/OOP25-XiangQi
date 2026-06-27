package it.unibo.xiangqi.model.api;

import java.util.List;
import java.util.Objects;

import it.unibo.xiangqi.common.Position;
import it.unibo.xiangqi.model.impl.BoardImpl;

public interface Board {
    static Board createBoard(List<Piece> pieces){
        Objects.requireNonNull(pieces); 
        return new BoardImpl(pieces); 
    } 
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
    List<Piece> getPieces(); 
    Piece getPieceAt(Position position);  
    void deletePiece(Piece piece); 
    void addPiece(Piece piece); 
}