package it.unibo.xiangqi.model.impl;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

/**
 * Unit tests for all Xiangqi piece movement rules.
 * Board layout reminder:
 *   black: rows 0-4, red: rows 5-9
 *   river boundary: RIVER_ROW_RED=4, RIVER_ROW_BLACK=5
 */
class PieceTest {
    /* Position constants. To avoid using magic number. */
    private static final int ROW_ZERO = 0;
    private static final int ROW_TWO = 2;
    private static final int ROW_THREE = 3;
    private static final int ROW_FOUR = 4;
    private static final int ROW_FIVE = 5;
    private static final int ROW_SIX = 6;
    private static final int ROW_SEVEN = 7;
    private static final int ROW_EIGHT = 8;
    private static final int ROW_NINE = 9;

    private static final int COL_ZERO = 0;
    private static final int COL_TWO = 2;
    private static final int COL_THREE = 3;
    private static final int COL_FOUR = 4;
    private static final int COL_FIVE = 5;
    private static final int COL_SIX = 6;
    private static final int COL_SEVEN = 7;
    private static final int COL_EIGHT = 8;

    /* Expected test results. For avoid magic number. */
    private static final int EXPECTED_SOLDIER_BEFORE_RIVER_MOVES = 1;
    private static final int EXPECTED_SOLDIER_AFTER_RIVER_MOVES = 3;
    private static final int EXPECTED_SOLDIER_AFTER_RIVER_EDGE_MOVES = 2;
    private static final int EXPECTED_ELEPHANT_MOVES = 4;
    private static final int EXPECTED_ADVISOR_MOVES = 4;
    private static final int EXPECTED_ADVISOR_CORNER_MOVES = 1;
    private static final int EXPECTED_GENERAL_MOVES = 4;
    private static final int EXPECTED_GENERAL_CORNER_MOVES = 2;
    private static final int EXPECTED_HORSE_MOVES = 8;

    private Player red;
    private Player black;
    private Board emptyBoard;

    @BeforeEach
    void setUp() {
        red = new PlayerImpl(Color.RED, true);
        black = new PlayerImpl(Color.BLACK, true);
        emptyBoard = Board.createBoard(List.of());
    }

    // Soldier Test:

    /** Red soldier before crossing the river can only move forward (increasing row). */
    @Test
    void redSoldierBeforeRiverCanOnlyMoveForward() {
        final Piece soldier = new Soldier(red, new Position(ROW_SIX, COL_FOUR));
        final List<Move> moves = soldier.getMoves(emptyBoard);

        assertEquals(EXPECTED_SOLDIER_BEFORE_RIVER_MOVES, moves.size());
        assertEquals(new Position(ROW_FIVE, COL_FOUR), moves.get(0).getTo());
    }

    /** Black soldier before crossing the river can only move forward (decreasing row). */
    @Test
    void blackSoldierBeforeRiverCanOnlyMoveForward() {
        final Piece soldier = new Soldier(black, new Position(ROW_THREE, COL_FOUR));
        final List<Move> moves = soldier.getMoves(emptyBoard);

        assertEquals(EXPECTED_SOLDIER_BEFORE_RIVER_MOVES, moves.size());
        assertEquals(new Position(ROW_FOUR, COL_FOUR), moves.get(0).getTo());
    }

