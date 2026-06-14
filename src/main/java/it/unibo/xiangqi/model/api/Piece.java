package it.unibo.xiangqi.model.api;

import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.common.api.Position;

public interface Piece {
    PieceType getType(); 
    Player getOwner(); 
    Position getPosition(); 
}