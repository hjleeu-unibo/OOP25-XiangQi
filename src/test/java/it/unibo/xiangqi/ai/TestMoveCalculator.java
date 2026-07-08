package it.unibo.xiangqi.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

/**
 * The test class for MoveCalculator.
 * TestMoveCalculator
 */
final class TestMoveCalculator {
    private static final int SOLDIER_INITIAL_VALUE = 10;
    private static final int CHARIOT_INITIAL_VALUE = 90;
    private static final int CANNON_INITIAL_VALUE = 45;
    private static final int HORSE_INITIAL_VALUE = 40;
    private static final int ELEPHANT_INITIAL_VALUE = 20;
    private static final int ADVISOR_INITIAL_VALUE = 20;

    /* Position constants. To avoid using magic number. */
    private static final int ROW_ZERO = 0;
    private static final int ROW_TWO = 2;
    private static final int ROW_THREE = 3;
    private static final int ROW_FOUR = 4;
    private static final int ROW_FIVE = 5;
    private static final int ROW_SEVEN = 7;
    private static final int ROW_EIGHT = 8;
    private static final int ROW_NINE = 9;

    private static final int COL_ZERO = 0;
    private static final int COL_ONE = 1;
    private static final int COL_TWO = 2;
    private static final int COL_FOUR = 4;
    private static final int COL_FIVE = 5;

    /* Excepted results. For avoid magic numbers. */
    private static final int EXPECTED_SUM_ONLY_REDS = 100;
    private static final int EXPECTED_CHARIOT_INITIAL_VALUE = 90;
    private static final int EXPECTED_SOLDIER_OVER_RIVER = 20;
    private static final int EXPECTED_THREATENING_UNPROTECTED = 285;
    private static final int EXPECTED_CANNON_ALIGNED_GENERAL = 40;
    private static final int EXPECTED_HORSE_OVER_RIVER = 65;
    private static final int EXPECTED_ELEPHANT_OUT_BOTTOM_ROW = 30;
    private static final int EXPECTED_ADVISOR_OUT_BOTTOM_ROW = 30;
    private static final int EXPECTED_THREATENING_PROTECTED = 168;
    private static final int EXPECTED_THREATENING_MUL_UNPROTECTED = 325;
    private static final int EXPECTED_THREATENED_PROTECTED = 100;
    private static final int EXPECTED_THREATENED_UNPROTECTED = 0;
    private static final int EXPECTED_MUL_THREATENED = 0;
    private static final int EXPECTED_MUL_THREATENED_PROTECTED = 130;

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
    void setUp() {
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
        when(pos.getRow()).thenReturn(ROW_NINE);
        when(pos.getCol()).thenReturn(COL_ZERO);
    }

    /* Test if calculatebBoardScore() sums only the current players' pieces. */
    @Test
    void testCalculateBoardScoreOnlySumCurrentPlayerPieces() {
        final Piece red1 = mock(Piece.class);
        final Piece red2 = mock(Piece.class);
        final Piece black = mock(Piece.class);

        when(red1.getOwner()).thenReturn(redPlayer);
        when(red1.getType()).thenReturn(PieceType.SOLDIER);
        when(red1.getCurrentValue()).thenReturn(SOLDIER_INITIAL_VALUE);
        when(red1.getInitialValue()).thenReturn(SOLDIER_INITIAL_VALUE);

        when(red2.getOwner()).thenReturn(redPlayer);
        when(red2.getType()).thenReturn(PieceType.CHARIOT);
        when(red2.getCurrentValue()).thenReturn(CHARIOT_INITIAL_VALUE);
        when(red2.getInitialValue()).thenReturn(CHARIOT_INITIAL_VALUE);

        when(black.getOwner()).thenReturn(blackPlayer);
        when(black.getType()).thenReturn(PieceType.CHARIOT);
        when(black.getCurrentValue()).thenReturn(CHARIOT_INITIAL_VALUE);
        when(black.getInitialValue()).thenReturn(CHARIOT_INITIAL_VALUE);

        when(red1.getPosition()).thenReturn(pos);
        when(red2.getPosition()).thenReturn(pos);
        when(black.getPosition()).thenReturn(pos);

        when(board.getPieces()).thenReturn(List.of(red1, red2, black));
        when(ruleEngine.getLegalMoves(any(Piece.class), any(Board.class))).thenReturn(List.of());

        final int score = moveCalculator.calculateBoardScore(gameState, redPlayer);

        /* ASSERTS. */
        assertEquals(EXPECTED_SUM_ONLY_REDS, score, "Should only sum red pieces values");

        /* Verify that a function call happened. */
        verify(red1).getCurrentValue();
        verify(red2).getCurrentValue();
        /* Verify that getCurrentValue never happens. */
        verify(black, never()).getCurrentValue();
    }

