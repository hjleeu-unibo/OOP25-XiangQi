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

import org.junit.jupiter.api.AfterEach;
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
 * TestGameLoaderImpl
 */
@ExtendWith(MockitoExtension.class)
public final class TestGameLoaderImpl {
    @TempDir
    Path tempDir;

    private File storeFile;
    private GameLoaderImpl loader;

    @Mock private GameModel gameModel;
    @Mock private Board board;
    @Mock private Player redPlayer;
    @Mock private Player blackPlayer;

    @BeforeEach
    void setUp() {

        //costruisco il percorso completo e converte da path a file
        storeFile = tempDir.resolve("test_store.txt").toFile();

        //costruttore private-package per testing
        loader = new GameLoaderImpl(storeFile);
    }

    @AfterEach  //ridondante JUnit con tempDir dovrebbe cancellare in automatico
    void cleanUp() {
        if (storeFile.exists()) {
            storeFile.delete();  //niente residui isolamento
        }
    }

    @Test
    void hasStoredGameWhenFileDoesNotExistReturnsFalse() {
        assertFalse(loader.hasStoredGame());
    }

    @Test
    void hasStoredGameWhenFileIsEmptyReturnsFalse() throws IOException {
        storeFile.createNewFile();   // empty file
        assertFalse(loader.hasStoredGame());
    }

    @Test
    void hasStoredGameWhenFileHasContentReturnsTrue() throws IOException {
        try (FileWriter writer = new FileWriter(storeFile)) {
            writer.write("scacchi"); //pk non usa buffered
        }
        assertTrue(loader.hasStoredGame());
    }

    @Test
    void discardSaveRemovesExistingFile() throws IOException {
        storeFile.createNewFile();
        assertTrue(storeFile.exists());
 
        loader.discardSave();
 
        assertFalse(storeFile.exists());
    }

    //anche senza file da cancellare non deve esplodere
    @Test
    void discardSaveWhenFileDoesNotExistDoesNothing() {
        assertFalse(storeFile.exists());
        //riceve lambda esegue lui e osserva se lancia eccezioni, se 
        //scrivesssi direttamente la funzione avrei void ed eccezione non ci arriverebbe
        assertDoesNotThrow(() -> loader.discardSave()); 
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
        //a sx compile-time a dx run-time quindi scrivo entrambi
        final ArgumentCaptor<GameModeType> modeCaptor = ArgumentCaptor.forClass(GameModeType.class);
        final ArgumentCaptor<Color> colorCaptor = ArgumentCaptor.forClass(Color.class);
        //generici non supportano i prmitivi -> autoboxing
        final ArgumentCaptor<Integer> redHintsCaptor = ArgumentCaptor.forClass(Integer.class);
        final ArgumentCaptor<Integer> blackHintsCaptor = ArgumentCaptor.forClass(Integer.class);
        @SuppressWarnings("unchecked") //a runtime i tipi genereci non ci sono quindi spprimo il warning che partirebbe
        final ArgumentCaptor<List<StoredPiece>> piecesCaptor = ArgumentCaptor.forClass(List.class);

        verify(gameModel).setStatus(//come funziona
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
        try (FileWriter writer = new FileWriter(storeFile)) {
            writer.write("PVE\n");
            writer.write("RED\n");
            writer.write("3\n");
            writer.write("2\n");
            writer.write("1\n");
            writer.write("GENERAL,RED\n");  // missing col and row
        }

        assertThrows(IllegalStateException.class, () -> loader.restore(gameModel));
    }

    @Test
    void restoreWithTruncatedFileThrows() throws IOException {
        try (FileWriter writer = new FileWriter(storeFile)) {
            writer.write("PVE\n");
            writer.write("RED\n");
            // file ends prematurely
        }

        assertThrows(IllegalStateException.class, () -> loader.restore(gameModel));
    }

    //round-trip test
    @Test
    void storeAndRestorePreservesData() {
        stubGameModelForStore(GameModeType.PVP, Color.BLACK, 1, 0, buildPieces());

        loader.store(gameModel);

        GameModel restoredModel = mock(GameModel.class);
        loader.restore(restoredModel);

        //come funziona e perche farla cosi
        verify(restoredModel).setStatus(
                //uso eq pk se non posso mischiare any (matcher) con valori
                eq(GameModeType.PVP),
                eq(Color.BLACK),
                eq(1),
                eq(0),
                any() // non controllo i pezzi 
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
        when(general.getPosition()).thenReturn(generalPos); //pk fare questo e anche col e row
        when(generalPos.getCol()).thenReturn(4);
        when(generalPos.getRow()).thenReturn(0);

        final Piece cannon = mock(Piece.class);
        final Position cannonPos = mock(Position.class);
        when(cannon.getType()).thenReturn(PieceType.CANNON);
        when(cannon.getOwner()).thenReturn(blackPlayer);
        when(cannon.getPosition()).thenReturn(cannonPos);
        when(cannonPos.getCol()).thenReturn(1);
        when(cannonPos.getRow()).thenReturn(7);

        return List.of(general, cannon);
    }

    private void writeSampleFile() throws IOException {
        try (FileWriter writer = new FileWriter(storeFile)) {
            writer.write("PVE\n");
            writer.write("RED\n");
            writer.write("3\n");
            writer.write("2\n");
            writer.write("2\n");
            writer.write("GENERAL,RED,4,0\n");
            writer.write("CANNON,BLACK,1,7\n");
        }
    }
}
