package it.unibo.xiangqi.model.impl;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.GameLoader;
import it.unibo.xiangqi.model.api.GameModel;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;
import it.unibo.xiangqi.model.api.StoredPiece;
 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the game loader.
 * Saves and loads game data using a text-based save file stored
 * in the user's home directory.
 * The save file is organized as follows:
 * 
 * <pre>
 * line 1: game mode
 * line 2: current player color
 * line 3: remaining red hints
 * line 4: remaining black hints
 * line 5: total number of pieces
 * line 6+: piece description-> TYPE,COLOR,X,Y
 * </pre>
 * 
 * <p>In PVE mode the red player is always the human and the black 
 * player is the bot.</p>
 */
public final class GameLoaderImpl implements GameLoader {

    private static final String FILE_NAME = "xiangqi_store.txt";
    private static final String SEPARATOR = ",";
    private static final int PIECE_FIELDS = 4;

    private final File storeFile;

    /**
     * Builds a new game loader and initializes the store file location.
     */
    public GameLoaderImpl() {
        final String userHome = System.getProperty("user.home");
        this.storeFile = new File(userHome, FILE_NAME);
    }

    //for testing purpose  è package private
    GameLoaderImpl(final File storeFile) {
    this.storeFile = storeFile;
    }

    //da modificare col e row -> in base costruttore
    @Override
    public void store(final GameModel gameModel) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(storeFile))) {

            writer.write(gameModel.getMode().name());
            writer.newLine();

            writer.write(gameModel.getCurrentPlayer().getColor().name());
            writer.newLine();

            final Player redPlayer = getPlayer(gameModel.getPlayers(), Color.RED);
            final Player blackPlayer = getPlayer(gameModel.getPlayers(), Color.BLACK);

            writer.write(Integer.toString(gameModel.getHintsRemaining(redPlayer)));
            writer.newLine();
            
            writer.write(Integer.toString(gameModel.getHintsRemaining(blackPlayer)));
            writer.newLine();

            final List<Piece> pieces = gameModel.getBoard().getPieces();
            writer.write(Integer.toString(pieces.size()));
            writer.newLine();

            for (final Piece piece : pieces) {
                writer.write(pieceToLine(piece));
                writer.newLine();
            }
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to store current game state", e);
        }
    }

    @Override
    public void restore(final GameModel gameModel) {
        if (!hasStoredGame()) {
            throw new IllegalStateException("No stored game is available.");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(storeFile))) {
            final GameModeType mode = GameModeType.valueOf(safeReadLine(reader));
            final Color currentPlayerColor = Color.valueOf(safeReadLine(reader));
            final int redHints = Integer.parseInt(safeReadLine(reader));
            final int blackHints = Integer.parseInt(safeReadLine(reader));
            final int piecesCount = Integer.parseInt(safeReadLine(reader));

            final List<StoredPiece> storedPieces = new ArrayList<>();  //da modificare qui
            for (int i = 0; i < piecesCount; i++) {
                storedPieces.add(parsePiece(safeReadLine(reader)));
            }

            gameModel.setStatus(mode, currentPlayerColor, redHints, blackHints, storedPieces);
        } catch (final IOException e) {
            throw new IllegalStateException("The game restore has failed.", e);
        }
    }

    @Override
    public void discardSave() {
        if (storeFile.exists() && !storeFile.delete()) {
            throw new IllegalStateException("Unable to delete the store file");
        }
    }

    @Override
    public boolean hasStoredGame() {
        return storeFile.exists() && storeFile.length() > 0;
    }

    //returns the player of the given color from the list of players
    private Player getPlayer(final List<Player> players, final Color color) {
        for (final Player player : players) {
            if (player.getColor() == color) {
                return player;
            }
        }
        throw new IllegalStateException("Player " + color + " not found");
    }

    //converts the main fields of a Piece into a string
    private String pieceToLine(final Piece piece) {
        return piece.getType().name() 
                    + SEPARATOR + piece.getOwner().getColor().name()
                    + SEPARATOR + piece.getPosition().getCol()
                    + SEPARATOR + piece.getPosition().getRow();
    }

    //reads the next line and rejects an unexpected end of file
    private String safeReadLine(final BufferedReader reader) throws IOException {
        final String line = reader.readLine();
        if (line == null) {
            throw new IllegalStateException("Unexpected end of file");
        }

        return line;
    }

    private StoredPiece parsePiece(final String line) {
        final String[] fields = line.split(SEPARATOR);
        if (fields.length != PIECE_FIELDS) {
            throw new IllegalStateException("Invalid piece description");
        }

        final PieceType type = PieceType.valueOf(fields[0]);
        final Color color = Color.valueOf(fields[1]);
        final int col = Integer.parseInt(fields[2]);
        final int row = Integer.parseInt(fields[3]);

        return new StoredPiece(type, color, new Position(row, col));
    }
}
