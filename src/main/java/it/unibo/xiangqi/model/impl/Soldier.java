package it.unibo.xiangqi.model.impl;

import java.util.List;

import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

public class Soldier extends AbstractPiece{

    protected Soldier(PieceType type, Player owner, Position position, int value) {
        super(type, owner, position, value);
        //TODO Auto-generated constructor stub
    }

    @Override
    public Piece createPiece(PieceType type, Player owner, Position startingPosition) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createPiece'");
    }

    @Override
    public List<Move> getMoves(Board board) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMoves'");
    }

}
