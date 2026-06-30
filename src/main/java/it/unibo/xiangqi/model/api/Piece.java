package it.unibo.xiangqi.model.api;

import it.unibo.xiangqi.common.PieceType;
import it.unibo.xiangqi.common.Position;

/**
 * temp
 * 
 * @hidden
 */
public interface Piece {
    PieceType getType(); 
    Player getOwner(); 
    Position getPosition(); 
}