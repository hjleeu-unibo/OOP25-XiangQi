package it.unibo.xiangqi.model.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

public class Advisor extends AbstractPiece {

    private static final int VALUE = 20;
    private static final int PALACE_COL_MIN = 3;
    private static final int PALACE_COL_MAX = 5;
    private static final int PALACE_ROW_RED_MIN = 7;  // red palace: rows 7-9
    private static final int PALACE_ROW_RED_MAX = 9;
    private static final int PALACE_ROW_BLACK_MIN = 0;
    private static final int PALACE_ROW_BLACK_MAX = 2;  // black palace: rows 0-2
    private static final int[][] DIRECTIONS = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

    protected Advisor(Player owner, Position position) {
        super(PieceType.ADVISOR, owner, position, VALUE);
    }

    @Override
    public Boolean isDefensor() {
        return true; // Advisor is a defensive piece protecting the General
    }

    @Override
    public List<Move> getMoves(Board board) {
       List<Move> moves = new ArrayList<>();
       return moves;
    }

}
