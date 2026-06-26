package it.unibo.xiangqi.ai;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.xiangqi.ai.impl.MoveCalculatorImpl;
import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameModel;
import it.unibo.xiangqi.model.api.GameState;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.RuleEngine;

public final class TestMoveCalculator {
    private MoveCalculatorImpl moveCalculator;
    private RuleEngine ruleEngine;
    private GameModel gameModel;

    @BeforeEach
    void setup() {
        ruleEngine = mock(RuleEngine.class);
        gameModel = mock(GameModel.class);

        moveCalculator = new MoveCalculatorImpl(ruleEngine);
    }

    @Test
    public void testCalculateBoardScore_onlyCurrentPlayerPieces() {
        Board board = mock(Board.class);
        Player redPlayer = mock(Player.class);
        Player blackPlayer = mock(Player.class);

        /* When is called ..., then return ... */
        when(redPlayer.getColor()).thenReturn(Color.RED);
        when(blackPlayer.getColor()).thenReturn(Color.BLACK);

        Piece red1 = mock(Piece.class);
        Piece red2 = mock(Piece.class);
        Piece black = mock(Piece.class);

        when(red1.getOwner()).thenReturn(redPlayer);
        when(red1.getCurrentValue()).thenReturn(10);
        when(red1.getInitialValue()).thenReturn(10);
        when(red2.getOwner()).thenReturn(redPlayer);
        when(red2.getCurrentValue()).thenReturn(20);
        when(red2.getInitialValue()).thenReturn(10);
        when(black.getOwner()).thenReturn(blackPlayer);
        when(black.getCurrentValue()).thenReturn(50);
        when(black.getInitialValue()).thenReturn(10);

        when(board.getPieces()).thenReturn(List.of(red1, red2, black));

        when(ruleEngine.getLegalMoves(any(Piece.class), any(Board.class))).thenReturn(Collections.emptyList());

        GameState gameState = mock(GameState.class);
        when(gameState.getBoard()).thenReturn(board);
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
}
