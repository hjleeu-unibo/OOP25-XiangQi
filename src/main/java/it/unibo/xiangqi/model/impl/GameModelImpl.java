package it.unibo.xiangqi.model.impl;

import java.util.List;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.common.api.GameStatus;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameModel;
import it.unibo.xiangqi.model.api.GameState;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

public class GameModelImpl implements GameModel {

    private Board board;
    private List<Player> players;
    private Player currentPlayer;
    private GameModeType mode;
    private GameStatus status;


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
        this.status = GameStatus.IN_PROGRESS;
        
        // create all pieces at standard starting positions
        final List<Piece> pieces = List.of(
        // black back row
            new Chariot(black, new Position(0, 0)),
            new Horse  (black, new Position(0, 1)),
            new Elephant(black, new Position(0, 2)),
            new Advisor (black, new Position(0, 3)),
            new General (black, new Position(0, 4)),
            new Advisor (black, new Position(0, 5)),
            new Elephant(black, new Position(0, 6)),
            new Horse   (black, new Position(0, 7)),
            new Chariot (black, new Position(0, 8)),
            // black cannons
            new Cannon  (black, new Position(2, 1)),
            new Cannon  (black, new Position(2, 7)),
            // black soldiers
            new Soldier (black, new Position(3, 0)),
            new Soldier (black, new Position(3, 2)),
            new Soldier (black, new Position(3, 4)),
            new Soldier (black, new Position(3, 6)),
            new Soldier (black, new Position(3, 8)),
            // red back row
            new Chariot (red,   new Position(9, 0)),
            new Horse   (red,   new Position(9, 1)),
            new Elephant(red,   new Position(9, 2)),
            new Advisor (red,   new Position(9, 3)),
            new General (red,   new Position(9, 4)),
            new Advisor (red,   new Position(9, 5)),
            new Elephant(red,   new Position(9, 6)),
            new Horse   (red,   new Position(9, 7)),
            new Chariot (red,   new Position(9, 8)),
            // red cannons
            new Cannon  (red,   new Position(7, 1)),
            new Cannon  (red,   new Position(7, 7)),
            // red soldiers
            new Soldier (red,   new Position(6, 0)),
            new Soldier (red,   new Position(6, 2)),
            new Soldier (red,   new Position(6, 4)),
            new Soldier (red,   new Position(6, 6)),
            new Soldier (red,   new Position(6, 8))
        );

        // this.board = new BoardImpl();
        // pieces.forEach(p -> board.setPieceAt(p.getPosition(), p));

    }

    public void endGame() {
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
        return false;
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
        return null;
    }

    @Override
    public void setStatus(GameModeType mode, Color currentPlayerColor, List<Position> redHints,
            List<Position> blackHints, List<Piece> pieces) {
        return;
    }
}
