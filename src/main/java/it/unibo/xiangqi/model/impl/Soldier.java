package it.unibo.xiangqi.model.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

public class Soldier extends AbstractPiece{

    protected Soldier(PieceType type, Player owner, Position position, double value) {
        super(type, owner, position, value);
    }

    @Override
    public Piece createPiece(PieceType type, Player owner, Position startingPosition) {
        return new Soldier(type, owner, startingPosition, 10.0);
    }

    @Override
    public List<Move> getMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        return moves;
    }
}