    /* Test the initial value of the red chariot. */
    @Test
    void testChariotAtStartingPositionHasInitialValue() {
        final Piece chariot = mock(Piece.class);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(CHARIOT_INITIAL_VALUE);
        when(chariot.getPosition()).thenReturn(pos);

        when(board.getPieces()).thenReturn(List.of(chariot));
        when(ruleEngine.getLegalMoves(chariot, board)).thenReturn(List.of());

        mockSetValue(chariot);

        final int score = moveCalculator.calculateBoardScore(gameState, redPlayer);

        assertEquals(EXPECTED_CHARIOT_INITIAL_VALUE, score);
    }

    /* Test if the red soldier over the river, changes its value or not. */
    @Test
    void testRedSoldierOverRiver() {
        final Piece soldier = mock(Piece.class);

        when(soldier.getOwner()).thenReturn(redPlayer);
        when(soldier.getType()).thenReturn(PieceType.SOLDIER);
        when(soldier.getInitialValue()).thenReturn(SOLDIER_INITIAL_VALUE);
        when(soldier.getPosition()).thenReturn(pos);
        when(pos.getRow()).thenReturn(ROW_FOUR);

        when(board.getPieces()).thenReturn(List.of(soldier));
        when(ruleEngine.getLegalMoves(soldier, board)).thenReturn(List.of());

        mockSetValue(soldier);

        final int score = moveCalculator.calculateBoardScore(gameState, redPlayer);

        assertEquals(EXPECTED_SOLDIER_OVER_RIVER, score);
    }

    /* Test threatening not protected. */
    @Test
    void testThreateningUnprotected() {
        final Piece chariot = mock(Piece.class);
        final Piece enemyHorse = mock(Piece.class);

        final Position chariotPos = mock(Position.class);
        final Position horsePos = mock(Position.class);
        final Move captureMove = mock(Move.class);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(CHARIOT_INITIAL_VALUE);
        when(chariot.getPosition()).thenReturn(chariotPos);

        when(chariotPos.getCol()).thenReturn(COL_ZERO);
        when(chariotPos.getRow()).thenReturn(ROW_NINE);

        when(enemyHorse.getOwner()).thenReturn(blackPlayer);
        when(enemyHorse.getType()).thenReturn(PieceType.HORSE);
        when(enemyHorse.getInitialValue()).thenReturn(HORSE_INITIAL_VALUE);
        when(enemyHorse.getPosition()).thenReturn(horsePos);

        when(horsePos.getCol()).thenReturn(COL_ZERO);
        when(horsePos.getRow()).thenReturn(ROW_FOUR);

        when(captureMove.getTo()).thenReturn(horsePos);
        when(board.getPieceAt(horsePos)).thenReturn(enemyHorse);
        when(ruleEngine.getLegalMoves(chariot, board)).thenReturn(List.of(captureMove));

        when(ruleEngine.getLegalMoves(enemyHorse, board)).thenReturn(List.of());
        when(board.getPieces()).thenReturn(List.of(enemyHorse, chariot));

        mockSetValue(chariot);
        mockSetValue(enemyHorse);

        final int score = moveCalculator.calculateBoardScore(gameState, redPlayer);

        /*
         * Base value: 90 (not aligned with the enemy general)
         * Horse: 40 + (32 - 2) = 70 -> min(70, 65) = 65
         * Threatening the horse: 3.0 * 65 = 195
         * Expected: 90 + 195 = 285
         */
        assertEquals(EXPECTED_THREATENING_UNPROTECTED, score);
    }

    /* Test cannon aligned with enemy general, and decreasing value due to dead pieces. */
    @Test
    void testCannonAlignedWithEnemyGeneral() {
        final Piece cannon = mock(Piece.class);
        final Piece enemyGeneral = mock(Piece.class);
        final Position cannonPos = mock(Position.class);
        final Position generalPos = mock(Position.class);

        when(cannon.getOwner()).thenReturn(redPlayer);
        when(cannon.getType()).thenReturn(PieceType.CANNON);
        when(cannon.getInitialValue()).thenReturn(CANNON_INITIAL_VALUE);
        when(cannon.getPosition()).thenReturn(cannonPos);
        when(cannonPos.getRow()).thenReturn(ROW_FIVE);
        when(cannonPos.getCol()).thenReturn(COL_FIVE);

        when(enemyGeneral.getOwner()).thenReturn(blackPlayer);
        when(enemyGeneral.getType()).thenReturn(PieceType.GENERAL);
        when(enemyGeneral.getPosition()).thenReturn(generalPos);
        when(generalPos.getRow()).thenReturn(ROW_ZERO);
        when(generalPos.getCol()).thenReturn(COL_FIVE);

        final List<Piece> pieces = new ArrayList<>(List.of(cannon, enemyGeneral));
        for (int i = 0; i < 10; i++) {
            final Piece p = mock(Piece.class);
            when(p.getOwner()).thenReturn(blackPlayer);
            when(p.getType()).thenReturn(PieceType.SOLDIER);
            when(p.getPosition()).thenReturn(mock(Position.class));
            mockSetValue(p);
            pieces.add(p);
        }

        when(board.getPieces()).thenReturn(pieces);
        when(ruleEngine.getLegalMoves(any(Piece.class), any(Board.class))).thenReturn(List.of());

        mockSetValue(cannon);

        final int score = moveCalculator.calculateBoardScore(gameState, redPlayer);

        /*
         * Base value: 45
         * Aligned with enemy general: 50
         * Dead pieces: 32 - (2 + 10) = 20
         * Min value: 40
         * Expected: max(40, 50 - 20) = 40
         */
        assertEquals(EXPECTED_CANNON_ALIGNED_GENERAL, score);
    }

