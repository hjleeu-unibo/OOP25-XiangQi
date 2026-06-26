package it.unibo.xiangqi.ai;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.xiangqi.ai.impl.MoveCalculatorImpl;
import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameModel;
import it.unibo.xiangqi.model.api.GameState;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.model.api.RuleEngine;

public final class TestMoveCalculator {
    private MoveCalculatorImpl moveCalculator;
    private RuleEngine ruleEngine;
    private GameModel gameModel;
    private Board board;
    private Player redPlayer;
    private GameState gameState;
    private Position pos;

    /* This block will run before every test cases. */
    @BeforeEach
    void setup() {
        ruleEngine = mock(RuleEngine.class);
        gameModel = mock(GameModel.class);
        board = mock(Board.class);
        redPlayer = mock(Player.class);
        gameState = mock(GameState.class);
        pos = mock(Position.class);

        moveCalculator = new MoveCalculatorImpl(ruleEngine);

        /* When is called ..., then return ... */
        when(redPlayer.getColor()).thenReturn(Color.RED);
        when(gameState.getBoard()).thenReturn(board);
    }

    /* Test if calculatebBoardScore() sums only the current players' pieces. */
    @Test
    void testCalculateBoardScore_onlySumCurrentPlayerPieces() {
        Player blackPlayer = mock(Player.class);

        when(blackPlayer.getColor()).thenReturn(Color.BLACK);

        Piece red1 = mock(Piece.class);
        Piece red2 = mock(Piece.class);
        Piece black = mock(Piece.class);

        when(red1.getOwner()).thenReturn(redPlayer);
        when(red1.getType()).thenReturn(PieceType.SOLDIER);
        when(red1.getCurrentValue()).thenReturn(10);
        when(red1.getInitialValue()).thenReturn(10);

        when(red2.getOwner()).thenReturn(redPlayer);
        when(red2.getType()).thenReturn(PieceType.CHARIOT);
        when(red2.getCurrentValue()).thenReturn(20);
        when(red2.getInitialValue()).thenReturn(10);

        when(black.getOwner()).thenReturn(blackPlayer);
        when(black.getType()).thenReturn(PieceType.CHARIOT);
        when(black.getCurrentValue()).thenReturn(50);
        when(black.getInitialValue()).thenReturn(10);

        when(pos.getCol()).thenReturn(9);
        when(pos.getRow()).thenReturn(0);
        when(red1.getPosition()).thenReturn(pos);
        when(red2.getPosition()).thenReturn(pos);
        when(black.getPosition()).thenReturn(pos);

        when(board.getPieces()).thenReturn(List.of(red1, red2, black));

        when(ruleEngine.getLegalMoves(any(Piece.class), any(Board.class))).thenReturn(List.of());

        when(gameState.getCurrentPlayer()).thenReturn(redPlayer);

        int score = moveCalculator.calculateBoardScore(gameState);

        /* ASSERTS. */
        assertEquals(30, score, "Should only sum red pieces values");
        /* Verify that a function call happened. */
        verify(red1).getCurrentValue();
        verify(red2).getCurrentValue();
        /* Verify that getCurrentValue never happens. */
        verify(black, never()).getCurrentValue();
    }

    /* Test the initial value of the red chariot. */
    @Test
    void testChariotAtStartingPositionHasInitialValue() {
        Piece chariot = mock(Piece.class);

        when(gameState.getCurrentPlayer()).thenReturn(redPlayer);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(90);
        when(chariot.getPosition()).thenReturn(pos);
        when(pos.getCol()).thenReturn(0);
        when(pos.getRow()).thenReturn(9);

        when(board.getPieces()).thenReturn(List.of(chariot));
        when(ruleEngine.getLegalMoves(chariot, board)).thenReturn(List.of());

        doAnswer(inv -> {
            when(chariot.getCurrentValue()).thenReturn((int)inv.getArgument(0));
            return null;
        }).when(chariot).setValue(anyInt());

        int score = moveCalculator.calculateBoardScore(gameState);

        assertEquals(90, score);
    }

    /* Test if the red soldier over the river, changes its value or not. */
    @Test
    void testRedSoldierOverRiver() {
        Piece soldier = mock(Piece.class);

        when(gameState.getCurrentPlayer()).thenReturn(redPlayer);
        
        when(soldier.getOwner()).thenReturn(redPlayer);
        when(soldier.getType()).thenReturn(PieceType.SOLDIER);
        when(soldier.getInitialValue()).thenReturn(10);
        when(soldier.getPosition()).thenReturn(pos);
        when(pos.getRow()).thenReturn(4);

        when(board.getPieces()).thenReturn(List.of(soldier));
        when(ruleEngine.getLegalMoves(soldier, board)).thenReturn(List.of());

        doAnswer(inv -> {
            when(soldier.getCurrentValue()).thenReturn((int)inv.getArgument(0));
            return null;
        }).when(soldier).setValue(anyInt());

        int score = moveCalculator.calculateBoardScore(gameState);

        assertEquals(20, score);
    }

    @Test
    void testChariotThreateningUnprotectedPiece() {
        Player blackPlayer = mock(Player.class);
        Piece chariot = mock(Piece.class);
        Piece enemyHorse = mock(Piece.class);

        Position chariotPos = mock(Position.class);
        Position horsePos = mock(Position.class);
        Move captureMove = mock(Move.class);

        when(blackPlayer.getColor()).thenReturn(Color.BLACK);
        when(gameState.getCurrentPlayer()).thenReturn(redPlayer);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(90);
        when(chariot.getPosition()).thenReturn(chariotPos);

        when(chariotPos.getCol()).thenReturn(0);
        when(chariotPos.getRow()).thenReturn(9);

        when(enemyHorse.getOwner()).thenReturn(blackPlayer);
        when(enemyHorse.getType()).thenReturn(PieceType.HORSE);
        when(enemyHorse.getInitialValue()).thenReturn(40);
        when(enemyHorse.getPosition()).thenReturn(horsePos);

        when(horsePos.getCol()).thenReturn(0);
        when(horsePos.getRow()).thenReturn(4);

        when(captureMove.getTo()).thenReturn(horsePos);
        when(board.getPieceAt(horsePos)).thenReturn(enemyHorse);
        when(ruleEngine.getLegalMoves(chariot, board)).thenReturn(List.of(captureMove));

        when(ruleEngine.getLegalMoves(enemyHorse, board)).thenReturn(List.of());
        when(board.getPieces()).thenReturn(List.of(chariot, enemyHorse));

        doAnswer(inv -> {
            when(chariot.getCurrentValue()).thenReturn((int)inv.getArgument(0));
            return null;
        }).when(chariot).setValue(anyInt());

        int score = moveCalculator.calculateBoardScore(gameState);

        /**
         * Base value: 90 (not aligned with the enemy general)
         * Threatening the horse: 1.5 * 40 = 60
         * Expected: 90 + 60 = 150
         */
        assertEquals(150, score);
    }
}
