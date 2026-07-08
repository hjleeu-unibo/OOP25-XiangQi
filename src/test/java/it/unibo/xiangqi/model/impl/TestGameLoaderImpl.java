package it.unibo.xiangqi.model.impl;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameModel;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.model.api.StoredPiece;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for GameLoaderImpl.
 */
@ExtendWith(MockitoExtension.class)
final class TestGameLoaderImpl {
    // Strings frequently used.
    private static final String PVE_STR = "PVE\n";
    private static final String RED_STR = "RED\n";
    private static final String ONE_STR = "1\n";
    private static final String TWO_STR = "2\n";
    private static final String THREE_STR = "3\n";

    private static final int ROW1 = 0;
    private static final int COL1 = 4;
    private static final int ROW2 = 7;
    private static final int COL2 = 1;

    @TempDir
    private Path tempDir;

    private File storeFile;
    private GameLoaderImpl loader;

    @Mock private GameModel gameModel;
    @Mock private Board board;
    @Mock private Player redPlayer;
    @Mock private Player blackPlayer;

    @BeforeEach
    void setUp() {

        //build the full path and convert from Path to File
        storeFile = tempDir.resolve("test_store.txt").toFile();

        //package-private constructor: points the loader at a temp file 
        // instead of the real user-home save file to mantain the 
        // isolation during testing phase
        loader = new GameLoaderImpl(storeFile);
    }

    @Test
    void hasStoredGameWhenFileDoesNotExistReturnsFalse() {
        assertFalse(loader.hasStoredGame());
    }

    @Test
    void hasStoredGameWhenFileIsEmptyReturnsFalse() throws IOException {
        Files.createFile(storeFile.toPath()); // empty file
        assertFalse(loader.hasStoredGame());
    }

    @Test
    void hasStoredGameWhenFileHasContentReturnsTrue() throws IOException {
        try (FileWriter writer = new FileWriter(storeFile, StandardCharsets.UTF_8)) {
            writer.write("scacchi"); 
        }
        assertTrue(loader.hasStoredGame());
    }

    @Test
    void discardSaveRemovesExistingFile() throws IOException {
        Files.createFile(storeFile.toPath());
        assertTrue(storeFile.exists());
 
        loader.discardSave();
 
        assertFalse(storeFile.exists());
    }

    @Test
    void discardSaveWhenFileDoesNotExistDoesNothing() {
        assertFalse(storeFile.exists());
        assertDoesNotThrow(loader::discardSave); 
    }

    @Test
    void storeWritesAllFieldsInExpectedFormat() {
        stubGameModelForStore(GameModeType.PVE, Color.RED, 3, 2, buildPieces());
 
        loader.store(gameModel);
 
        assertTrue(storeFile.exists());
        assertTrue(storeFile.length() > 0);
    }

    @Test
    void restoreWhenFileDoesNotExistThrows() {
        assertThrows(IllegalStateException.class, () -> loader.restore(gameModel));
    }

    @Test
    void restorePassesReadDataToSetStatus() throws IOException {
        writeSampleFile();

        loader.restore(gameModel);

        // capture arguments passed to setStatus and check them
        final ArgumentCaptor<GameModeType> modeCaptor = ArgumentCaptor.forClass(GameModeType.class);
        final ArgumentCaptor<Color> colorCaptor = ArgumentCaptor.forClass(Color.class);
        final ArgumentCaptor<Integer> redHintsCaptor = ArgumentCaptor.forClass(Integer.class);
        final ArgumentCaptor<Integer> blackHintsCaptor = ArgumentCaptor.forClass(Integer.class);
        @SuppressWarnings("unchecked") 
        final ArgumentCaptor<List<StoredPiece>> piecesCaptor = ArgumentCaptor.forClass(List.class);

        verify(gameModel).setStatus(
                modeCaptor.capture(),
                colorCaptor.capture(),
                redHintsCaptor.capture(),
                blackHintsCaptor.capture(),
                piecesCaptor.capture()
        );

        assertEquals(GameModeType.PVE, modeCaptor.getValue());
        assertEquals(Color.RED, colorCaptor.getValue());
        assertEquals(3, redHintsCaptor.getValue());
        assertEquals(2, blackHintsCaptor.getValue());
        assertEquals(2, piecesCaptor.getValue().size());

        final StoredPiece firstPiece = piecesCaptor.getValue().get(0);
        assertEquals(PieceType.GENERAL, firstPiece.type());
        assertEquals(Color.RED, firstPiece.color());
    }