    /* Test horse over river, with some dead pieces. */
    @Test
    void testHorseOverRiver() {
        final Piece horse = mock(Piece.class);
        final Position horsePos = mock(Position.class);

        when(horse.getOwner()).thenReturn(redPlayer);
        when(horse.getType()).thenReturn(PieceType.HORSE);
        when(horse.getInitialValue()).thenReturn(HORSE_INITIAL_VALUE);
        when(horse.getPosition()).thenReturn(horsePos);
        when(horsePos.getRow()).thenReturn(ROW_THREE);

        final List<Piece> pieces = new ArrayList<>(List.of(horse));
        for (int i = 0; i < 10; i++) {
            final Piece p = mock(Piece.class);
            when(p.getOwner()).thenReturn(blackPlayer);
            when(p.getType()).thenReturn(PieceType.ADVISOR);
            when(p.getPosition()).thenReturn(mock(Position.class));
            mockSetValue(p);
            pieces.add(p);
        }

        when(board.getPieces()).thenReturn(pieces);
        when(ruleEngine.getLegalMoves(any(Piece.class), any(Board.class))).thenReturn(List.of());

        mockSetValue(horse);

        final int score = moveCalculator.calculateBoardScore(gameState, redPlayer);

        /*
         * Base: 40
         * Over the river: 50
         * Dead pieces: 32 - 11 = 21
         * Max value: 65
         * Expected: max(65, 50 + 21) = 65
         */
        assertEquals(EXPECTED_HORSE_OVER_RIVER, score);
    }

    /* Test elephant out of the bottom row. */
    @Test
    void testElephantOutBottomRow() {
        final Piece elephant = mock(Piece.class);
        final Position elephantPos = mock(Position.class);

        when(elephant.getOwner()).thenReturn(redPlayer);
        when(elephant.getType()).thenReturn(PieceType.ELEPHANT);
        when(elephant.getInitialValue()).thenReturn(ELEPHANT_INITIAL_VALUE);
        when(elephant.getPosition()).thenReturn(elephantPos);
        when(elephantPos.getRow()).thenReturn(ROW_SEVEN);

        when(board.getPieces()).thenReturn(List.of(elephant));
        when(ruleEngine.getLegalMoves(any(Piece.class), any(Board.class))).thenReturn(List.of());

        mockSetValue(elephant);

        final int score = moveCalculator.calculateBoardScore(gameState, redPlayer);

        assertEquals(EXPECTED_ELEPHANT_OUT_BOTTOM_ROW, score);
    }

    /* Test advisor out of the bottom row. */
    @Test
    void testAdvisorOutBottomRow() {
        final Piece advisor = mock(Piece.class);
        final Position advisorPos = mock(Position.class);

        when(advisor.getOwner()).thenReturn(redPlayer);
        when(advisor.getType()).thenReturn(PieceType.ADVISOR);
        when(advisor.getInitialValue()).thenReturn(ADVISOR_INITIAL_VALUE);
        when(advisor.getPosition()).thenReturn(advisorPos);
        when(advisorPos.getRow()).thenReturn(ROW_EIGHT);

        when(board.getPieces()).thenReturn(List.of(advisor));
        when(ruleEngine.getLegalMoves(any(Piece.class), any(Board.class))).thenReturn(List.of());

        mockSetValue(advisor);

        final int score = moveCalculator.calculateBoardScore(gameState, redPlayer);

        assertEquals(EXPECTED_ADVISOR_OUT_BOTTOM_ROW, score);
    }

