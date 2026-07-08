package it.unibo.xiangqi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.model.impl.PlayerImpl;
import it.unibo.xiangqi.model.api.Piece;

/**
 * Test class for Board.
 * UseBoard
 */
final class TestUseBoard {
    private static final int ROW1 = 0;
    private static final int COL1 = 3;
    private static final int ROW2 = 1;
    private static final int COL2 = 5;
    private static final int ROW3 = 7;
    private static final int COL3 = 2;

    private Board board;
    private Piece p2;
    private Piece p3;

    @BeforeEach
    void setUp() {
        final Player blackPlayer = new PlayerImpl(Color.BLACK, true);
        final Player redPlayer = new PlayerImpl(Color.RED, true);
        final Piece p1 = new FakePiece(PieceType.ADVISOR, blackPlayer, new Position(ROW1, COL1));
        this.p2 = new FakePiece(PieceType.CANNON, redPlayer, new Position(ROW2, COL2));
        this.p3 = new FakePiece(PieceType.ELEPHANT, blackPlayer, new Position(ROW3, COL3));
        board = Board.createBoard(List.of(p1, p2, p3));
    }

    @Test
    void test1() {
        assertEquals(board.getPieceAt(new Position(ROW3, COL3)), this.p3);
        assertNotEquals(board.getPieceAt(new Position(ROW1, COL1)), this.p2);
    }
}
