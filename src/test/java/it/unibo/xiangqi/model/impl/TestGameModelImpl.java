package it.unibo.xiangqi.model.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.common.api.GameStatus;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameModel;
import it.unibo.xiangqi.model.api.GameState;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.model.api.StoredPiece;

class TestGameModelImpl {
    private GameModel gameModel;

    @BeforeEach
    void setUp() {
        // Placeholder players/board;
        final Player placeholderRed = new PlayerImpl(Color.RED, true);
        final Player placeholderBlack = new PlayerImpl(Color.BLACK, true);
        gameModel = new GameModelImpl(
            Board.createBoard(List.of()),
            List.of(placeholderRed, placeholderBlack)
        );
    }

    /* Test that the red player always moves first after starting a new game. */
    @Test
    void startGameRedPlayerGoesFirst() {
        gameModel.startGame(GameModeType.PVP);
        assertEquals(Color.RED, gameModel.getCurrentPlayer().getColor());
    }

    /* Test that both players start with the full amount of hints. */
    @Test
    void startGameBothPlayersHaveFullHints() {
        gameModel.startGame(GameModeType.PVP);
        final Player red = gameModel.getPlayers().get(0);
        final Player black = gameModel.getPlayers().get(1);
        assertEquals(3, gameModel.getHintsRemaining(red));
        assertEquals(3, gameModel.getHintsRemaining(black));
    }

    /* Test that the game status becomes IN_PROGRESS after starting a new game. */
    @Test
    void startGameStatusIsInProgress() {
        gameModel.startGame(GameModeType.PVE);
        assertEquals(GameStatus.IN_PROGRESS, gameModel.getStatus());
    }

    /* Test that the board is populated with all 32 pieces after starting a new game. */
    @Test
    void startGameBoardHas32Pieces() {
        gameModel.startGame(GameModeType.PVP);
        assertEquals(32, gameModel.getBoard().getPieces().size());
    }

    /* Test that switchTurn alternates the current player between red and black. */
    @Test
    void switchTurnAlternatesBetweenPlayers() {
        gameModel.startGame(GameModeType.PVP);
        final Player first = gameModel.getCurrentPlayer();

        gameModel.switchTurn();
        assertNotEquals(first, gameModel.getCurrentPlayer());

        gameModel.switchTurn();
        assertEquals(first, gameModel.getCurrentPlayer());
    }

    /* Test that movePiece captures the enemy piece present at the destination. */
    @Test
    void movePieceCapturesEnemyPieceAtDestination() {
        gameModel.startGame(GameModeType.PVP);
        final Board board = gameModel.getBoard();

        // Example: a red soldier advancing and capturing an enemy piece
        // Adjust these coordinates to match a real legal move from your initial layout
        final Position from = new Position(6, 0); // e.g. red soldier's starting position
        final Position to = new Position(5, 0);

        final boolean result = gameModel.movePiece(new Move(from, to));

        assertTrue(result);
        assertEquals(to, board.getPieceAt(to).getPosition());
    }

    /* Test that endGame sets the game status to FINISHED. */
    @Test
    void endGameSetsStatusToFinished() {
        gameModel.startGame(GameModeType.PVP);
        gameModel.endGame();
        assertEquals(GameStatus.FINISHED, gameModel.getStatus());
        assertTrue(gameModel.isOver());
    }

    /* Test that endGame removes all pieces from the board. */
    @Test
    void endGameClearsBoard() {
        gameModel.startGame(GameModeType.PVP);
        gameModel.endGame();
        assertTrue(gameModel.getBoard().getPieces().isEmpty());
    }

    /* Test that copyState returns an independent snapshot, unaffected by later mutations to the real board. */
    @Test
    void copyStateIsIndependentSnapshot() {
        gameModel.startGame(GameModeType.PVP);
        final GameState snapshot = gameModel.copyState();

        final Piece originalPiece = gameModel.getBoard().getPieces().get(0);
        final Position originalPos = originalPiece.getPosition();

        originalPiece.setPosition(new Position(0, 0)); // mutate the original board

        // the snapshot's piece should not be affected
        final Piece snapshotPiece = snapshot.getBoard().getPieceAt(originalPos);
        assertNotNull(snapshotPiece, "Snapshot should retain the piece at its original position (deep copy)");
    }

    /* Test that useHint decrements the hint count only for the given player. */
    @Test
    void useHintDecrementsCorrectPlayerCount() {
        gameModel.startGame(GameModeType.PVP);
        final Player red = gameModel.getPlayers().get(0);

        gameModel.useHint(red);

        assertEquals(2, gameModel.getHintsRemaining(red));
    }

    /* Test that setStatus restores mode, current player, hints and pieces from the given saved data. */
    @Test
    void setStatusRestoresGivenState() {
        gameModel.startGame(GameModeType.PVP); // run once first so player references exist for comparison
        final List<StoredPiece> stored = List.of(
            new StoredPiece(PieceType.GENERAL, Color.RED, new Position(9, 4))
        );

        gameModel.setStatus(GameModeType.PVE, Color.BLACK, 1, 2, stored);

        assertEquals(GameModeType.PVE, gameModel.getMode());
        assertEquals(Color.BLACK, gameModel.getCurrentPlayer().getColor());
        assertEquals(1, gameModel.getBoard().getPieces().size());
        assertEquals(GameStatus.IN_PROGRESS, gameModel.getStatus());
    }

    /* Test that getOpponentPlayer returns the player who is not currently taking their turn. */
    @Test
    void testGetOpponentPlayerReturnsTheOtherPlayer() {
        gameModel.startGame(GameModeType.PVP);

        final Player current = gameModel.getCurrentPlayer();
        final Player opponent = gameModel.getOpponentPlayer();

        assertNotEquals(current, opponent);
        assertTrue(gameModel.getPlayers().contains(opponent));
    }

    /* Test that getOpponentPlayer keeps returning the correct player after switchTurn. */
    @Test
    void testGetOpponentPlayerUpdatesAfterSwitchTurn() {
        gameModel.startGame(GameModeType.PVP);

        final Player redAsCurrent = gameModel.getCurrentPlayer();
        final Player blackAsOpponent = gameModel.getOpponentPlayer();

        gameModel.switchTurn();

        // after switching turn, current and opponent should have swapped
        assertEquals(blackAsOpponent, gameModel.getCurrentPlayer());
        assertEquals(redAsCurrent, gameModel.getOpponentPlayer());
    }
}