    /* Test threatening but protected. */
    @Test
    void testThreateningProtected() {
        final Piece chariot = mock(Piece.class);
        final Piece enemyHorse = mock(Piece.class);
        final Piece enemySoldier = mock(Piece.class);

        final Position chariotPos = mock(Position.class);
        final Position horsePos = mock(Position.class);
        final Position soldierPos = mock(Position.class);
        final Move captureMove = mock(Move.class);
        final Move protectMove = mock(Move.class);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(CHARIOT_INITIAL_VALUE);
        when(chariot.getPosition()).thenReturn(chariotPos);
        when(chariotPos.getCol()).thenReturn(COL_ZERO);
        when(chariotPos.getRow()).thenReturn(ROW_TWO);

        when(enemyHorse.getOwner()).thenReturn(blackPlayer);
        when(enemyHorse.getType()).thenReturn(PieceType.HORSE);
        when(enemyHorse.getInitialValue()).thenReturn(HORSE_INITIAL_VALUE);
        when(enemyHorse.getPosition()).thenReturn(horsePos);
        when(horsePos.getCol()).thenReturn(COL_ZERO);
        when(horsePos.getRow()).thenReturn(ROW_FOUR);

        when(enemySoldier.getOwner()).thenReturn(blackPlayer);
        when(enemySoldier.getType()).thenReturn(PieceType.SOLDIER);
        when(enemySoldier.getInitialValue()).thenReturn(SOLDIER_INITIAL_VALUE);
        when(enemySoldier.getPosition()).thenReturn(soldierPos);
        when(soldierPos.getCol()).thenReturn(COL_ZERO);
        when(soldierPos.getRow()).thenReturn(ROW_FIVE);

        when(captureMove.getTo()).thenReturn(horsePos);
        when(board.getPieceAt(horsePos)).thenReturn(enemyHorse);
        when(ruleEngine.getLegalMoves(chariot, board)).thenReturn(List.of(captureMove));

        when(protectMove.getTo()).thenReturn(horsePos);
        when(ruleEngine.getLegalMoves(enemyHorse, board)).thenReturn(List.of());
        when(ruleEngine.getLegalMoves(enemySoldier, board)).thenReturn(List.of(protectMove));

        when(board.getPieces()).thenReturn(List.of(enemyHorse, enemySoldier, chariot));

        mockSetValue(chariot);
        mockSetValue(enemyHorse);
        mockSetValue(enemySoldier);

        final int score = moveCalculator.calculateBoardScore(gameState, redPlayer);

        /*
        * Base value: 90
        * Horse value: 40 + (32 - 3) = 69 -> min(69, 65) = 65
        * Threatening multiplier (protected): 3.0 * 0.8 = 2.4
        * Unsafe move: target 65, own 90 -> 0.5 penalty
        * Attack bonus: 2.4 * 65 * 0.5 = 78
        * Expected: 90 + 78 = 168
        */
        assertEquals(EXPECTED_THREATENING_PROTECTED, score);
    }

    /* Test threatening multiple not protected pieces. */
    @Test
    void testThreateningMultipleUnprotected() {
        final Piece chariot = mock(Piece.class);
        final Piece enemyHorse = mock(Piece.class);
        final Piece enemySoldier = mock(Piece.class);

        final Position chariotPos = mock(Position.class);
        final Position horsePos = mock(Position.class);
        final Position soldierPos = mock(Position.class);
        final Move captureHorse = mock(Move.class);
        final Move captureSoldier = mock(Move.class);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(CHARIOT_INITIAL_VALUE);
        when(chariot.getPosition()).thenReturn(chariotPos);
        when(chariotPos.getCol()).thenReturn(COL_ZERO);
        when(chariotPos.getRow()).thenReturn(ROW_TWO);

        when(enemyHorse.getOwner()).thenReturn(blackPlayer);
        when(enemyHorse.getType()).thenReturn(PieceType.HORSE);
        when(enemyHorse.getInitialValue()).thenReturn(HORSE_INITIAL_VALUE);
        when(enemyHorse.getPosition()).thenReturn(horsePos);
        when(horsePos.getCol()).thenReturn(COL_ZERO);
        when(horsePos.getRow()).thenReturn(ROW_ZERO);

        when(enemySoldier.getOwner()).thenReturn(blackPlayer);
        when(enemySoldier.getType()).thenReturn(PieceType.SOLDIER);
        when(enemySoldier.getInitialValue()).thenReturn(SOLDIER_INITIAL_VALUE);
        when(enemySoldier.getPosition()).thenReturn(soldierPos);
        when(soldierPos.getCol()).thenReturn(COL_ZERO);
        when(soldierPos.getRow()).thenReturn(ROW_FIVE);

        when(captureHorse.getTo()).thenReturn(horsePos);
        when(captureSoldier.getTo()).thenReturn(soldierPos);
        when(board.getPieceAt(horsePos)).thenReturn(enemyHorse);
        when(board.getPieceAt(soldierPos)).thenReturn(enemySoldier);
        when(ruleEngine.getLegalMoves(chariot, board)).thenReturn(List.of(captureHorse, captureSoldier));

        when(ruleEngine.getLegalMoves(enemyHorse, board)).thenReturn(List.of());
        when(ruleEngine.getLegalMoves(enemySoldier, board)).thenReturn(List.of());

        when(board.getPieces()).thenReturn(List.of(enemyHorse, enemySoldier, chariot));

        mockSetValue(chariot);
        mockSetValue(enemyHorse);
        mockSetValue(enemySoldier);

        final int score = moveCalculator.calculateBoardScore(gameState, redPlayer);

        /*
         * Base: 90
         * Horse: 40 + (32 - 3) = 69 -> 65
         * Soldier: 20
         * Threatening not protected:
         *  - Horse: 3.0 * 65 = 195
         *  - Soldier: 2.0 * 20 = 40
         * Expected: 90 + 195 + 40 = 325
         */
        assertEquals(EXPECTED_THREATENING_MUL_UNPROTECTED, score);
    }

