package it.unibo.xiangqi.model.impl;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for all Xiangqi piece movement rules.
 * Board layout reminder:
 *   black: rows 0-4, red: rows 5-9
 *   river boundary: RIVER_ROW_RED=4, RIVER_ROW_BLACK=5
 *
 * NOTE: this test class lives in it.unibo.xiangqi.model.impl (same package
 * as Soldier, Advisor, Elephant, General, Horse, Cannon, BoardImpl) because
 * those classes have protected constructors, only accessible from within
 * the package. Moving/keeping the test here avoids needing to weaken that
 * encapsulation just for testing.
 */
class PieceTest {

    private Player red;
    private Player black;
    private Board emptyBoard;

    @BeforeEach
    void setUp() {
        red        = new PlayerImpl(Color.RED,   true);
        black      = new PlayerImpl(Color.BLACK, true);
        emptyBoard = Board.createBoard(List.of());
    }

    // Soldier Test:

    /** Red soldier before crossing the river can only move forward (increasing row). */
    @Test
    void redSoldierBeforeRiverCanOnlyMoveForward() {
        final Piece soldier = new Soldier(red, new Position(6, 4));
        final List<Move> moves = soldier.getMoves(emptyBoard);

        assertEquals(1, moves.size());
        assertEquals(new Position(5, 4), moves.get(0).getTo());
    }

    /** Black soldier before crossing the river can only move forward (decreasing row). */
    @Test
    void blackSoldierBeforeRiverCanOnlyMoveForward() {
        final Piece soldier = new Soldier(black, new Position(3, 4));
        final List<Move> moves = soldier.getMoves(emptyBoard);

        assertEquals(1, moves.size());
        assertEquals(new Position(4, 4), moves.get(0).getTo());
    }

