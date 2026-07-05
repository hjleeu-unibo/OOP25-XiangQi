package it.unibo.xiangqi.model.impl;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public final class TestRuleEngineImpl {

    @Mock private Board     board;

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
    @Mock private Position redSoldierPos;
    @Mock private Position blackCannonPos;
    @Mock private Position otherPos;

    @Mock private Move threatMove;
    @Mock private Move safeMove;
    @Mock private Move blockingMove;

    private RuleEngineImpl ruleEngine;

    // RuleEngineImpl simulates moves by copying the real pieces of the
    // board it is given (via PieceFactory.copyPiece). Mocking the static
    // factory lets the simulated board keep reusing the same mocked Piece
    // instances, so their stubbed behaviour (getMoves, getOwner, ...)
    // still applies after "copying".
    private MockedStatic<PieceFactory> pieceFactoryMock;

    @BeforeEach
    void setUp() {
        ruleEngine = new RuleEngineImpl();

        pieceFactoryMock = mockStatic(PieceFactory.class);
        pieceFactoryMock.when(() -> PieceFactory.copyPiece(any(Piece.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

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
        when(blackCannon.getPosition()).thenReturn(blackCannonPos);

        // Red soldier belongs to red
        when(redSoldier.getOwner()).thenReturn(redPlayer);
        when(redSoldier.getType()).thenReturn(PieceType.SOLDIER);
        when(redSoldier.getPosition()).thenReturn(redSoldierPos);

        // Threat move targets the red general's position
        when(threatMove.getTo()).thenReturn(redGeneralPos);

        // Safe move targets a different position
        when(safeMove.getTo()).thenReturn(otherPos);

        // Both the soldier's escape moves start from its own square
        when(safeMove.getFrom()).thenReturn(redSoldierPos);
        when(blockingMove.getFrom()).thenReturn(redSoldierPos);
    }

    @AfterEach
    void tearDown() {
        pieceFactoryMock.close();
    }

    @Test
    void isCheck_whenEnemyCanReachGeneral_returnsTrue() {
        when(board.getPieces()).thenReturn(List.of(redGeneral, blackCannon));
        when(blackCannon.getMoves(board)).thenReturn(List.of(threatMove));
 
        assertTrue(ruleEngine.isCheck(redPlayer, board));
    }

    @Test
    void isCheck_whenNoEnemyCanReachGeneral_returnsFalse() {
        when(board.getPieces()).thenReturn(List.of(redGeneral, blackCannon));
        when(blackCannon.getMoves(board)).thenReturn(List.of(safeMove));
 
        assertFalse(ruleEngine.isCheck(redPlayer, board));
    }

    @Test
    void isCheck_whenEnemyHasNoMoves_returnsFalse() {
        when(board.getPieces()).thenReturn(List.of(redGeneral, blackCannon));
        when(blackCannon.getMoves(board)).thenReturn(List.of());
 
        assertFalse(ruleEngine.isCheck(redPlayer, board));
    }

    // No matter which move gets simulated, the resulting board still has
    // blackCannon threatening the red general (i.e. the move never escapes check).
    private void stubSimulationAlwaysInCheck() {
        when(blackCannon.getMoves(any(Board.class))).thenReturn(List.of(threatMove));
    }

     
    @Test
    void isCheckMate_whenInCheckWithNoEscape_returnsTrue() {
        when(board.getPieces()).thenReturn(List.of(redGeneral, redSoldier, blackCannon));
        when(blackCannon.getMoves(board)).thenReturn(List.of(threatMove));

        when(redSoldier.getMoves(board)).thenReturn(List.of(blockingMove));
        when(blockingMove.getTo()).thenReturn(otherPos);
        stubSimulationAlwaysInCheck();

        assertTrue(ruleEngine.isCheckMate(redPlayer, board));
    }

    @Test
    void isCheckMate_whenInCheckButHasEscape_returnsFalse() {
        when(board.getPieces()).thenReturn(List.of(redGeneral, blackGeneral, redSoldier, blackCannon));

        // Simulating blockingMove resolves the check: on the simulated board
        // the cannon can no longer reach the general. Stubbed before the
        // exact-board case below, so the more specific stub wins for real calls.
        when(blackCannon.getMoves(any(Board.class))).thenReturn(List.of(safeMove));
        when(blackCannon.getMoves(board)).thenReturn(List.of(threatMove));

        when(redSoldier.getMoves(board)).thenReturn(List.of(blockingMove));
        when(blockingMove.getTo()).thenReturn(otherPos);

        // Generals on different columns → no flying-general issue
        when(redGeneralPos.getCol()).thenReturn(3);
        when(blackGeneralPos.getCol()).thenReturn(6);

        assertFalse(ruleEngine.isCheckMate(redPlayer, board));
    }

    @Test
    void isDraw_withOnlyGeneralsAndDefenders_returnsTrue() {
        Piece redAdvisor = mock(Piece.class);
        Piece blackElephant = mock(Piece.class);

        when(redAdvisor.isDefensor()).thenReturn(true);
        when(blackElephant.isDefensor()).thenReturn(true);
    
        // di default il getType degli altri ritorna null - il codice
        // di isDraw deve escludere solo i Generali
        when(redAdvisor.getType()).thenReturn(PieceType.ADVISOR);
        when(blackElephant.getType()).thenReturn(PieceType.ELEPHANT);
 
        when(board.getPieces()).thenReturn(List.of(redGeneral, blackGeneral, redAdvisor, blackElephant));
 
        assertTrue(ruleEngine.isDraw(board));
    }

    @Test
    void isDraw_whenAtLeastOneOffensivePiece_returnsFalse() {

        Piece redChariot = mock(Piece.class);   // pezzo offensivo
        Piece blackElephant = mock(Piece.class);

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
    void getLegalMoves_includesMoveThatDoesNotLeaveInCheck() {
        when(redSoldier.getMoves(board)).thenReturn(List.of(safeMove));
        when(board.getPieces()).thenReturn(List.of(redGeneral, blackGeneral, redSoldier, blackCannon));

        // Simulate: no check after the move
        when(blackCannon.getMoves(any(Board.class))).thenReturn(List.of());

        // Generals on different columns → no flying general
        when(redGeneralPos.getCol()).thenReturn(4);
        when(blackGeneralPos.getCol()).thenReturn(5);

        List<Move> legal = ruleEngine.getLegalMoves(redSoldier, board);
        assertEquals(1, legal.size());
        assertSame(safeMove, legal.get(0));
    }

    
    @Test
    void getLegalMoves_excludesMoveThatLeavesInCheck() {
        when(redSoldier.getMoves(board)).thenReturn(List.of(safeMove));
        when(board.getPieces()).thenReturn(List.of(redGeneral, redSoldier, blackCannon));
        stubSimulationAlwaysInCheck();

        List<Move> legal = ruleEngine.getLegalMoves(redSoldier, board);
        assertTrue(legal.isEmpty());
    }

    @Test
    void getLegalMoves_whenNoCandidateMoves_returnsEmpty() {
        when(redSoldier.getMoves(board)).thenReturn(List.of());
 
        List<Move> legal = ruleEngine.getLegalMoves(redSoldier, board);
        assertTrue(legal.isEmpty());
    }

}
