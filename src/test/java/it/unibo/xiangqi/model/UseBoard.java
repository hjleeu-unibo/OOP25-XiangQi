package it.unibo.xiangqi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.xiangqi.common.Color;
import it.unibo.xiangqi.common.PieceType;
import it.unibo.xiangqi.common.Position;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Piece;

public class UseBoard {
    private Player redPlayer; 
    private Player blackPlayer; 
    private Board board; 
    private Piece p1, p2, p3; 
    
    @BeforeEach
    void setup(){
        this.blackPlayer = new FakePlayer(Color.BLACK); 
        this.redPlayer = new FakePlayer(Color.RED); 
        this.p1 = new FakePiece(PieceType.ADVISOR, this.blackPlayer, new Position(0, 3)); 
        this.p2 = new FakePiece(PieceType.CANNON, this.redPlayer, new Position(1, 5)); 
        this.p3 = new FakePiece(PieceType.ELEPHANT, this.blackPlayer, new Position(7, 2)); 
        board = Board.createBoard(List.of(p1, p2, p3)); 
    }

    @Test
    void test1(){
        assertEquals(board.getPieceAt(new Position(7, 2)), p3);
        assertNotEquals(board.getPieceAt(new Position(0, 3)), p2);
    }
}
