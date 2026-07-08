package it.unibo.xiangqi.model.impl;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for RuleEngineImpl
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
final class TestRuleEngineImpl {
    private static final int RED_GENERAL_COL = 5;
    private static final int BLACK_GENERAL_COL = 4;
    @Mock private Board board;

    @Mock private Player redPlayer;
    @Mock private Player blackPlayer;

    // Red general + a red piece that can block
    @Mock private Piece redGeneral;
    @Mock private Piece redSoldier;

    // Black general + an enemy attacker
    @Mock private Piece blackGeneral;
    @Mock private Piece blackCannon;

    @Mock private Position redGeneralPos;
    @Mock private Position blackGeneralPos;
    @Mock private Position otherPos;

    @Mock private Move threatMove;
    @Mock private Move safeMove;
    @Mock private Move blockingMove;

    private RuleEngineImpl ruleEngine;

    @BeforeEach
    void setUp() {
        ruleEngine = new RuleEngineImpl();

        // Players' colours
        when(redPlayer.getColor()).thenReturn(Color.RED);
        when(blackPlayer.getColor()).thenReturn(Color.BLACK);

        // Generals' types and owners
        when(redGeneral.getType()).thenReturn(PieceType.GENERAL);
        when(redGeneral.getOwner()).thenReturn(redPlayer);
        when(redGeneral.getPosition()).thenReturn(redGeneralPos);
        when(redGeneral.isDefensor()).thenReturn(false);

        when(blackGeneral.getType()).thenReturn(PieceType.GENERAL);
        when(blackGeneral.getOwner()).thenReturn(blackPlayer);
        when(blackGeneral.getPosition()).thenReturn(blackGeneralPos);
        when(blackGeneral.isDefensor()).thenReturn(false);

        // Enemy cannon belongs to black
        when(blackCannon.getOwner()).thenReturn(blackPlayer);
        when(blackCannon.getType()).thenReturn(PieceType.CANNON);

        // Red soldier belongs to red
        when(redSoldier.getOwner()).thenReturn(redPlayer);
        when(redSoldier.getType()).thenReturn(PieceType.SOLDIER);

        // Threat move targets the red general's position
        when(threatMove.getTo()).thenReturn(redGeneralPos);

        // Safe move targets a different position
        when(safeMove.getTo()).thenReturn(otherPos);
    }

    @Test
    void isCheckWhenEnemyCanReachGeneralReturnsTrue() {
        when(board.getPieces()).thenReturn(List.of(redGeneral, blackCannon));
        when(blackCannon.getMoves(board)).thenReturn(List.of(threatMove));

        assertTrue(ruleEngine.isCheck(redPlayer, board));
    }

    @Test
    void isCheckWhenNoEnemyCanReachGeneralReturnsFalse() {
        when(board.getPieces()).thenReturn(List.of(redGeneral, blackCannon));
        when(blackCannon.getMoves(board)).thenReturn(List.of(safeMove));

        assertFalse(ruleEngine.isCheck(redPlayer, board));
    }

    @Test
    void isCheckWhenEnemyHasNoThreatMovesReturnsFalse() {
        when(board.getPieces()).thenReturn(List.of(redGeneral, blackCannon));
        when(blackCannon.getMoves(board)).thenReturn(List.of());

        assertFalse(ruleEngine.isCheck(redPlayer, board));
    }

    private void simulationAlwaysInCheck() {
        final Board simulatedBoard = mock(Board.class);
        when(board.afterMove(any(Move.class))).thenReturn(simulatedBoard);
        when(simulatedBoard.getPieces()).thenReturn(List.of(redGeneral, blackCannon));
        when(blackCannon.getMoves(simulatedBoard)).thenReturn(List.of(threatMove));
    }

    @Test
    void isCheckMateWhenInCheckWithNoEscapeReturnsTrue() {
        when(board.getPieces()).thenReturn(List.of(redGeneral, redSoldier, blackCannon));
        when(blackCannon.getMoves(board)).thenReturn(List.of(threatMove));

        when(redSoldier.getMoves(board)).thenReturn(List.of(blockingMove));
        simulationAlwaysInCheck();

        assertTrue(ruleEngine.isCheckMate(redPlayer, board));
    }

