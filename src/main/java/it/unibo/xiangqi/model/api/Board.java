package it.unibo.xiangqi.model.api;

import java.util.List;

public interface Board {
    List<Piece> getPieces(); 
    Piece getPieceAt(Position pos); 
}