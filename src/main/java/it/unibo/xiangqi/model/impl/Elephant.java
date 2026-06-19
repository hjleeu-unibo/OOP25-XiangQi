package it.unibo.xiangqi.model.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

public class Elephant extends AbstractPiece {

    private static final int VALUE = 20;

    protected Elephant(Player owner, Position position) {
        super(PieceType.ELEPHANT, owner, position, VALUE);
    }

    @Override
    public Boolean isDefensor() {
        return true;
    }

    @Override
    public List<Move> getMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        return moves;
    }

}