    @Test
    void isCheckMateWhenInCheckButHasEscapeReturnsFalse() {
        when(board.getPieces()).thenReturn(List.of(redGeneral, redSoldier, blackCannon));
        when(blackCannon.getMoves(board)).thenReturn(List.of(threatMove));
        when(redSoldier.getMoves(board)).thenReturn(List.of(blockingMove));

        // Simulating blockingMove resolves the check on the simulated board
        // the cannon can no longer reach the general.
        final Board simulatedBoard = mock(Board.class);
        when(board.afterMove(blockingMove)).thenReturn(simulatedBoard);
        when(simulatedBoard.getPieces()).thenReturn(List.of(redGeneral, blackGeneral, redSoldier, blackCannon));
        when(blackCannon.getMoves(simulatedBoard)).thenReturn(List.of(safeMove));

        // Generals on different columns no flying-general issue
        when(redGeneralPos.getCol()).thenReturn(RED_GENERAL_COL);
        when(blackGeneralPos.getCol()).thenReturn(BLACK_GENERAL_COL);

        assertFalse(ruleEngine.isCheckMate(redPlayer, board));
    }

    @Test
    void isDrawWithOnlyGeneralsAndDefendersReturnsTrue() {
        final Piece redAdvisor = mock(Piece.class);
        final Piece blackElephant = mock(Piece.class);

        when(redAdvisor.isDefensor()).thenReturn(true);
        when(blackElephant.isDefensor()).thenReturn(true);

        when(redAdvisor.getType()).thenReturn(PieceType.ADVISOR);
        when(blackElephant.getType()).thenReturn(PieceType.ELEPHANT);

        when(board.getPieces()).thenReturn(List.of(redGeneral, blackGeneral, redAdvisor, blackElephant));

        assertTrue(ruleEngine.isDraw(board));
    }

    @Test
    void isDrawWhenAtLeastOneOffensivePieceReturnsFalse() {
        final Piece redChariot = mock(Piece.class); 
        final Piece blackElephant = mock(Piece.class);

        when(redChariot.getType()).thenReturn(PieceType.CHARIOT);
        when(redChariot.isDefensor()).thenReturn(false);

        when(blackElephant.getType()).thenReturn(PieceType.ELEPHANT);
        when(blackElephant.isDefensor()).thenReturn(true);

        when(board.getPieces()).thenReturn(
            List.of(redGeneral, blackGeneral, redChariot, blackElephant)
        );

        assertFalse(ruleEngine.isDraw(board));
    }

    @Test
    void testGetLegalMovesIncludesMoveThatDoesNotLeaveInCheck() {
        when(redSoldier.getMoves(board)).thenReturn(List.of(safeMove));

        // Simulate no check and no flying general
        final Board simulatedBoard = mock(Board.class);
        when(board.afterMove(safeMove)).thenReturn(simulatedBoard);
        when(simulatedBoard.getPieces()).thenReturn(List.of(redGeneral, blackGeneral, redSoldier, blackCannon));
        when(blackCannon.getMoves(simulatedBoard)).thenReturn(List.of()); // no threat

        // Generals on different columns
        when(redGeneralPos.getCol()).thenReturn(RED_GENERAL_COL);
        when(blackGeneralPos.getCol()).thenReturn(BLACK_GENERAL_COL);

        final List<Move> legal = ruleEngine.getLegalMoves(redSoldier, board);
        assertEquals(1, legal.size());
        assertSame(safeMove, legal.get(0));
    }

    @Test
    void testGetLegalMovesExcludesMoveThatLeavesInCheck() {
        when(redSoldier.getMoves(board)).thenReturn(List.of(safeMove));
        simulationAlwaysInCheck();

        final List<Move> legal = ruleEngine.getLegalMoves(redSoldier, board);
        assertTrue(legal.isEmpty());
    }

    @Test
    void testGetLegalMovesWhenNoCandidateMovesReturnsEmpty() {
        when(redSoldier.getMoves(board)).thenReturn(List.of());

        final List<Move> legal = ruleEngine.getLegalMoves(redSoldier, board);
        assertTrue(legal.isEmpty());
    }
}