    /** Red soldier after crossing the river can move forward and sideways (3 moves). */
    @Test
    void redSoldierAfterRiverCanMoveSideways() {
        final Piece soldier = new Soldier(red, new Position(4, 4));
        final List<Move> moves = soldier.getMoves(emptyBoard);

        assertEquals(3, moves.size());
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(3, 4)))); // forward
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(4, 3)))); // left
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(4, 5)))); // right
    }

    /** Black soldier after crossing the river can move forward and sideways (3 moves). */
    @Test
    void blackSoldierAfterRiverCanMoveSideways() {
        final Piece soldier = new Soldier(black, new Position(5, 4));
        final List<Move> moves = soldier.getMoves(emptyBoard);

        assertEquals(3, moves.size());
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(6, 4)))); // forward
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(5, 3)))); // left
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(5, 5)))); // right
    }

    /** Soldier cannot move backward at any point. */
    @Test
    void redSoldierCannotMoveBackward() {
        final Piece soldier = new Soldier(red, new Position(5, 4));
        final List<Move> moves = soldier.getMoves(emptyBoard);

        assertTrue(moves.stream().noneMatch(m -> m.getTo().getRow() > 5));
    }

    /** Soldier cannot capture a friendly piece. */
    @Test
    void soldierCannotCaptureFriendly() {
        final Piece soldier  = new Soldier(red, new Position(6, 4));
        final Piece friendly = new Soldier(red, new Position(5, 4));
        final Board board    = new BoardImpl(List.of(soldier, friendly));
        final List<Move> moves = soldier.getMoves(board);

        assertTrue(moves.isEmpty());
    }

    /** Soldier can capture an enemy piece. */
    @Test
    void soldierCanCaptureEnemy() {
        final Piece soldier = new Soldier(red,   new Position(6, 4));
        final Piece enemy   = new Soldier(black, new Position(5, 4));
        final Board board   = new BoardImpl(List.of(soldier, enemy));
        final List<Move> moves = soldier.getMoves(board);

        assertEquals(1, moves.size());
        assertEquals(new Position(5, 4), moves.get(0).getTo());
    }

    /** Soldier at the edge of the board has limited moves. */
    @Test
    void redSoldierAfterRiverAtBoardEdgeHasLimitedMoves() {
        // at column 0 after river — only forward and right (no left)
        final Piece soldier = new Soldier(red, new Position(4, 0));
        final List<Move> moves = soldier.getMoves(emptyBoard);

        assertEquals(2, moves.size());
    }

    // Elephant Test:

    /** Elephant moves diagonally two squares. */
    @Test
    void elephantMovesdiagonallyTwoSquares() {
        final Piece elephant = new Elephant(red, new Position(7, 4));
        final List<Move> moves = elephant.getMoves(emptyBoard);

        assertEquals(4, moves.size());
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(9, 6))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(9, 2))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(5, 6))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(5, 2))));
    }

    /** Red elephant cannot cross the river (row <= 4). */
    @Test
    void redElephantCannotCrossRiver() {
        final Piece elephant = new Elephant(red, new Position(5, 4));
        final List<Move> moves = elephant.getMoves(emptyBoard);

        assertTrue(moves.stream().allMatch(m -> m.getTo().getRow() >= 5));
    }

    /** Black elephant cannot cross the river (row >= 5). */
    @Test
    void blackElephantCannotCrossRiver() {
        final Piece elephant = new Elephant(black, new Position(4, 4));
        final List<Move> moves = elephant.getMoves(emptyBoard);

        assertTrue(moves.stream().allMatch(m -> m.getTo().getRow() <= 4));
    }

    /** Elephant cannot move if the eye cell is blocked. */
    @Test
    void elephantBlockedByPieceInEyeCell() {
        final Piece elephant = new Elephant(red, new Position(7, 4));
        // block all four eye cells
        final Piece b1 = new Soldier(red, new Position(6, 5));
        final Piece b2 = new Soldier(red, new Position(6, 3));
        final Piece b3 = new Soldier(red, new Position(8, 5));
        final Piece b4 = new Soldier(red, new Position(8, 3));
        final Board board = new BoardImpl(List.of(elephant, b1, b2, b3, b4));
        final List<Move> moves = elephant.getMoves(board);

        assertTrue(moves.isEmpty());
    }

    /** Elephant cannot move to a cell occupied by a friendly piece. */
    @Test
    void elephantCannotCaptureFriendly() {
        final Piece elephant = new Elephant(red, new Position(7, 4));
        final Piece friendly = new Chariot(red, new Position(9, 6));
        final Board board    = new BoardImpl(List.of(elephant, friendly));
        final List<Move> moves = elephant.getMoves(board);

        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(9, 6))));
    }

    // Advisor Test:

    /** Advisor moves diagonally one square within the palace. */
    @Test
    void advisorMovesWithinPalace() {
        final Piece advisor = new Advisor(red, new Position(8, 4));
        final List<Move> moves = advisor.getMoves(emptyBoard);

        assertEquals(4, moves.size());
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(9, 5))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(9, 3))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(7, 5))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(7, 3))));
    }

    /** Red advisor cannot leave the red palace (rows 7-9, cols 3-5). */
    @Test
    void redAdvisorCannotLeavePalace() {
        final Piece advisor = new Advisor(red, new Position(9, 3));
        final List<Move> moves = advisor.getMoves(emptyBoard);

        assertTrue(moves.stream().allMatch(m ->
            m.getTo().getRow() >= 7 && m.getTo().getRow() <= 9 &&
            m.getTo().getCol() >= 3 && m.getTo().getCol() <= 5));
    }

    /** Black advisor cannot leave the black palace (rows 0-2, cols 3-5). */
    @Test
    void blackAdvisorCannotLeavePalace() {
        final Piece advisor = new Advisor(black, new Position(0, 3));
        final List<Move> moves = advisor.getMoves(emptyBoard);

        assertTrue(moves.stream().allMatch(m ->
            m.getTo().getRow() >= 0 && m.getTo().getRow() <= 2 &&
            m.getTo().getCol() >= 3 && m.getTo().getCol() <= 5));
    }

    /** Advisor at a corner of the palace has only one move. */
    @Test
    void advisorAtPalaceCornerHasOneMove() {
        final Piece advisor = new Advisor(red, new Position(9, 3));
        final List<Move> moves = advisor.getMoves(emptyBoard);

        assertEquals(1, moves.size());
        assertEquals(new Position(8, 4), moves.get(0).getTo());
    }

    // General Test:

    /** General moves orthogonally one square within the palace. */
    @Test
    void generalMovesOrthogonallyWithinPalace() {
        final Piece general = new General(red, new Position(8, 4));
        final List<Move> moves = general.getMoves(emptyBoard);

        assertEquals(4, moves.size());
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(9, 4))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(7, 4))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(8, 5))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(8, 3))));
    }

    /** General cannot leave the palace. */
    @Test
    void redGeneralCannotLeavePalace() {
        final Piece general = new General(red, new Position(8, 4));
        final List<Move> moves = general.getMoves(emptyBoard);

        assertTrue(moves.stream().allMatch(m ->
            m.getTo().getRow() >= 7 && m.getTo().getRow() <= 9 &&
            m.getTo().getCol() >= 3 && m.getTo().getCol() <= 5));
    }

    /** General at the corner of the palace has only 2 moves. */
    @Test
    void generalAtPalaceCornerHasTwoMoves() {
        final Piece general = new General(red, new Position(9, 3));
        final List<Move> moves = general.getMoves(emptyBoard);

        assertEquals(2, moves.size());
    }

    // Horse Test:

    /** Horse in the center of the board has 8 possible moves. */
    @Test
    void horseinCenterHasEightMoves() {
        final Piece horse = new Horse(red, new Position(5, 4));
        final List<Move> moves = horse.getMoves(emptyBoard);

        assertEquals(8, moves.size());
    }

    /** Horse cannot move if the leg cell is blocked. */
    @Test
    void horseBlockedByPieceOnLeg() {
        final Piece horse   = new Horse(red,   new Position(7, 4));
        // block the upward leg
        final Piece blocker = new Soldier(red, new Position(6, 4));
        final Board board   = new BoardImpl(List.of(horse, blocker));
        final List<Move> moves = horse.getMoves(board);

        // two moves upward (up2+right1, up2+left1) are blocked
        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(5, 5))));
        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(5, 3))));
    }

    /** Horse in the board edge has max 2 moves. */
    @Test
    void horseAtEdgeHasFewerMoves() {
        final Piece horse = new Horse(red, new Position(9, 0));
        final List<Move> moves = horse.getMoves(emptyBoard);

        assertTrue(moves.size() <= 2);
    }

    /** Horse can capture an enemy piece. */
    @Test
    void horseCanCaptureEnemy() {
        final Piece horse = new Horse(red,   new Position(7, 4));
        final Piece enemy = new Soldier(black, new Position(5, 5));
        final Board board = new BoardImpl(List.of(horse, enemy));
        final List<Move> moves = horse.getMoves(board);

        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(5, 5))));
    }

    /** Horse cannot capture a friendly piece. */
    @Test
    void horseCannotCaptureFriendly() {
        final Piece horse    = new Horse(red,   new Position(7, 4));
        final Piece friendly = new Soldier(red, new Position(5, 5));
        final Board board    = new BoardImpl(List.of(horse, friendly));
        final List<Move> moves = horse.getMoves(board);

        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(5, 5))));
    }

    // Cannon Test:

    /** Cannon can slide freely along a rank with no pieces in the way. */
    @Test
    void cannonSlidesFreely() {
        final Piece cannon = new Cannon(red, new Position(7, 4));
        final List<Move> moves = cannon.getMoves(emptyBoard);

        // can reach all cells in the same row and column
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(7, 0))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(7, 8))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(0, 4))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(9, 4))));
    }

    /** Cannon cannot jump over a piece to move (only to capture). */
    @Test
    void cannonCannotJumpOverPieceToMove() {
        final Piece cannon  = new Cannon(red,   new Position(7, 4));
        final Piece blocker = new Soldier(red,  new Position(7, 6));
        final Board board   = new BoardImpl(List.of(cannon, blocker));
        final List<Move> moves = cannon.getMoves(board);

        // cannot move to cells beyond the blocker in the same row
        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(7, 6))));
        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(7, 7))));
    }

    /** Cannon can capture an enemy piece by jumping over exactly one piece and then will stop. */
    @Test
    void cannonCapturesOverScreen() {
        final Piece cannon  = new Cannon(red,   new Position(7, 0));
        final Piece screen  = new Soldier(red,  new Position(7, 3));
        final Piece enemy   = new Soldier(black, new Position(7, 6));
        final Board board   = new BoardImpl(List.of(cannon, screen, enemy));
        final List<Move> moves = cannon.getMoves(board);

        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(7, 6))));
        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(7, 7))));
    }

    /** Cannon cannot capture a friendly piece even with a screen. */
    @Test
    void cannonCannotCaptureFriendlyOverScreen() {
        final Piece cannon   = new Cannon(red,  new Position(7, 0));
        final Piece screen   = new Soldier(red, new Position(7, 3));
        final Piece friendly = new Soldier(red, new Position(7, 6));
        final Board board    = new BoardImpl(List.of(cannon, screen, friendly));
        final List<Move> moves = cannon.getMoves(board);

        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(7, 6))));
    }

    /** Cannon cannot capture if there are two pieces between it and the target. */
    @Test
    void cannonCannotCaptureOverTwoPieces() {
        final Piece cannon  = new Cannon(red,    new Position(7, 0));
        final Piece screen1 = new Soldier(red,   new Position(7, 2));
        final Piece screen2 = new Soldier(red,   new Position(7, 4));
        final Piece enemy   = new Soldier(black, new Position(7, 6));
        final Board board   = new BoardImpl(List.of(cannon, screen1, screen2, enemy));
        final List<Move> moves = cannon.getMoves(board);

        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(7, 6))));
    }
    
    // Chariot Test:

    /** Chariot can slide freely along a rank with no pieces in the way. */
    @Test
    void chariotSlidesFreely() {
        final Piece chariot = new Chariot(red, new Position(7, 4));
        final List<Move> moves = chariot.getMoves(emptyBoard);

        // can reach all cells in the same row and column
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(7, 0))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(7, 8))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(0, 4))));
        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(9, 4))));
    }

    /** Chariot cannot jump over a piece to move. */
    @Test
    void chariotCannotJumpOverPieceToMove() {
        final Piece chariot  = new Chariot(red,   new Position(7, 4));
        final Piece blocker = new Soldier(red,  new Position(7, 6));
        final Board board   = new BoardImpl(List.of(chariot, blocker));
        final List<Move> moves = chariot.getMoves(board);

        // cannot move to cells beyond the blocker in the same row
        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(7, 7))));
    }

    /** Chariot can capture an enemy piece*/
    @Test
    void chariotCanCaptureEnemy() {
        final Piece chariot  = new Chariot(red,   new Position(7, 0));
        final Piece enemy   = new Soldier(black, new Position(7, 6));
        final Board board   = new BoardImpl(List.of(chariot, enemy));
        final List<Move> moves = chariot.getMoves(board);

        assertTrue(moves.stream().anyMatch(m -> m.getTo().equals(new Position(7, 6))));
        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(7, 7))));
    }

    /** Chariot cannot capture a friendly piece even with a screen. */
    @Test
    void chariotCannotCaptureFriendly() {
        final Piece chariot   = new Chariot(red,  new Position(7, 0));
        final Piece friendly = new Soldier(red, new Position(7, 6));
        final Board board    = new BoardImpl(List.of(chariot, friendly));
        final List<Move> moves = chariot.getMoves(board);

        assertTrue(moves.stream().noneMatch(m -> m.getTo().equals(new Position(7, 6))));
    }

}