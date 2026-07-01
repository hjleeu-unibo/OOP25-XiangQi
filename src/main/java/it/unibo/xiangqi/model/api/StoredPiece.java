package it.unibo.xiangqi.model.api;

import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.common.api.Color;

/**
 * Represents the data required to recreate a piece when restoring
 * a previously saved game
 * 
 * @param type the type of the piece
 * @param color the color of the owning player
 * @param position the position of the piece on the board
 */

public record StoredPiece(PieceType type, Color color, Position position){
}
