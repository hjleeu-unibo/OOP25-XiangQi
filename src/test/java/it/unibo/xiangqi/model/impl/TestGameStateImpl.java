package it.unibo.xiangqi.model.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import it.unibo.xiangqi.fake.FakePiece;
import it.unibo.xiangqi.fake.FakePlayer;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameState;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.PieceType;

/**
 * Test class for GameStateImpl.
 */
final class TestGameStateImpl {
    @Test
    void testApplyTurnAndSwitchTurn() {
        /* Arrange: create two players and a board containing a single piece. */
        final Player blackPlayer = new FakePlayer(Color.BLACK); 
        final Player redPlayer = new FakePlayer(Color.RED);
        final Board board = new BoardImpl(List.of(new FakePiece(PieceType.ADVISOR, redPlayer, new Position(0, 0)))); 
        /* Define the move that will be applied. */
        final Move move = new Move(new Position(0, 0), new Position(1, 1)); 
        /* Act: apply the move to obtain the next game state. */
        final GameState gameState1 = GameState.createGameState(board, List.of(redPlayer, blackPlayer), redPlayer); 
        final GameState gameState2 = gameState1.applyTurn(move); 
        /* Assert: verify that the piece has been moved correctly. */
        final Piece piece = gameState2.getBoard().getPieces().get(0); 
        assertEquals(piece.getPosition(), new Position(1, 1)); 
        assertEquals(piece.getType(), PieceType.ADVISOR); 
        assertEquals(piece.getOwner(), redPlayer);
        /* Assert: verify that the turn has been switched to the other player. */
        final Player newPlayer = gameState2.getCurrentPlayer(); 
        assertEquals(newPlayer, blackPlayer);
    }
}
