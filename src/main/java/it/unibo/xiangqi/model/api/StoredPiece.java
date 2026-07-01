package it.unibo.xiangqi.model.api;

import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.common.api.Color;

public record StoredPiece(PieceType type, Color color, Position position) {
}
