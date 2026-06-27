package it.unibo.xiangqi.ai;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
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
    private Player blackPlayer;
    private GameState gameState;
    private Position pos;

    /* This block will run before every test cases. */
    @BeforeEach
    void setup() {
        ruleEngine = mock(RuleEngine.class);
        gameModel = mock(GameModel.class);
        board = mock(Board.class);
        redPlayer = mock(Player.class);
        blackPlayer = mock(Player.class);
        gameState = mock(GameState.class);
        pos = mock(Position.class);

        moveCalculator = new MoveCalculatorImpl(ruleEngine);

        /* When is called ..., then return ... */
        when(redPlayer.getColor()).thenReturn(Color.RED);
        when(blackPlayer.getColor()).thenReturn(Color.BLACK);
        when(gameState.getBoard()).thenReturn(board);
        when(gameState.getCurrentPlayer()).thenReturn(redPlayer);
        when(pos.getRow()).thenReturn(9);
        when(pos.getCol()).thenReturn(0);
    }

    /* Test if calculatebBoardScore() sums only the current players' pieces. */
    @Test
    void testCalculateBoardScore_onlySumCurrentPlayerPieces() {
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

        when(red1.getPosition()).thenReturn(pos);
        when(red2.getPosition()).thenReturn(pos);
        when(black.getPosition()).thenReturn(pos);

        when(board.getPieces()).thenReturn(List.of(red1, red2, black));

        when(ruleEngine.getLegalMoves(any(Piece.class), board)).thenReturn(List.of());

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

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(90);
        when(chariot.getPosition()).thenReturn(pos);

        when(board.getPieces()).thenReturn(List.of(chariot));
        when(ruleEngine.getLegalMoves(chariot, board)).thenReturn(List.of());

        mockSetValue(chariot);

        int score = moveCalculator.calculateBoardScore(gameState);

        assertEquals(90, score);
    }

    /* Test if the red soldier over the river, changes its value or not. */
    @Test
    void testRedSoldierOverRiver() {
        Piece soldier = mock(Piece.class);
        
        when(soldier.getOwner()).thenReturn(redPlayer);
        when(soldier.getType()).thenReturn(PieceType.SOLDIER);
        when(soldier.getInitialValue()).thenReturn(10);
        when(soldier.getPosition()).thenReturn(pos);
        when(pos.getRow()).thenReturn(4);

        when(board.getPieces()).thenReturn(List.of(soldier));
        when(ruleEngine.getLegalMoves(soldier, board)).thenReturn(List.of());

        mockSetValue(soldier);

        int score = moveCalculator.calculateBoardScore(gameState);

        assertEquals(20, score);
    }

    /* Test threatening not protected. */
    @Test
    void testThreateningUnprotected() {
        Piece chariot = mock(Piece.class);
        Piece enemyHorse = mock(Piece.class);

        Position chariotPos = mock(Position.class);
        Position horsePos = mock(Position.class);
        Move captureMove = mock(Move.class);

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

        mockSetValue(chariot);

        int score = moveCalculator.calculateBoardScore(gameState);

        /**
         * Base value: 90 (not aligned with the enemy general)
         * Threatening the horse: 1.5 * 40 = 60
         * Expected: 90 + 60 = 150
         */
        assertEquals(150, score);
    }

    /* Test cannon aligned with enemy general, and decreasing value due to dead pieces. */
    @Test
    void testCannonAlignedWithEnemyGeneral() {
        Piece cannon = mock(Piece.class);
        Piece enemyGeneral = mock(Piece.class);
        Position cannonPos = mock(Position.class);
        Position generalPos = mock(Position.class);

        when(cannon.getOwner()).thenReturn(redPlayer);
        when(cannon.getType()).thenReturn(PieceType.CANNON);
        when(cannon.getInitialValue()).thenReturn(45);
        when(cannon.getPosition()).thenReturn(cannonPos);
        when(cannonPos.getRow()).thenReturn(5);
        when(cannonPos.getCol()).thenReturn(5);

        when(enemyGeneral.getOwner()).thenReturn(blackPlayer);
        when(enemyGeneral.getType()).thenReturn(PieceType.GENERAL);
        when(enemyGeneral.getPosition()).thenReturn(generalPos);
        when(generalPos.getRow()).thenReturn(0);
        when(generalPos.getCol()).thenReturn(5);

        List<Piece> pieces = new ArrayList<>(List.of(cannon, enemyGeneral));
        for (int i = 0; i < 10; i++) {
            Piece p = mock(Piece.class);
            when(p.getOwner()).thenReturn(blackPlayer);
            when(p.getType()).thenReturn(PieceType.SOLDIER);
            when(p.getPosition()).thenReturn(mock(Position.class));
            mockSetValue(p);
            pieces.add(p);
        }

        when(board.getPieces()).thenReturn(pieces);
        when(ruleEngine.getLegalMoves(any(Piece.class), board)).thenReturn(List.of());

        mockSetValue(cannon);

        int score = moveCalculator.calculateBoardScore(gameState);

        /**
         * Base value: 45
         * Aligned with enemy general: 50
         * Dead pieces: 32 - (2 + 10) = 20
         * Min value: 40
         * Expected: max(40, 50 - 20) = 40
         */
        assertEquals(40, score);
    }

    /* Test horse over river, with some dead pieces. */
    @Test
    void testHorseOverRiver() {
        Piece horse = mock(Piece.class);
        Position horsePos = mock(Position.class);

        when(horse.getOwner()).thenReturn(redPlayer);
        when(horse.getType()).thenReturn(PieceType.HORSE);
        when(horse.getInitialValue()).thenReturn(40);
        when(horse.getPosition()).thenReturn(horsePos);
        when(horsePos.getRow()).thenReturn(3);

        List<Piece> pieces = new ArrayList<>(List.of(horse));
        for (int i = 0; i < 10; i++) {
            Piece p = mock(Piece.class);
            when(p.getOwner()).thenReturn(redPlayer);
            when(p.getType()).thenReturn(PieceType.ADVISOR);
            when(p.getPosition()).thenReturn(mock(Position.class));
            mockSetValue(p);
            pieces.add(p);
        }

        when(board.getPieces()).thenReturn(pieces);
        when(ruleEngine.getLegalMoves(any(Piece.class), board)).thenReturn(List.of());

        mockSetValue(horse);

        int score = moveCalculator.calculateBoardScore(gameState);

        /**
         * Base: 40
         * Over the river: 50
         * Dead pieces: 32 - 11 = 21
         * Max value: 65
         * Expected: max(65, 50 + 21) = 65
         */
        assertEquals(65, score);
    }

    /* Test elephant out of the bottom row. */
    @Test
    void testElephantOutBottomRow() {
        Piece elephant = mock(Piece.class);
        Position elephantPos = mock(Position.class);

        when(elephant.getOwner()).thenReturn(redPlayer);
        when(elephant.getType()).thenReturn(PieceType.ELEPHANT);
        when(elephant.getInitialValue()).thenReturn(20);
        when(elephant.getPosition()).thenReturn(elephantPos);
        when(elephantPos.getRow()).thenReturn(7);

        when(board.getPieces()).thenReturn(List.of(elephant));
        when(ruleEngine.getLegalMoves(any(Piece.class), board)).thenReturn(List.of());

        mockSetValue(elephant);

        int score = moveCalculator.calculateBoardScore(gameState);

        assertEquals(30, score);
    }

    /* Test advisor out of the bottom row. */
    @Test
    void testAdvisorOutBottomRow() {
        Piece advisor = mock(Piece.class);
        Position advisorPos = mock(Position.class);

        when(advisor.getOwner()).thenReturn(redPlayer);
        when(advisor.getType()).thenReturn(PieceType.ADVISOR);
        when(advisor.getInitialValue()).thenReturn(20);
        when(advisor.getPosition()).thenReturn(advisorPos);
        when(advisorPos.getRow()).thenReturn(8);

        when(board.getPieces()).thenReturn(List.of(advisor));
        when(ruleEngine.getLegalMoves(any(Piece.class), board)).thenReturn(List.of());

        mockSetValue(advisor);

        int score = moveCalculator.calculateBoardScore(gameState);

        assertEquals(30, score);
    }

    /* Test threatening but protected. */
    @Test
    void testThreateningProtected() {
        Piece chariot = mock(Piece.class);
        Piece enemyHorse = mock(Piece.class);
        Piece enemySoldier = mock(Piece.class);

        Position chariotPos = mock(Position.class);
        Position horsePos = mock(Position.class);
        Position soldierPos = mock(Position.class);
        Move captureMove = mock(Move.class);
        Move protectMove = mock(Move.class);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(90);
        when(chariot.getPosition()).thenReturn(chariotPos);
        when(chariotPos.getCol()).thenReturn(0);
        when(chariotPos.getRow()).thenReturn(2);

        when(enemyHorse.getOwner()).thenReturn(blackPlayer);
        when(enemyHorse.getType()).thenReturn(PieceType.HORSE);
        when(enemyHorse.getInitialValue()).thenReturn(40);
        when(enemyHorse.getPosition()).thenReturn(horsePos);
        when(horsePos.getCol()).thenReturn(0);
        when(horsePos.getRow()).thenReturn(4);

        when(enemySoldier.getOwner()).thenReturn(blackPlayer);
        when(enemySoldier.getType()).thenReturn(PieceType.SOLDIER);
        when(enemySoldier.getInitialValue()).thenReturn(10);
        when(enemySoldier.getPosition()).thenReturn(soldierPos);
        when(soldierPos.getCol()).thenReturn(0);
        when(soldierPos.getRow()).thenReturn(5);

        when(captureMove.getTo()).thenReturn(horsePos);
        when(board.getPieceAt(horsePos)).thenReturn(enemyHorse);
        when(ruleEngine.getLegalMoves(chariot, board)).thenReturn(List.of(captureMove));

        when(protectMove.getTo()).thenReturn(horsePos);
        when(ruleEngine.getLegalMoves(enemyHorse, board)).thenReturn(List.of());
        when(ruleEngine.getLegalMoves(enemySoldier, board)).thenReturn(List.of(protectMove));

        when(board.getPieces()).thenReturn(List.of(chariot, enemyHorse, enemySoldier));

        mockSetValue(chariot);
        mockSetValue(enemyHorse);
        mockSetValue(enemySoldier);

        int score = moveCalculator.calculateBoardScore(gameState);

        /**
         * Base value: 90 (not aligned with the enemy general)
         * Threatening but protected: 0
         * Expected: 90
         */
        assertEquals(90, score);
    }

    /* Test threatening multiple not protected pieces. */
    @Test
    void testThreateningMultipleUnprotected() {
        Piece chariot = mock(Piece.class);
        Piece enemyHorse = mock(Piece.class);
        Piece enemySoldier = mock(Piece.class);

        Position chariotPos = mock(Position.class);
        Position horsePos = mock(Position.class);
        Position soldierPos = mock(Position.class);
        Move captureHorse = mock(Move.class);
        Move captureSoldier = mock(Move.class);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(90);
        when(chariot.getPosition()).thenReturn(chariotPos);
        when(chariotPos.getCol()).thenReturn(0);
        when(chariotPos.getRow()).thenReturn(2);

        when(enemyHorse.getOwner()).thenReturn(blackPlayer);
        when(enemyHorse.getType()).thenReturn(PieceType.HORSE);
        when(enemyHorse.getInitialValue()).thenReturn(40);
        when(enemyHorse.getPosition()).thenReturn(horsePos);
        when(horsePos.getCol()).thenReturn(0);
        when(horsePos.getRow()).thenReturn(0);

        when(enemySoldier.getOwner()).thenReturn(blackPlayer);
        when(enemySoldier.getType()).thenReturn(PieceType.SOLDIER);
        when(enemySoldier.getInitialValue()).thenReturn(10);
        when(enemySoldier.getPosition()).thenReturn(soldierPos);
        when(soldierPos.getCol()).thenReturn(0);
        when(soldierPos.getRow()).thenReturn(5);

        when(captureHorse.getTo()).thenReturn(horsePos);
        when(captureSoldier.getTo()).thenReturn(soldierPos);
        when(board.getPieceAt(horsePos)).thenReturn(enemyHorse);
        when(board.getPieceAt(soldierPos)).thenReturn(enemySoldier);
        when(ruleEngine.getLegalMoves(chariot, board)).thenReturn(List.of(captureHorse, captureSoldier));

        when(ruleEngine.getLegalMoves(enemyHorse, board)).thenReturn(List.of());
        when(ruleEngine.getLegalMoves(enemySoldier, board)).thenReturn(List.of());

        when(board.getPieces()).thenReturn(List.of(chariot, enemyHorse, enemySoldier));

        mockSetValue(chariot);
        mockSetValue(enemyHorse);
        mockSetValue(enemySoldier);

        int score = moveCalculator.calculateBoardScore(gameState);

        /**
         * Base: 90
         * Threatening not protected: 1.5 * (40 + 45) = 127
         * Expected: 90 + 217 = 217
         */
        assertEquals(217, score);
    }

    /* Test under threaten but protected. */
    @Test
    void testThreatenedProtected() {
        Piece chariot = mock(Piece.class);
        Piece enemyHorse = mock(Piece.class);
        Piece soldier = mock(Piece.class);

        Position chariotPos = mock(Position.class);
        Position horsePos = mock(Position.class);
        Position soldierPos = mock(Position.class);
        Move captureMove = mock(Move.class);
        Move protectMove = mock(Move.class);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(90);
        when(chariot.getPosition()).thenReturn(chariotPos);
        when(chariotPos.getCol()).thenReturn(0);
        when(chariotPos.getRow()).thenReturn(2);

        when(enemyHorse.getOwner()).thenReturn(blackPlayer);
        when(enemyHorse.getType()).thenReturn(PieceType.HORSE);
        when(enemyHorse.getInitialValue()).thenReturn(40);
        when(enemyHorse.getPosition()).thenReturn(horsePos);

        when(soldier.getOwner()).thenReturn(blackPlayer);
        when(soldier.getType()).thenReturn(PieceType.SOLDIER);
        when(soldier.getInitialValue()).thenReturn(10);
        when(soldier.getPosition()).thenReturn(soldierPos);
        when(soldierPos.getCol()).thenReturn(0);
        when(soldierPos.getRow()).thenReturn(3);

        when(captureMove.getTo()).thenReturn(chariotPos);
        when(protectMove.getTo()).thenReturn(chariotPos);

        when(ruleEngine.getLegalMoves(soldier, board)).thenReturn(List.of(protectMove));
        when(ruleEngine.getLegalMoves(enemyHorse, board)).thenReturn(List.of(captureMove));
        when(ruleEngine.getLegalMoves(chariot, board)).thenReturn(List.of());

        when(board.getPieces()).thenReturn(List.of(chariot, enemyHorse, soldier));

        mockSetValue(chariot);
        mockSetValue(enemyHorse);
        mockSetValue(soldier);

        int score = moveCalculator.calculateBoardScore(gameState);

        /**
         * Base: soldier 10, chariot 90
         * Threatening protected: 90
         * Expected: 10 + 90 + 90 = 190
         */
        assertEquals(190, score);
    }

    /* Test threatened not protected. */
    @Test
    void testThreatenedUnprotected() {
        Piece chariot = mock(Piece.class);
        Piece enemyHorse = mock(Piece.class);

        Position chariotPos = mock(Position.class);
        Position horsePos = mock(Position.class);
        Move captureMove = mock(Move.class);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(90);
        when(chariot.getPosition()).thenReturn(chariotPos);
        when(chariotPos.getCol()).thenReturn(0);
        when(chariotPos.getRow()).thenReturn(2);

        when(enemyHorse.getOwner()).thenReturn(blackPlayer);
        when(enemyHorse.getType()).thenReturn(PieceType.HORSE);
        when(enemyHorse.getInitialValue()).thenReturn(40);
        when(enemyHorse.getPosition()).thenReturn(horsePos);

        when(captureMove.getTo()).thenReturn(chariotPos);

        when(ruleEngine.getLegalMoves(enemyHorse, board)).thenReturn(List.of(captureMove));
        when(ruleEngine.getLegalMoves(chariot, board)).thenReturn(List.of());

        when(board.getPieces()).thenReturn(List.of(chariot, enemyHorse));

        mockSetValue(chariot);
        mockSetValue(enemyHorse);

        int score = moveCalculator.calculateBoardScore(gameState);

        /**
         * Base: 90
         * Threatening not protected: -90
         * Expected: 90 - 90 = 0
         */
        assertEquals(0, score);
    }

    /* Test threatened by multiple enemies. */
    @Test
    void testMultipleThreatened() {
        Piece chariot = mock(Piece.class);
        Piece enemyHorse = mock(Piece.class);
        Piece enemySoldier = mock(Piece.class);

        Position chariotPos = mock(Position.class);
        Position horsePos = mock(Position.class);
        Position soldierPos = mock(Position.class);
        Move captureMove1 = mock(Move.class);
        Move captureMove2 = mock(Move.class);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(90);
        when(chariot.getPosition()).thenReturn(chariotPos);
        when(chariotPos.getCol()).thenReturn(0);
        when(chariotPos.getRow()).thenReturn(2);

        when(enemyHorse.getOwner()).thenReturn(blackPlayer);
        when(enemyHorse.getType()).thenReturn(PieceType.HORSE);
        when(enemyHorse.getInitialValue()).thenReturn(40);
        when(enemyHorse.getPosition()).thenReturn(horsePos);

        when(enemySoldier.getOwner()).thenReturn(blackPlayer);
        when(enemySoldier.getType()).thenReturn(PieceType.SOLDIER);
        when(enemySoldier.getInitialValue()).thenReturn(10);
        when(enemySoldier.getPosition()).thenReturn(soldierPos);

        when(captureMove1.getTo()).thenReturn(chariotPos);
        when(captureMove2.getTo()).thenReturn(chariotPos);

        when(ruleEngine.getLegalMoves(enemyHorse, board)).thenReturn(List.of(captureMove1));
        when(ruleEngine.getLegalMoves(enemySoldier, board)).thenReturn(List.of(captureMove2));
        when(ruleEngine.getLegalMoves(chariot, board)).thenReturn(List.of());

        when(board.getPieces()).thenReturn(List.of(chariot, enemyHorse, enemySoldier));

        mockSetValue(chariot);
        mockSetValue(enemyHorse);
        mockSetValue(enemySoldier);

        int score = moveCalculator.calculateBoardScore(gameState);

        /**
         * Base: 90
         * Threatening not protected: -90
         * Expected: 90 - 90 = 0
         */
        assertEquals(0, score);
    }

    /* Test threatened by multiple enemies bu protected. */
    @Test
    void testMultipleThreatenedProtected() {
        Piece chariot = mock(Piece.class);
        Piece cannon = mock(Piece.class);
        Piece enemyHorse = mock(Piece.class);
        Piece enemySoldier = mock(Piece.class);

        Position chariotPos = mock(Position.class);
        Position cannonPos = mock(Position.class);
        Position horsePos = mock(Position.class);
        Position soldierPos = mock(Position.class);
        Move captureMove1 = mock(Move.class);
        Move captureMove2 = mock(Move.class);
        Move protectMove = mock(Move.class);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(90);
        when(chariot.getPosition()).thenReturn(chariotPos);
        when(chariotPos.getCol()).thenReturn(0);
        when(chariotPos.getRow()).thenReturn(2);

        when(cannon.getOwner()).thenReturn(redPlayer);
        when(cannon.getType()).thenReturn(PieceType.CANNON);
        when(cannon.getInitialValue()).thenReturn(45);
        when(cannon.getPosition()).thenReturn(cannonPos);

        when(enemyHorse.getOwner()).thenReturn(blackPlayer);
        when(enemyHorse.getType()).thenReturn(PieceType.HORSE);
        when(enemyHorse.getInitialValue()).thenReturn(40);
        when(enemyHorse.getPosition()).thenReturn(horsePos);

        when(enemySoldier.getOwner()).thenReturn(blackPlayer);
        when(enemySoldier.getType()).thenReturn(PieceType.SOLDIER);
        when(enemySoldier.getInitialValue()).thenReturn(10);
        when(enemySoldier.getPosition()).thenReturn(soldierPos);

        when(captureMove1.getTo()).thenReturn(chariotPos);
        when(captureMove2.getTo()).thenReturn(chariotPos);
        when(protectMove.getTo()).thenReturn(chariotPos);

        when(ruleEngine.getLegalMoves(enemyHorse, board)).thenReturn(List.of(captureMove1));
        when(ruleEngine.getLegalMoves(enemySoldier, board)).thenReturn(List.of(captureMove2));
        when(ruleEngine.getLegalMoves(cannon, board)).thenReturn(List.of(protectMove));
        when(ruleEngine.getLegalMoves(chariot, board)).thenReturn(List.of());

        when(board.getPieces()).thenReturn(List.of(chariot, cannon, enemyHorse, enemySoldier));

        mockSetValue(chariot);
        mockSetValue(cannon);
        mockSetValue(enemyHorse);
        mockSetValue(enemySoldier);

        int score = moveCalculator.calculateBoardScore(gameState);

        /**
         * Base: chariot 90, cannon 45
         * Threatening protected: 90
         * Expected: 90 + 45 + 90 = 225
         */
        assertEquals(225, score);
    }

    /* Test getBestMove. */
    @Test
    void testGetBestMove() {
        Piece chariot = mock(Piece.class);
        Piece enemyHorse = mock(Piece.class);
        Piece enemySoldier = mock(Piece.class);

        Position chariotPos = mock(Position.class);
        Position horsePos = mock(Position.class);
        Position soldierPos = mock(Position.class);
        Move captureHorse = mock(Move.class);
        Move captureSoldier = mock(Move.class);

        GameState sim1 = mock(GameState.class);
        GameState sim2 = mock(GameState.class);
        Board board1 = mock(Board.class);
        Board board2 = mock(Board.class);

        when(gameModel.getBoard()).thenReturn(board);
        when(gameModel.getCurrentPlayer()).thenReturn(redPlayer);
        when(gameModel.copyState()).thenReturn(gameState);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(90);
        when(chariot.getPosition()).thenReturn(chariotPos);
        when(chariotPos.getCol()).thenReturn(0);
        when(chariotPos.getRow()).thenReturn(2);

        when(board.getPieces()).thenReturn(List.of(chariot, enemyHorse, enemySoldier));
        when(ruleEngine.getLegalMoves(chariot, board)).thenReturn(List.of(captureHorse, captureSoldier));

        when(gameState.applyMove(captureHorse)).thenReturn(sim1);
        when(sim1.getBoard()).thenReturn(board1);
        when(sim1.getCurrentPlayer()).thenReturn(redPlayer);
        when(enemyHorse.getOwner()).thenReturn(blackPlayer);
        when(enemyHorse.getType()).thenReturn(PieceType.HORSE);
        when(enemyHorse.getInitialValue()).thenReturn(40);
        when(enemyHorse.getPosition()).thenReturn(horsePos);
        when(board1.getPieces()).thenReturn(List.of(chariot, enemyHorse, enemySoldier));
        when(ruleEngine.getLegalMoves(enemyHorse, board1)).thenReturn(List.of());

        mockSetValue(enemyHorse);

        when(gameState.applyMove(captureSoldier)).thenReturn(sim2);
        when(sim2.getBoard()).thenReturn(board2);
        when(sim2.getCurrentPlayer()).thenReturn(redPlayer);
        when(enemySoldier.getOwner()).thenReturn(blackPlayer);
        when(enemySoldier.getType()).thenReturn(PieceType.SOLDIER);
        when(enemySoldier.getInitialValue()).thenReturn(10);
        when(enemySoldier.getPosition()).thenReturn(soldierPos);
        when(board2.getPieces()).thenReturn(List.of(chariot, enemyHorse, enemySoldier));
        when(ruleEngine.getLegalMoves(enemySoldier, board2)).thenReturn(List.of());

        mockSetValue(enemySoldier);

        Move bestMove = moveCalculator.getBestMove(gameModel);

        assertEquals(captureHorse, bestMove);
    }

    /* Test getBestMove with all negative score moves. */
    @Test
    void testGetBestMoveAllNegative() {
        Piece chariot = mock(Piece.class);

        Position chariotPos = mock(Position.class);
        Move move1 = mock(Move.class);
        Move move2 = mock(Move.class);

        GameState sim1 = mock(GameState.class);
        GameState sim2 = mock(GameState.class);
        Board board1 = mock(Board.class);
        Board board2 = mock(Board.class);

        when(gameModel.getBoard()).thenReturn(board);
        when(gameModel.getCurrentPlayer()).thenReturn(redPlayer);
        when(gameModel.copyState()).thenReturn(gameState);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(90);
        when(chariot.getPosition()).thenReturn(chariotPos);
        when(chariotPos.getCol()).thenReturn(0);
        when(chariotPos.getRow()).thenReturn(2);

        when(board.getPieces()).thenReturn(List.of(chariot));
        when(ruleEngine.getLegalMoves(chariot, board)).thenReturn(List.of(move1, move2));

        when(gameState.applyMove(move1)).thenReturn(sim1);
        when(sim1.getBoard()).thenReturn(board1);
        when(sim1.getCurrentPlayer()).thenReturn(redPlayer);
        when(board1.getPieces()).thenReturn(List.of());

        when(gameState.applyMove(move2)).thenReturn(sim2);
        when(sim2.getBoard()).thenReturn(board2);
        when(sim2.getCurrentPlayer()).thenReturn(redPlayer);
        when(board2.getPieces()).thenReturn(List.of());

        Move bestMove = moveCalculator.getBestMove(gameModel);

        assertNotNull(bestMove);
    }

    /* Test getBestMove with multiple pieces. */
    @Test
    void testGetBestMoveMultiplePieces() {
        Piece piece1 = mock(Piece.class);
        Piece piece2 = mock(Piece.class);
        Move moveFromPiece1 = mock(Move.class);
        Move moveFromPiece2 = mock(Move.class);
        GameState sim1 = mock(GameState.class);
        GameState sim2 = mock(GameState.class);
        Board simBoard1 = mock(Board.class);
        Board simBoard2 = mock(Board.class);
        Piece simPiece1 = mock(Piece.class);
        Piece simPiece2 = mock(Piece.class);
        Position simPos1 = mock(Position.class);
        Position simPos2 = mock(Position.class);

        when(gameModel.getBoard()).thenReturn(board);
        when(gameModel.getCurrentPlayer()).thenReturn(redPlayer);
        when(gameModel.copyState()).thenReturn(gameState);

        when(piece1.getOwner()).thenReturn(redPlayer);
        when(piece2.getOwner()).thenReturn(redPlayer);
        when(board.getPieces()).thenReturn(List.of(piece1, piece2));
        when(ruleEngine.getLegalMoves(piece1, board)).thenReturn(List.of(moveFromPiece1));
        when(ruleEngine.getLegalMoves(piece2, board)).thenReturn(List.of(moveFromPiece2));

        when(gameState.applyMove(moveFromPiece1)).thenReturn(sim1);
        when(sim1.getBoard()).thenReturn(simBoard1);
        when(sim1.getCurrentPlayer()).thenReturn(redPlayer);
        when(simPiece1.getOwner()).thenReturn(redPlayer);
        when(simPiece1.getType()).thenReturn(PieceType.SOLDIER);
        when(simPiece1.getInitialValue()).thenReturn(10);
        when(simPiece1.getPosition()).thenReturn(simPos1);
        when(simPos1.getRow()).thenReturn(9);
        when(simBoard1.getPieces()).thenReturn(List.of(simPiece1));
        when(ruleEngine.getLegalMoves(simPiece1, simBoard1)).thenReturn(List.of());

        mockSetValue(simPiece1);

        /* The move with higher score. */
        when(gameState.applyMove(moveFromPiece2)).thenReturn(sim2);
        when(sim2.getBoard()).thenReturn(simBoard2);
        when(sim2.getCurrentPlayer()).thenReturn(redPlayer);
        when(simPiece2.getOwner()).thenReturn(redPlayer);
        when(simPiece2.getType()).thenReturn(PieceType.CHARIOT);
        when(simPiece2.getInitialValue()).thenReturn(90);
        when(simPiece2.getPosition()).thenReturn(simPos2);
        when(simPos2.getRow()).thenReturn(9);
        when(simPos2.getCol()).thenReturn(0);
        when(simBoard2.getPieces()).thenReturn(List.of(simPiece2));
        when(ruleEngine.getLegalMoves(simPiece2, simBoard2)).thenReturn(List.of());
        
        mockSetValue(simPiece2);

        Move result = moveCalculator.getBestMove(gameModel);

        assertEquals(moveFromPiece2, result);
    }

    private void mockSetValue(final Piece piece) {
        doAnswer(inv -> {
            when(piece.getCurrentValue()).thenReturn((int)inv.getArgument(0));
            return null;
        }).when(piece).setValue(anyInt());
    }
}