    @Test
    void restoreWithCorruptedPieceLineThrows() throws IOException {
        try (FileWriter writer = new FileWriter(storeFile, StandardCharsets.UTF_8)) {
            writer.write(PVE_STR);
            writer.write(RED_STR);
            writer.write(THREE_STR);
            writer.write(TWO_STR);
            writer.write(ONE_STR);
            writer.write("GENERAL,RED\n");  // missing col and row
        }

        assertThrows(IllegalStateException.class, () -> loader.restore(gameModel));
    }

    @Test
    void restoreWithTruncatedFileThrows() throws IOException {
        try (FileWriter writer = new FileWriter(storeFile, StandardCharsets.UTF_8)) {
            writer.write(PVE_STR);
            writer.write(RED_STR);
            // file ends prematurely
        }

        assertThrows(IllegalStateException.class, () -> loader.restore(gameModel));
    }

    // round-trip test
    @Test
    void storeAndRestorePreservesData() {
        stubGameModelForStore(GameModeType.PVP, Color.BLACK, 1, 0, buildPieces());

        loader.store(gameModel);

        final GameModel restoredModel = mock(GameModel.class);
        loader.restore(restoredModel);

        verify(restoredModel).setStatus(
                eq(GameModeType.PVP),
                eq(Color.BLACK),
                eq(1),
                eq(0),
                any() // pieces aren't checked 
        );
    }

    private void stubGameModelForStore(final GameModeType mode,
                                       final Color currentColor,
                                       final int redHints,
                                       final int blackHints,
                                       final List<Piece> pieces) {
        when(gameModel.getMode()).thenReturn(mode);
        when(gameModel.getCurrentPlayer())
                .thenReturn(currentColor == Color.RED ? redPlayer : blackPlayer);
        when(redPlayer.getColor()).thenReturn(Color.RED);
        when(blackPlayer.getColor()).thenReturn(Color.BLACK);
        when(gameModel.getPlayers()).thenReturn(List.of(redPlayer, blackPlayer));
        when(gameModel.getHintsRemaining(redPlayer)).thenReturn(redHints);
        when(gameModel.getHintsRemaining(blackPlayer)).thenReturn(blackHints);
        when(gameModel.getBoard()).thenReturn(board);
        when(board.getPieces()).thenReturn(pieces);
    }

    private List<Piece> buildPieces() {
        final Piece general = mock(Piece.class);
        final Position generalPos = mock(Position.class);
        when(general.getType()).thenReturn(PieceType.GENERAL);
        when(general.getOwner()).thenReturn(redPlayer);
        when(general.getPosition()).thenReturn(generalPos); 
        when(generalPos.getCol()).thenReturn(COL1);
        when(generalPos.getRow()).thenReturn(ROW1);

        final Piece cannon = mock(Piece.class);
        final Position cannonPos = mock(Position.class);
        when(cannon.getType()).thenReturn(PieceType.CANNON);
        when(cannon.getOwner()).thenReturn(blackPlayer);
        when(cannon.getPosition()).thenReturn(cannonPos);
        when(cannonPos.getCol()).thenReturn(COL2);
        when(cannonPos.getRow()).thenReturn(ROW2);

        return List.of(general, cannon);
    }

    private void writeSampleFile() throws IOException {
        try (FileWriter writer = new FileWriter(storeFile, StandardCharsets.UTF_8)) {
            writer.write(PVE_STR);
            writer.write(RED_STR);
            writer.write(THREE_STR);
            writer.write(TWO_STR);
            writer.write(TWO_STR);
            writer.write("GENERAL,RED,4,0\n");
            writer.write("CANNON,BLACK,1,7\n");
        }
    }
}
