package it.unibo.xiangqi.model.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

public class General extends AbstractPiece{

    private static final int VALUE = 1000;

    // the palace spans columns 3-5 for both sides
    private static final int PALACE_COL_MIN = 3;
    private static final int PALACE_COL_MAX = 5;

    // red palace: rows 7-9, black palace: rows 0-2
    // the remaining bounds (red max row 9, black min row 0) are covered by tryAddMove
    private static final int PALACE_ROW_RED_MIN = 7;
    private static final int PALACE_ROW_BLACK_MAX = 2;

    // 4 orthogonal moves of magnitude 1 (no diagonal, unlike Advisor)
    private static final int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    protected General(Player owner, Position position) {
        super(PieceType.GENERAL, owner, position, VALUE);
    }

    @Override
    public List<Move> getMoves(Board board) {
       List<Move> moves = new ArrayList<>();
       return moves;
    }

}
