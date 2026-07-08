package it.unibo.xiangqi.model.impl;

import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameState;
import it.unibo.xiangqi.model.api.Move;
import it.unibo.xiangqi.model.api.Player;

public class FakeGameStateImpl implements GameState {

    private Board board; 
    private Player player1; 
    private Player player2; 
    private Player currentPlayer; 

    public FakeGameStateImpl(Board board, Player player1, Player player2, Player currentPlayer) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = currentPlayer;
    }
    @Override
    public Board getBoard() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBoard'");
    }

    @Override
    public Player getCurrentPlayer() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCurrentPlayer'");
    }

    @Override
    public GameState applyTurn(Move move) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'applyTurn'");
    }

}