    /* Test under threaten but protected. */
    @Test
    void testThreatenedProtected() {
        final Piece chariot = mock(Piece.class);
        final Piece enemyHorse = mock(Piece.class);
        final Piece soldier = mock(Piece.class);

        final Position chariotPos = mock(Position.class);
        final Position horsePos = mock(Position.class);
        final Position soldierPos = mock(Position.class);
        final Move captureMove = mock(Move.class);
        final Move protectMove = mock(Move.class);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(CHARIOT_INITIAL_VALUE);
        when(chariot.getPosition()).thenReturn(chariotPos);
        when(chariotPos.getCol()).thenReturn(COL_ZERO);
        when(chariotPos.getRow()).thenReturn(ROW_FOUR);

        when(enemyHorse.getOwner()).thenReturn(blackPlayer);
        when(enemyHorse.getType()).thenReturn(PieceType.HORSE);
        when(enemyHorse.getInitialValue()).thenReturn(HORSE_INITIAL_VALUE);
        when(enemyHorse.getPosition()).thenReturn(horsePos);

        when(soldier.getOwner()).thenReturn(redPlayer);
        when(soldier.getType()).thenReturn(PieceType.SOLDIER);
        when(soldier.getInitialValue()).thenReturn(SOLDIER_INITIAL_VALUE);
        when(soldier.getPosition()).thenReturn(soldierPos);
        when(soldierPos.getCol()).thenReturn(COL_ZERO);
        when(soldierPos.getRow()).thenReturn(ROW_FIVE);

        when(captureMove.getTo()).thenReturn(chariotPos);
        when(protectMove.getTo()).thenReturn(chariotPos);

        when(ruleEngine.getLegalMoves(soldier, board)).thenReturn(List.of(protectMove));
        when(ruleEngine.getLegalMoves(enemyHorse, board)).thenReturn(List.of(captureMove));
        when(ruleEngine.getLegalMoves(chariot, board)).thenReturn(List.of());

        when(board.getPieces()).thenReturn(List.of(chariot, enemyHorse, soldier));

        mockSetValue(chariot);
        mockSetValue(enemyHorse);
        mockSetValue(soldier);

        final int score = moveCalculator.calculateBoardScore(gameState, redPlayer);

        /*
         * Base: soldier 10, chariot 90
         * Threatening protected: 0
         * Expected: 100
         */
        assertEquals(EXPECTED_THREATENED_PROTECTED, score);
    }

    /* Test threatened not protected. */
    @Test
    void testThreatenedUnprotected() {
        final Piece chariot = mock(Piece.class);
        final Piece enemyHorse = mock(Piece.class);

        final Position chariotPos = mock(Position.class);
        final Position horsePos = mock(Position.class);
        final Move captureMove = mock(Move.class);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(CHARIOT_INITIAL_VALUE);
        when(chariot.getPosition()).thenReturn(chariotPos);
        when(chariotPos.getCol()).thenReturn(COL_ZERO);
        when(chariotPos.getRow()).thenReturn(ROW_TWO);

        when(enemyHorse.getOwner()).thenReturn(blackPlayer);
        when(enemyHorse.getType()).thenReturn(PieceType.HORSE);
        when(enemyHorse.getInitialValue()).thenReturn(HORSE_INITIAL_VALUE);
        when(enemyHorse.getPosition()).thenReturn(horsePos);

        when(captureMove.getTo()).thenReturn(chariotPos);

        when(ruleEngine.getLegalMoves(enemyHorse, board)).thenReturn(List.of(captureMove));
        when(ruleEngine.getLegalMoves(chariot, board)).thenReturn(List.of());

        when(board.getPieces()).thenReturn(List.of(chariot, enemyHorse));

        mockSetValue(chariot);
        mockSetValue(enemyHorse);

        final int score = moveCalculator.calculateBoardScore(gameState, redPlayer);

        /*
         * Base: 90
         * Threatening not protected: -90
         * Expected: 90 - 90 = 0
         */
        assertEquals(EXPECTED_THREATENED_UNPROTECTED, score);
    }

