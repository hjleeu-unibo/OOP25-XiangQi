package it.unibo.xiangqi.model.impl;

import java.util.List;

import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameModel;
import it.unibo.xiangqi.model.api.GameState;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;

public class GameModelImpl implements GameModel {

    private Board board;
    private List<Player> players;
    private Player currentPlayer;
    private GameModeType mode;
    private boolean gameOver;


    protected GameModelImpl(final Board board, final List<Player> players) {
        this.board = board;
        this.players = players;
        this.currentPlayer = players.get(0); // red starts always at first
        this.gameOver = false;
    }

    @Override
    public void startGame(GameModeType mode) {
        this.mode = mode;
        this.currentPlayer = players.get(0);
        this.gameOver = false;
    }

    public void endGame() {
       this.gameOver = true;
    }

    public boolean isOver() {
        return gameOver;
    }

    public void switchTurn() {
       return;
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
    public void setStatus(GameModeType mode, List<Player> players, Player currentPlayer, Board board,
            List<Piece> pieces) {
        this.mode = mode;
        this.players = players;
        this.currentPlayer = currentPlayer;
        this.board = board;
        this.gameOver = false;
    }

}
