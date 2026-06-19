package it.unibo.xiangqi.model.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

public class Advisor extends AbstractPiece {

    private static final int VALUE = 20;

    protected Advisor(Player owner, Position position) {
        super(PieceType.ADVISOR, owner, position, VALUE);
    }

    @Override
    public Piece createPiece(PieceType type, Player owner, Position startingPosition) {
       return null;
    }

    @Override
    public List<Move> getMoves(Board board) {
       List<Move> moves = new ArrayList<>();
       return moves;
    }

}
