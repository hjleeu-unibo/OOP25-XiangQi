package it.unibo.xiangqi.model.impl;

import java.util.List;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.common.api.GameStatus;
import it.unibo.xiangqi.common.api.StoredPiece;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameModel;
import it.unibo.xiangqi.model.api.GameState;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;

public class GameModelImpl implements GameModel {
    
    private static final int INITIAL_HINTS = 3;

    private Board board;
    private List<Player> players;
    private Player currentPlayer;
    private GameModeType mode;
    private GameStatus status;
    
    private int redHints;
    private int blackHints; 


    protected GameModelImpl(final Board board, final List<Player> players) {
        this.board = board;
        this.players = players;
        this.currentPlayer = players.get(0); // red starts always at first
        this.status = GameStatus.NOT_STARTED;
    }

    @Override
    public void startGame(GameModeType mode) {
        this.mode = mode;

        // build players based on game mode
        final Player red = new PlayerImpl(Color.RED, true);
        final Player black = new PlayerImpl(Color.BLACK, mode == GameModeType.PVP);

        this.players = List.of(red,black);

        // red always moves first
        this.currentPlayer = players.get(0);

        // both players start with full hints
        this.blackHints = INITIAL_HINTS;
        this.redHints = INITIAL_HINTS;
        
        // create all pieces at standard starting positions
        this.board = Board.createBoard(PieceFactory.initializePieces(red, black));
        this.status = GameStatus.IN_PROGRESS;
    }

    public void endGame() {
        // clear the board
        board.getPieces().forEach(p -> board.deletePiece(p));

        // reset players and current player
        this.players = List.of();
        this.currentPlayer = null;

        // mark game as finished
        this.status = GameStatus.FINISHED;
    }

    public boolean isOver() {
        return this.status == GameStatus.FINISHED;
    }

    public GameStatus getStatus() {
        return this.status;
    }

    public void switchTurn() {
        final int currentIndex = players.indexOf(currentPlayer);
        final int nextIndex = (currentIndex + 1) % players.size();
        currentPlayer = players.get(nextIndex);
    }

    @Override
    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    @Override
    public boolean movePiece(Move move) {
        final Piece piece = board.getPieceAt(move.getFrom());
        final Piece captured = board.getPieceAt(move.getTo());

        // remove captured enemy piece if present
        if (captured != null) {
            board.deletePiece(captured);
        }

        // update piece's internal position
        piece.setPosition(move.getTo());
        return piece.getPosition().equals(move.getTo());
    }

    @Override
    public Board getBoard() {
        return this.board;
    }

    @Override
    public List<Player> getPlayers() {
        return this.players;
    }

    @Override
    public GameModeType getMode() {
        return this.mode;
    }

    @Override
    public GameState copyState() {
        final List<Piece> copiedPieces = board.getPieces().stream()
            .map(PieceFactory::copyPiece)
            .toList();
        return GameState.createGameState(Board.createBoard(copiedPieces), this.players, this.currentPlayer);
    }

    @Override
    public void setStatus(GameModeType mode, Color currentPlayerColor, int redHints,
            int blackHints, List<StoredPiece> storedPieces) {
                this.mode = mode;

                // build players from mode
                final Player red   = new PlayerImpl(Color.RED,   true);
                final Player black = new PlayerImpl(Color.BLACK,  mode == GameModeType.PVP);
                this.players = List.of(red, black);

                // restore current player by matching the saved color
                this.currentPlayer = this.players.stream()
                .filter(p -> p.getColor() == currentPlayerColor)
                .findFirst()
                .orElse(red);

                // restore board information
                final List<Piece> pieces = storedPieces.stream()
                .map(s -> PieceFactory.fromStoredPiece(s, red, black))
                .toList();

                this.board = Board.createBoard(pieces);

                // restore hints information
                this.redHints = redHints;
                this.blackHints = blackHints;

                this.status = GameStatus.IN_PROGRESS;
        return;
    }

    @Override
    public int getHintsRemaining(Player player) {
        return player.getColor() == Color.RED ? redHints : blackHints; 
    }

    @Override
    public void useHint(Player player) {
        if (player.getColor() == Color.RED) {
            redHints--;
        } else{
            blackHints--;
        }
    }
}