    /* Test threatened by multiple enemies. */
    @Test
    void testMultipleThreatened() {
        final Piece chariot = mock(Piece.class);
        final Piece enemyHorse = mock(Piece.class);
        final Piece enemySoldier = mock(Piece.class);

        final Position chariotPos = mock(Position.class);
        final Position horsePos = mock(Position.class);
        final Position soldierPos = mock(Position.class);
        final Move captureMove1 = mock(Move.class);
        final Move captureMove2 = mock(Move.class);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(CHARIOT_INITIAL_VALUE);
        when(chariot.getPosition()).thenReturn(chariotPos);
        when(chariotPos.getCol()).thenReturn(COL_ZERO);
        when(chariotPos.getRow()).thenReturn(ROW_TWO);

        when(enemyHorse.getOwner()).thenReturn(blackPlayer);
        when(enemyHorse.getType()).thenReturn(PieceType.HORSE);
        when(enemyHorse.getInitialValue()).thenReturn(HORSE_INITIAL_VALUE);
        when(enemyHorse.getPosition()).thenReturn(horsePos);

        when(enemySoldier.getOwner()).thenReturn(blackPlayer);
        when(enemySoldier.getType()).thenReturn(PieceType.SOLDIER);
        when(enemySoldier.getInitialValue()).thenReturn(SOLDIER_INITIAL_VALUE);
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

        final int score = moveCalculator.calculateBoardScore(gameState, redPlayer);

        /*
         * Base: 90
         * Threatening not protected: -90
         * Expected: 90 - 90 = 0
         */
        assertEquals(EXPECTED_MUL_THREATENED, score);
    }

    /* Test threatened by multiple enemies bu protected. */
    @Test
    void testMultipleThreatenedProtected() {
        final Piece chariot = mock(Piece.class);
        final Piece cannon = mock(Piece.class);
        final Piece enemyHorse = mock(Piece.class);
        final Piece enemySoldier = mock(Piece.class);

        final Position chariotPos = mock(Position.class);
        final Position cannonPos = mock(Position.class);
        final Position horsePos = mock(Position.class);
        final Position soldierPos = mock(Position.class);
        final Move captureMove1 = mock(Move.class);
        final Move captureMove2 = mock(Move.class);
        final Move protectMove = mock(Move.class);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(CHARIOT_INITIAL_VALUE);
        when(chariot.getPosition()).thenReturn(chariotPos);
        when(chariotPos.getCol()).thenReturn(COL_ZERO);
        when(chariotPos.getRow()).thenReturn(ROW_TWO);

        when(cannon.getOwner()).thenReturn(redPlayer);
        when(cannon.getType()).thenReturn(PieceType.CANNON);
        when(cannon.getInitialValue()).thenReturn(CANNON_INITIAL_VALUE);
        when(cannon.getPosition()).thenReturn(cannonPos);
        when(cannonPos.getCol()).thenReturn(COL_FOUR);
        when(cannonPos.getRow()).thenReturn(ROW_NINE);

        when(enemyHorse.getOwner()).thenReturn(blackPlayer);
        when(enemyHorse.getType()).thenReturn(PieceType.HORSE);
        when(enemyHorse.getInitialValue()).thenReturn(HORSE_INITIAL_VALUE);
        when(enemyHorse.getPosition()).thenReturn(horsePos);

        when(enemySoldier.getOwner()).thenReturn(blackPlayer);
        when(enemySoldier.getType()).thenReturn(PieceType.SOLDIER);
        when(enemySoldier.getInitialValue()).thenReturn(SOLDIER_INITIAL_VALUE);
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

        final int score = moveCalculator.calculateBoardScore(gameState, redPlayer);

        /*
         * Base: chariot 90, cannon 45
         * Cannon real value: 45 - (32 - 4) = 28 -> max(40, 28) = 40
         * Expected: 90 + 40 = 130
         */
        assertEquals(EXPECTED_MUL_THREATENED_PROTECTED, score);
    }

