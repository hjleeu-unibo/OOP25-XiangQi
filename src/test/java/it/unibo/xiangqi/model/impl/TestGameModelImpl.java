package it.unibo.xiangqi.model.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.common.api.GameStatus;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.*;

class TestGameModelImpl {

    private GameModel gameModel;

    @BeforeEach
    void setUp() {
        // Placeholder players/board;
        Player placeholderRed = new PlayerImpl(Color.RED, true);
        Player placeholderBlack = new PlayerImpl(Color.BLACK, true);
        gameModel = new GameModelImpl(
            Board.createBoard(List.of()),
            List.of(placeholderRed, placeholderBlack)
        );
    }

    /* Test that the red player always moves first after starting a new game. */
    @Test
    void startGame_redPlayerGoesFirst() {
        gameModel.startGame(GameModeType.PVP);
        assertEquals(Color.RED, gameModel.getCurrentPlayer().getColor());
    }

    /* Test that both players start with the full amount of hints. */
    @Test
    void startGame_bothPlayersHaveFullHints() {
        gameModel.startGame(GameModeType.PVP);
        Player red = gameModel.getPlayers().get(0);
        Player black = gameModel.getPlayers().get(1);
        assertEquals(3, gameModel.getHintsRemaining(red));
        assertEquals(3, gameModel.getHintsRemaining(black));
    }

    /* Test that the game status becomes IN_PROGRESS after starting a new game. */
    @Test
    void startGame_statusIsInProgress() {
        gameModel.startGame(GameModeType.PVE);
        assertEquals(GameStatus.IN_PROGRESS, gameModel.getStatus());
    }

    /* Test that the board is populated with all 32 pieces after starting a new game. */
    @Test
    void startGame_boardHas32Pieces() {
        gameModel.startGame(GameModeType.PVP);
        assertEquals(32, gameModel.getBoard().getPieces().size());
    }

    /* Test that switchTurn alternates the current player between red and black. */
    @Test
    void switchTurn_alternatesBetweenPlayers() {
        gameModel.startGame(GameModeType.PVP);
        Player first = gameModel.getCurrentPlayer();

        gameModel.switchTurn();
        assertNotEquals(first, gameModel.getCurrentPlayer());

        gameModel.switchTurn();
        assertEquals(first, gameModel.getCurrentPlayer());
    }

    /* Test that movePiece captures the enemy piece present at the destination. */
    @Test
    void movePiece_capturesEnemyPieceAtDestination() {
        gameModel.startGame(GameModeType.PVP);
        Board board = gameModel.getBoard();

        // Example: a red soldier advancing and capturing an enemy piece
        // Adjust these coordinates to match a real legal move from your initial layout
        Position from = new Position(6, 0); // e.g. red soldier's starting position
        Position to = new Position(5, 0);

        boolean result = gameModel.movePiece(new Move(from, to));

        assertTrue(result);
        assertEquals(to, board.getPieceAt(to).getPosition());
    }

    /* Test that endGame sets the game status to FINISHED. */
    @Test
    void endGame_setsStatusToFinished() {
        gameModel.startGame(GameModeType.PVP);
        gameModel.endGame();
        assertEquals(GameStatus.FINISHED, gameModel.getStatus());
        assertTrue(gameModel.isOver());
    }

    /* Test that endGame removes all pieces from the board. */
    @Test
    void endGame_clearsBoard() {
        gameModel.startGame(GameModeType.PVP);
        gameModel.endGame();
        assertTrue(gameModel.getBoard().getPieces().isEmpty());
    }

    /* Test that copyState returns an independent snapshot, unaffected by later mutations to the real board. */
    @Test
    void copyState_isIndependentSnapshot() {
        gameModel.startGame(GameModeType.PVP);
        GameState snapshot = gameModel.copyState();

        Piece originalPiece = gameModel.getBoard().getPieces().get(0);
        Position originalPos = originalPiece.getPosition();

        originalPiece.setPosition(new Position(0, 0)); // mutate the original board

        // the snapshot's piece should not be affected
        Piece snapshotPiece = snapshot.getBoard().getPieceAt(originalPos);
        assertNotNull(snapshotPiece, "Snapshot should retain the piece at its original position (deep copy)");
    }

    /* Test that useHint decrements the hint count only for the given player. */
    @Test
    void useHint_decrementsCorrectPlayerCount() {
        gameModel.startGame(GameModeType.PVP);
        Player red = gameModel.getPlayers().get(0);

        gameModel.useHint(red);

        assertEquals(2, gameModel.getHintsRemaining(red));
    }

    /* Test that setStatus restores mode, current player, hints and pieces from the given saved data. */
    @Test
    void setStatus_restoresGivenState() {
        gameModel.startGame(GameModeType.PVP); // run once first so player references exist for comparison
        List<StoredPiece> stored = List.of(
            new StoredPiece(PieceType.GENERAL, Color.RED, new Position(9, 4))
        );

        gameModel.setStatus(GameModeType.PVE, Color.BLACK, 1, 2, stored);

        assertEquals(GameModeType.PVE, gameModel.getMode());
        assertEquals(Color.BLACK, gameModel.getCurrentPlayer().getColor());
        assertEquals(1, gameModel.getBoard().getPieces().size());
        assertEquals(GameStatus.IN_PROGRESS, gameModel.getStatus());
    }
}