    /** Red soldier after crossing the river can move forward and sideways (3 moves). */
    @Test
    void redSoldierAfterRiverCanMoveSideways() {
        final Piece soldier = new Soldier(red, new Position(ROW_FOUR, COL_FOUR));
        final List<Move> moves = soldier.getMoves(emptyBoard);

        assertEquals(EXPECTED_SOLDIER_AFTER_RIVER_MOVES, moves.size());

        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_THREE, COL_FOUR)))); // forward
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_FOUR, COL_THREE)))); // left
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_FOUR, COL_FIVE)))); // right
    }

    /** Black soldier after crossing the river can move forward and sideways (3 moves). */
    @Test
    void blackSoldierAfterRiverCanMoveSideways() {
        final Piece soldier = new Soldier(black, new Position(ROW_FIVE, COL_FOUR));
        final List<Move> moves = soldier.getMoves(emptyBoard);

        assertEquals(EXPECTED_SOLDIER_AFTER_RIVER_MOVES, moves.size());

        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_SIX, COL_FOUR)))); // forward
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_FIVE, COL_THREE)))); // left
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_FIVE, COL_FIVE)))); // right
    }

    /** Soldier cannot move backward at any point. */
    @Test
    void redSoldierCannotMoveBackward() {
        final Piece soldier = new Soldier(red, new Position(ROW_FIVE, COL_FOUR));
        final List<Move> moves = soldier.getMoves(emptyBoard);

        assertTrue(moves.stream().noneMatch(m -> m.getTo().getRow() > ROW_FIVE));
    }

    /** Soldier cannot capture a friendly piece. */
    @Test
    void soldierCannotCaptureFriendly() {
        final Piece soldier = new Soldier(red, new Position(ROW_SIX, COL_FOUR));
        final Piece friendly = new Soldier(red, new Position(ROW_FIVE, COL_FOUR));
        final Board board = new BoardImpl(List.of(soldier, friendly));
        final List<Move> moves = soldier.getMoves(board);

        assertTrue(moves.isEmpty());
    }

    /** Soldier can capture an enemy piece. */
    @Test
    void soldierCanCaptureEnemy() {
        final Piece soldier = new Soldier(red, new Position(ROW_SIX, COL_FOUR));
        final Piece enemy = new Soldier(black, new Position(ROW_FIVE, COL_FOUR));
        final Board board = new BoardImpl(List.of(soldier, enemy));
        final List<Move> moves = soldier.getMoves(board);

        assertEquals(EXPECTED_SOLDIER_BEFORE_RIVER_MOVES, moves.size());
        assertEquals(new Position(ROW_FIVE, COL_FOUR), moves.get(0).getTo());
    }

    /** Soldier at the edge of the board has limited moves. */
    @Test
    void redSoldierAfterRiverAtBoardEdgeHasLimitedMoves() {
        // at column 0 after river — only forward and right (no left)
        final Piece soldier = new Soldier(red, new Position(ROW_FOUR, COL_ZERO));
        final List<Move> moves = soldier.getMoves(emptyBoard);

        assertEquals(EXPECTED_SOLDIER_AFTER_RIVER_EDGE_MOVES, moves.size());
    }

    // Elephant Test:

    /** Elephant moves diagonally two squares. */
    @Test
    void elephantMovesdiagonallyTwoSquares() {
        final Piece elephant = new Elephant(red, new Position(ROW_SEVEN, COL_FOUR));
        final List<Move> moves = elephant.getMoves(emptyBoard);

        assertEquals(EXPECTED_ELEPHANT_MOVES, moves.size());

        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_NINE, COL_SIX))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_NINE, COL_TWO))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_FIVE, COL_SIX))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_FIVE, COL_TWO))));
    }

    /** Red elephant cannot cross the river (row <= 4). */
    @Test
    void redElephantCannotCrossRiver() {
        final Piece elephant = new Elephant(red, new Position(ROW_FIVE, COL_FOUR));
        final List<Move> moves = elephant.getMoves(emptyBoard);

        assertTrue(moves.stream().allMatch(m -> m.getTo().getRow() >= ROW_FIVE));
    }

    /** Black elephant cannot cross the river (row >= 5). */
    @Test
    void blackElephantCannotCrossRiver() {
        final Piece elephant = new Elephant(black, new Position(ROW_FOUR, COL_FOUR));
        final List<Move> moves = elephant.getMoves(emptyBoard);

        assertTrue(moves.stream().allMatch(m -> m.getTo().getRow() <= 4));
    }

    /** Elephant cannot move if the eye cell is blocked. */
    @Test
    void elephantBlockedByPieceInEyeCell() {
        final Piece elephant = new Elephant(red, new Position(ROW_SEVEN, COL_FOUR));
        // block all four eye cells
        final Piece b1 = new Soldier(red, new Position(ROW_SIX, COL_FIVE));
        final Piece b2 = new Soldier(red, new Position(ROW_SIX, COL_THREE));
        final Piece b3 = new Soldier(red, new Position(ROW_EIGHT, COL_FIVE));
        final Piece b4 = new Soldier(red, new Position(ROW_EIGHT, COL_THREE));
        final Board board = new BoardImpl(List.of(elephant, b1, b2, b3, b4));
        final List<Move> moves = elephant.getMoves(board);

        assertTrue(moves.isEmpty());
    }

    /** Elephant cannot move to a cell occupied by a friendly piece. */
    @Test
    void elephantCannotCaptureFriendly() {
        final Piece elephant = new Elephant(red, new Position(ROW_SEVEN, COL_FOUR));
        final Piece friendly = new Chariot(red, new Position(ROW_NINE, COL_SIX));
        final Board board = new BoardImpl(List.of(elephant, friendly));
        final List<Move> moves = elephant.getMoves(board);

        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(ROW_NINE, COL_SIX))));
    }

    // Advisor Test:

    /** Advisor moves diagonally one square within the palace. */
    @Test
    void advisorMovesWithinPalace() {
        final Piece advisor = new Advisor(red, new Position(ROW_EIGHT, COL_FOUR));
        final List<Move> moves = advisor.getMoves(emptyBoard);

        assertEquals(EXPECTED_ADVISOR_MOVES, moves.size());

        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_NINE, COL_FIVE))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_NINE, COL_THREE))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_FIVE))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_THREE))));
    }

    /** Red advisor cannot leave the red palace (rows 7-9, cols 3-5). */
    @Test
    void redAdvisorCannotLeavePalace() {
        final Piece advisor = new Advisor(red, new Position(ROW_NINE, COL_THREE));
        final List<Move> moves = advisor.getMoves(emptyBoard);

        assertTrue(moves.stream().allMatch(m ->
            m.getTo().getRow() >= ROW_SEVEN
            && m.getTo().getRow() <= ROW_NINE
            && m.getTo().getCol() >= COL_THREE
            && m.getTo().getCol() <= COL_FIVE
        ));
    }

    /** Black advisor cannot leave the black palace (rows 0-2, cols 3-5). */
    @Test
    void blackAdvisorCannotLeavePalace() {
        final Piece advisor = new Advisor(black, new Position(ROW_ZERO, COL_THREE));
        final List<Move> moves = advisor.getMoves(emptyBoard);

        assertTrue(moves.stream().allMatch(m ->
            m.getTo().getRow() >= ROW_ZERO
            && m.getTo().getRow() <= ROW_TWO
            && m.getTo().getCol() >= COL_THREE
            && m.getTo().getCol() <= COL_FIVE
        ));
    }

    /** Advisor can capture an enemy piece. */
    @Test
    void advisorCanCaptureEnemy() {
        final Piece advisor = new Advisor(red, new Position(ROW_EIGHT, COL_FOUR));
        final Piece enemy = new Soldier(black, new Position(ROW_SEVEN, COL_FIVE));
        final Board board = new BoardImpl(List.of(advisor, enemy));
        final List<Move> moves = advisor.getMoves(board);

        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_FIVE))));
    }

    /** Advisor can not capture a friendly piece. */
    @Test
    void advisorCannotCaptureFriendly() {
        final Piece advisor = new Advisor(red, new Position(ROW_EIGHT, COL_FOUR));
        final Piece friendly = new Soldier(red, new Position(ROW_SEVEN, COL_FIVE));
        final Board board = new BoardImpl(List.of(advisor, friendly));
        final List<Move> moves = advisor.getMoves(board);

        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_FIVE))));
    }

    /** Advisor at a corner of the palace has only one move. */
    @Test
    void advisorAtPalaceCornerHasOneMove() {
        final Piece advisor = new Advisor(red, new Position(ROW_NINE, COL_THREE));
        final List<Move> moves = advisor.getMoves(emptyBoard);

        assertEquals(EXPECTED_ADVISOR_CORNER_MOVES, moves.size());
        assertEquals(new Position(ROW_EIGHT, COL_FOUR), moves.get(0).getTo());
    }

    // General Test:

    /** General moves orthogonally one square within the palace. */
    @Test
    void generalMovesOrthogonallyWithinPalace() {
        final Piece general = new General(red, new Position(ROW_EIGHT, COL_FOUR));
        final List<Move> moves = general.getMoves(emptyBoard);

        assertEquals(EXPECTED_GENERAL_MOVES, moves.size());

        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_NINE, COL_FOUR))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_FOUR))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_EIGHT, COL_FIVE))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_EIGHT, COL_THREE))));
    }

    /** General cannot leave the palace. */
    @Test
    void redGeneralCannotLeavePalace() {
        final Piece general = new General(red, new Position(ROW_EIGHT, COL_FOUR));
        final List<Move> moves = general.getMoves(emptyBoard);

        assertTrue(moves.stream().allMatch(m ->
            m.getTo().getRow() >= ROW_SEVEN
            && m.getTo().getRow() <= ROW_NINE
            && m.getTo().getCol() >= COL_THREE
            && m.getTo().getCol() <= COL_FIVE
        ));
    }

    /** General can capture an enemy piece. */
    @Test
    void generalCanCaptureEnemy() {
        final Piece general = new General(red, new Position(ROW_NINE, COL_FOUR));
        final Piece enemy = new Chariot(black, new Position(ROW_EIGHT, COL_FOUR));
        final Board board = new BoardImpl(List.of(general, enemy));
        final List<Move> moves = general.getMoves(board);

        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_EIGHT, COL_FOUR))));
    }

    /** General can not capture a friendly piece. */
    @Test
    void generalCannotCaptureFriendly() {
        final Piece general = new General(red, new Position(ROW_NINE, COL_FOUR));
        final Piece friendly = new Chariot(red, new Position(ROW_EIGHT, COL_FOUR));
        final Board board = new BoardImpl(List.of(general, friendly));
        final List<Move> moves = general.getMoves(board);

        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(ROW_EIGHT, COL_FOUR))));
    }

    /** General at the corner of the palace has only 2 moves. */
    @Test
    void generalAtPalaceCornerHasTwoMoves() {
        final Piece general = new General(red, new Position(ROW_NINE, COL_THREE));
        final List<Move> moves = general.getMoves(emptyBoard);

        assertEquals(EXPECTED_GENERAL_CORNER_MOVES, moves.size());
    }

    // Horse Test:

    /** Horse in the center of the board has 8 possible moves. */
    @Test
    void horseinCenterHasEightMoves() {
        final Piece horse = new Horse(red, new Position(ROW_FIVE, COL_FOUR));
        final List<Move> moves = horse.getMoves(emptyBoard);

        assertEquals(EXPECTED_HORSE_MOVES, moves.size());
    }

    /** Horse cannot move if the leg cell is blocked. */
    @Test
    void horseBlockedByPieceOnLeg() {
        final Piece horse = new Horse(red, new Position(ROW_SEVEN, COL_FOUR));
        // block the upward leg
        final Piece blocker = new Soldier(red, new Position(ROW_SIX, COL_FOUR));
        final Board board = new BoardImpl(List.of(horse, blocker));
        final List<Move> moves = horse.getMoves(board);

        // two moves upward (up2+right1, up2+left1) are blocked
        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(ROW_FIVE, COL_FIVE))));
        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(ROW_FIVE, COL_THREE))));
    }

    /** Horse in the board edge has max 2 moves. */
    @Test
    void horseAtEdgeHasFewerMoves() {
        final Piece horse = new Horse(red, new Position(ROW_NINE, COL_ZERO));
        final List<Move> moves = horse.getMoves(emptyBoard);

        assertTrue(moves.size() <= 2);
    }

    /** Horse can capture an enemy piece. */
    @Test
    void horseCanCaptureEnemy() {
        final Piece horse = new Horse(red, new Position(ROW_SEVEN, COL_FOUR));
        final Piece enemy = new Soldier(black, new Position(ROW_FIVE, COL_FIVE));
        final Board board = new BoardImpl(List.of(horse, enemy));
        final List<Move> moves = horse.getMoves(board);

        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_FIVE, COL_FIVE))));
    }

    /** Horse cannot capture a friendly piece. */
    @Test
    void horseCannotCaptureFriendly() {
        final Piece horse = new Horse(red, new Position(ROW_SEVEN, COL_FOUR));
        final Piece friendly = new Soldier(red, new Position(ROW_FIVE, COL_FIVE));
        final Board board = new BoardImpl(List.of(horse, friendly));
        final List<Move> moves = horse.getMoves(board);

        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(ROW_FIVE, COL_FIVE))));
    }

    // Cannon Test:

    /** Cannon can slide freely along a rank with no pieces in the way. */
    @Test
    void cannonSlidesFreely() {
        final Piece cannon = new Cannon(red, new Position(ROW_SEVEN, COL_FOUR));
        final List<Move> moves = cannon.getMoves(emptyBoard);

        // can reach all cells in the same row and column
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_ZERO))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_EIGHT))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_ZERO, COL_FOUR))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_NINE, COL_FOUR))));
    }

    /** Cannon cannot jump over a piece to move (only to capture). */
    @Test
    void cannonCannotJumpOverPieceToMove() {
        final Piece cannon = new Cannon(red, new Position(ROW_SEVEN, COL_FOUR));
        final Piece blocker = new Soldier(red, new Position(ROW_SEVEN, COL_SIX));
        final Board board = new BoardImpl(List.of(cannon, blocker));
        final List<Move> moves = cannon.getMoves(board);

        // cannot move to cells beyond the blocker in the same row
        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_SIX))));
        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_SEVEN))));
    }

    /** Cannon can capture an enemy piece by jumping over exactly one piece and then will stop. */
    @Test
    void cannonCapturesOverScreen() {
        final Piece cannon = new Cannon(red, new Position(ROW_SEVEN, COL_ZERO));
        final Piece screen = new Soldier(red, new Position(ROW_SEVEN, COL_THREE));
        final Piece enemy = new Soldier(black, new Position(ROW_SEVEN, COL_SIX));
        final Board board = new BoardImpl(List.of(cannon, screen, enemy));
        final List<Move> moves = cannon.getMoves(board);

        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_SIX))));
        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_SEVEN))));
    }

    /** Cannon cannot capture a friendly piece even with a screen. */
    @Test
    void cannonCannotCaptureFriendlyOverScreen() {
        final Piece cannon = new Cannon(red, new Position(ROW_SEVEN, COL_ZERO));
        final Piece screen = new Soldier(red, new Position(ROW_SEVEN, COL_THREE));
        final Piece friendly = new Soldier(red, new Position(ROW_SEVEN, COL_SIX));
        final Board board = new BoardImpl(List.of(cannon, screen, friendly));
        final List<Move> moves = cannon.getMoves(board);

        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_SIX))));
    }

    /** Cannon cannot capture if there are two pieces between it and the target. */
    @Test
    void cannonCannotCaptureOverTwoPieces() {
        final Piece cannon = new Cannon(red, new Position(ROW_SEVEN, COL_ZERO));
        final Piece screen1 = new Soldier(red, new Position(ROW_SEVEN, COL_TWO));
        final Piece screen2 = new Soldier(red, new Position(ROW_SEVEN, COL_FOUR));
        final Piece enemy = new Soldier(black, new Position(ROW_SEVEN, COL_SIX));
        final Board board = new BoardImpl(List.of(cannon, screen1, screen2, enemy));
        final List<Move> moves = cannon.getMoves(board);

        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_SIX))));
    }

    // Chariot Test:

    /** Chariot can slide freely along a rank with no pieces in the way. */
    @Test
    void chariotSlidesFreely() {
        final Piece chariot = new Chariot(red, new Position(ROW_SEVEN, COL_FOUR));
        final List<Move> moves = chariot.getMoves(emptyBoard);

        // can reach all cells in the same row and column
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_ZERO))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_EIGHT))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_ZERO, COL_FOUR))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_NINE, COL_FOUR))));
    }

    /** Chariot cannot jump over a piece to move. */
    @Test
    void chariotCannotJumpOverPieceToMove() {
        final Piece chariot = new Chariot(red, new Position(ROW_SEVEN, COL_FOUR));
        final Piece blocker = new Soldier(red, new Position(ROW_SEVEN, COL_SIX));
        final Board board = new BoardImpl(List.of(chariot, blocker));
        final List<Move> moves = chariot.getMoves(board);

        // cannot move to cells beyond the blocker in the same row
        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_SEVEN))));
    }

    /** Chariot can capture an enemy piece. */
    @Test
    void chariotCanCaptureEnemy() {
        final Piece chariot = new Chariot(red, new Position(ROW_SEVEN, COL_ZERO));
        final Piece enemy = new Soldier(black, new Position(ROW_SEVEN, COL_SIX));
        final Board board = new BoardImpl(List.of(chariot, enemy));
        final List<Move> moves = chariot.getMoves(board);

        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_SIX))));
        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_SEVEN))));
    }

    /** Chariot cannot capture a friendly piece even with a screen. */
    @Test
    void chariotCannotCaptureFriendly() {
        final Piece chariot = new Chariot(red, new Position(ROW_SEVEN, COL_ZERO));
        final Piece friendly = new Soldier(red, new Position(ROW_SEVEN, COL_SIX));
        final Board board = new BoardImpl(List.of(chariot, friendly));
        final List<Move> moves = chariot.getMoves(board);

        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(ROW_SEVEN, COL_SIX))));
    }
}