    /* Test getBestMove. */
    @Test
    void testGetBestMove() {
        final Piece chariot1 = mock(Piece.class);
        final Piece chariot2 = mock(Piece.class);
        final Piece enemyHorse = mock(Piece.class);
        final Piece enemySoldier = mock(Piece.class);

        final Position chariotPos = mock(Position.class);
        final Position horsePos = mock(Position.class);
        final Position soldierPos = mock(Position.class);
        final Move captureHorse = mock(Move.class);
        final Move captureSoldier = mock(Move.class);
        final Move threatenMove = mock(Move.class);

        final GameState sim1 = mock(GameState.class);
        final GameState sim2 = mock(GameState.class);
        final Board board1 = mock(Board.class);
        final Board board2 = mock(Board.class);

        when(gameModel.getBoard()).thenReturn(board);
        when(gameModel.getCurrentPlayer()).thenReturn(redPlayer);
        when(gameModel.copyState()).thenReturn(gameState);

        when(chariot1.getOwner()).thenReturn(redPlayer);
        when(chariot1.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot1.getInitialValue()).thenReturn(CHARIOT_INITIAL_VALUE);
        when(chariot1.getPosition()).thenReturn(chariotPos);

        when(chariot2.getOwner()).thenReturn(redPlayer);
        when(chariot2.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot2.getInitialValue()).thenReturn(CHARIOT_INITIAL_VALUE);
        when(chariot2.getPosition()).thenReturn(chariotPos);

        mockSetValue(chariot1);
        mockSetValue(chariot2);

        when(enemyHorse.getOwner()).thenReturn(blackPlayer);
        when(enemyHorse.getType()).thenReturn(PieceType.HORSE);
        when(enemyHorse.getInitialValue()).thenReturn(HORSE_INITIAL_VALUE);
        when(enemyHorse.getPosition()).thenReturn(horsePos);
        when(horsePos.getRow()).thenReturn(ROW_ZERO);
        when(horsePos.getCol()).thenReturn(COL_ONE);

        when(enemySoldier.getOwner()).thenReturn(blackPlayer);
        when(enemySoldier.getType()).thenReturn(PieceType.SOLDIER);
        when(enemySoldier.getInitialValue()).thenReturn(SOLDIER_INITIAL_VALUE);
        when(enemySoldier.getPosition()).thenReturn(soldierPos);
        when(soldierPos.getRow()).thenReturn(ROW_ZERO);
        when(soldierPos.getCol()).thenReturn(COL_TWO);

        mockSetValue(enemyHorse);
        mockSetValue(enemySoldier);

        when(board.getPieces()).thenReturn(List.of(chariot1, enemyHorse, enemySoldier));
        when(ruleEngine.getLegalMoves(chariot1, board)).thenReturn(List.of(captureHorse, captureSoldier));
        when(captureHorse.getTo()).thenReturn(horsePos);
        when(captureSoldier.getTo()).thenReturn(soldierPos);

        /*
         * Base: 90
         * Horse: 40 + (32 - 2) = 70 -> 65
         * Threatening not protected: 1.5 * 65 = 97
         * Expected: 90 + 97 = 187 (the higher one)
         */
        when(gameState.applyTurn(captureHorse)).thenReturn(sim1);
        when(sim1.getBoard()).thenReturn(board1);
        when(sim1.getCurrentPlayer()).thenReturn(redPlayer);
        when(board1.getPieces()).thenReturn(List.of(chariot1, enemyHorse));
        when(threatenMove.getTo()).thenReturn(horsePos);
        when(board1.getPieceAt(horsePos)).thenReturn(enemyHorse);
        when(ruleEngine.getLegalMoves(chariot1, board1)).thenReturn(List.of(threatenMove));
        when(ruleEngine.getLegalMoves(enemyHorse, board1)).thenReturn(List.of());

        /*
         * Base: 90
         * Soldier (not threatened): 10
         * Expected: 90
         */
        when(gameState.applyTurn(captureSoldier)).thenReturn(sim2);
        when(sim2.getBoard()).thenReturn(board2);
        when(sim2.getCurrentPlayer()).thenReturn(redPlayer);
        when(board2.getPieces()).thenReturn(List.of(enemySoldier, chariot2));
        when(ruleEngine.getLegalMoves(chariot2, board2)).thenReturn(List.of());
        when(ruleEngine.getLegalMoves(enemySoldier, board2)).thenReturn(List.of());

        final Move bestMove = moveCalculator.getBestMove(gameModel);

        assertEquals(captureHorse, bestMove);
    }

