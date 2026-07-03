package it.unibo.xiangqi.model.impl;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameModel;
import it.unibo.xiangqi.model.api.GameState;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public final class TestRuleEngineImpl {

    @Mock private GameModel gameModel;
    @Mock private GameState gameState;
    @Mock private Board     board;
    @Mock private Board     simulatedBoard;
 
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
        ruleEngine = new RuleEngineImpl(gameModel);
 
        // Players' colours
        when(redPlayer.getColor()).thenReturn(Color.RED);
        when(blackPlayer.getColor()).thenReturn(Color.BLACK);
 
        // Generals' types and owners
        when(redGeneral.getType()).thenReturn(PieceType.GENERAL);
        when(redGeneral.getOwner()).thenReturn(redPlayer);
        when(redGeneral.getPosition()).thenReturn(redGeneralPos);
 
        when(blackGeneral.getType()).thenReturn(PieceType.GENERAL);
        when(blackGeneral.getOwner()).thenReturn(blackPlayer);
        when(blackGeneral.getPosition()).thenReturn(blackGeneralPos);
 
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

    private void stubSimulationAlwaysInCheck() {
        GameState copiedState = mock(GameState.class);
        when(gameModel.copyState()).thenReturn(copiedState);
        when(copiedState.applyMove(any())).thenReturn(copiedState); //cosa significa any
        when(copiedState.getBoard()).thenReturn(simulatedBoard);
 
        when(simulatedBoard.getPieces()).thenReturn(List.of(redGeneral, blackCannon));
        when(blackCannon.getMoves(simulatedBoard)).thenReturn(List.of(threatMove));
    }

     
    @Test
    void isCheckMate_whenInCheckWithNoEscape_returnsTrue() {
        when(board.getPieces()).thenReturn(List.of(redGeneral, redSoldier, blackCannon));
        when(blackCannon.getMoves(board)).thenReturn(List.of(threatMove));
 
        when(redSoldier.getMoves(board)).thenReturn(List.of(blockingMove));  //in che senso questo
        when(blockingMove.getTo()).thenReturn(otherPos);
        stubSimulationAlwaysInCheck();
 
        assertTrue(ruleEngine.isCheckMate(redPlayer, board));
    }

    @Test
    void isCheckMate_whenInCheckButHasEscape_returnsFalse() {
        when(board.getPieces()).thenReturn(List.of(redGeneral, redSoldier, blackCannon));
        when(blackCannon.getMoves(board)).thenReturn(List.of(threatMove));
        when(redSoldier.getMoves(board)).thenReturn(List.of(blockingMove));
        when(blockingMove.getTo()).thenReturn(otherPos);
 
        // Simulating blockingMove resolves the check
        GameState copiedState = mock(GameState.class);
        when(gameModel.copyState()).thenReturn(copiedState);
        when(copiedState.applyMove(blockingMove)).thenReturn(copiedState);
        when(copiedState.getBoard()).thenReturn(simulatedBoard);

        when(simulatedBoard.getPieces()).thenReturn(List.of(redGeneral, blackGeneral, redSoldier, blackCannon));
        when(blackCannon.getMoves(simulatedBoard)).thenReturn(List.of(safeMove));
 
        // Generals on different columns → no flying-general issue
        when(redGeneralPos.getCol()).thenReturn(3);
        when(blackGeneralPos.getCol()).thenReturn(6);
 
        assertFalse(ruleEngine.isCheckMate(redPlayer, board));
    }

    @Test
    void isDraw_whenAllPiecesAreDefensive_returnsTrue() {
        Piece defensiveRed   = mock(Piece.class);
        Piece defensiveBlack = mock(Piece.class);
        when(defensiveRed.isDefensor()).thenReturn(true);
        when(defensiveBlack.isDefensor()).thenReturn(true);
 
        when(board.getPieces()).thenReturn(List.of(defensiveRed, defensiveBlack));
 
        assertTrue(ruleEngine.isDraw(board));
    }

    @Test
    void isDraw_whenAtLeastOneOffensivePiece_returnsFalse() {
        Piece defensivePiece = mock(Piece.class);
        Piece offensivePiece = mock(Piece.class);
        when(defensivePiece.isDefensor()).thenReturn(true);
        when(offensivePiece.isDefensor()).thenReturn(false);
 
        when(board.getPieces()).thenReturn(List.of(defensivePiece, offensivePiece));
 
        assertFalse(ruleEngine.isDraw(board));
    }

    @Test
    void getLegalMoves_includesMoveThatDoesNotLeaveInCheck() {
        when(redSoldier.getMoves(board)).thenReturn(List.of(safeMove));
 
        // Simulate: no check, no flying general
        GameState copiedState = mock(GameState.class);
        when(gameModel.copyState()).thenReturn(copiedState);
        when(copiedState.applyMove(safeMove)).thenReturn(copiedState);
        when(copiedState.getBoard()).thenReturn(simulatedBoard);

        when(simulatedBoard.getPieces()).thenReturn(List.of(redGeneral, blackGeneral, redSoldier, blackCannon));
        when(blackCannon.getMoves(simulatedBoard)).thenReturn(List.of()); // no threat
 
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
