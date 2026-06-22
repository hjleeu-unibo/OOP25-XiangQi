package it.unibo.xiangqi.model.api;

import java.util.List;

import it.unibo.xiangqi.common.Position;

public interface Board {
    List<Piece> getPieces(); 
    Piece getPieceAt(Position pos); 
}