    /* Test getBestMove with all negative score moves. */
    @Test
    void testGetBestMoveAllNegative() {
        final Piece chariot = mock(Piece.class);

        final Position chariotPos = mock(Position.class);
        final Move move1 = mock(Move.class);
        final Move move2 = mock(Move.class);

        final GameState sim1 = mock(GameState.class);
        final GameState sim2 = mock(GameState.class);
        final Board board1 = mock(Board.class);
        final Board board2 = mock(Board.class);

        when(gameModel.getBoard()).thenReturn(board);
        when(gameModel.getCurrentPlayer()).thenReturn(redPlayer);
        when(gameModel.copyState()).thenReturn(gameState);

        when(chariot.getOwner()).thenReturn(redPlayer);
        when(chariot.getType()).thenReturn(PieceType.CHARIOT);
        when(chariot.getInitialValue()).thenReturn(CHARIOT_INITIAL_VALUE);
        when(chariot.getPosition()).thenReturn(chariotPos);
        when(chariotPos.getCol()).thenReturn(COL_ZERO);
        when(chariotPos.getRow()).thenReturn(ROW_TWO);

        when(board.getPieces()).thenReturn(List.of(chariot));
        when(ruleEngine.getLegalMoves(chariot, board)).thenReturn(List.of(move1, move2));

        when(gameState.applyTurn(move1)).thenReturn(sim1);
        when(sim1.getBoard()).thenReturn(board1);
        when(sim1.getCurrentPlayer()).thenReturn(redPlayer);
        when(board1.getPieces()).thenReturn(List.of());

        when(gameState.applyTurn(move2)).thenReturn(sim2);
        when(sim2.getBoard()).thenReturn(board2);
        when(sim2.getCurrentPlayer()).thenReturn(redPlayer);
        when(board2.getPieces()).thenReturn(List.of());

        final Move bestMove = moveCalculator.getBestMove(gameModel);

        assertNotNull(bestMove);
    }

    /* Test getBestMove with multiple pieces. */
    @Test
    void testGetBestMoveMultiplePieces() {
        final Piece piece1 = mock(Piece.class);
        final Piece piece2 = mock(Piece.class);
        final Move moveFromPiece1 = mock(Move.class);
        final Move moveFromPiece2 = mock(Move.class);
        final GameState sim1 = mock(GameState.class);
        final GameState sim2 = mock(GameState.class);
        final Board simBoard1 = mock(Board.class);
        final Board simBoard2 = mock(Board.class);
        final Piece simPiece1 = mock(Piece.class);
        final Piece simPiece2 = mock(Piece.class);
        final Position simPos1 = mock(Position.class);
        final Position simPos2 = mock(Position.class);

        when(gameModel.getBoard()).thenReturn(board);
        when(gameModel.getCurrentPlayer()).thenReturn(redPlayer);
        when(gameModel.copyState()).thenReturn(gameState);

        when(piece1.getOwner()).thenReturn(redPlayer);
        when(piece2.getOwner()).thenReturn(redPlayer);
        when(board.getPieces()).thenReturn(List.of(piece1, piece2));
        when(ruleEngine.getLegalMoves(piece1, board)).thenReturn(List.of(moveFromPiece1));
        when(ruleEngine.getLegalMoves(piece2, board)).thenReturn(List.of(moveFromPiece2));

        when(gameState.applyTurn(moveFromPiece1)).thenReturn(sim1);
        when(sim1.getBoard()).thenReturn(simBoard1);
        when(sim1.getCurrentPlayer()).thenReturn(redPlayer);
        when(simPiece1.getOwner()).thenReturn(redPlayer);
        when(simPiece1.getType()).thenReturn(PieceType.SOLDIER);
        when(simPiece1.getInitialValue()).thenReturn(SOLDIER_INITIAL_VALUE);
        when(simPiece1.getPosition()).thenReturn(simPos1);
        when(simPos1.getRow()).thenReturn(ROW_NINE);
        when(simBoard1.getPieces()).thenReturn(List.of(simPiece1));
        when(ruleEngine.getLegalMoves(simPiece1, simBoard1)).thenReturn(List.of());

        mockSetValue(simPiece1);

        /* The move with higher score. */
        when(gameState.applyTurn(moveFromPiece2)).thenReturn(sim2);
        when(sim2.getBoard()).thenReturn(simBoard2);
        when(sim2.getCurrentPlayer()).thenReturn(redPlayer);
        when(simPiece2.getOwner()).thenReturn(redPlayer);
        when(simPiece2.getType()).thenReturn(PieceType.CHARIOT);
        when(simPiece2.getInitialValue()).thenReturn(CHARIOT_INITIAL_VALUE);
        when(simPiece2.getPosition()).thenReturn(simPos2);
        when(simPos2.getRow()).thenReturn(ROW_NINE);
        when(simPos2.getCol()).thenReturn(COL_ZERO);
        when(simBoard2.getPieces()).thenReturn(List.of(simPiece2));
        when(ruleEngine.getLegalMoves(simPiece2, simBoard2)).thenReturn(List.of());

        mockSetValue(simPiece2);

        final Move result = moveCalculator.getBestMove(gameModel);

        assertEquals(moveFromPiece2, result);
    }

    /* Helper function that "simulate" the Piece.setValue method. */
    private void mockSetValue(final Piece piece) {
        doAnswer(inv -> {
            when(piece.getCurrentValue()).thenReturn(inv.<Integer>getArgument(0));
            return null;
        }).when(piece).setValue(anyInt());
    }
}
