package it.unibo.xiangqi.model.impl;

import java.util.Objects;

import it.unibo.xiangqi.common.Move;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameState;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;

public class GameStateImpl implements GameState{

    private Board board; 
    private Player player1; 
    private Player player2; 
    private Player currentPlayer; 

    public GameStateImpl(Board board, Player player1, Player player2, Player currentPlayer) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = currentPlayer; 
    }

    @Override
    public Board getBoard() {
        return this.board; 
    }

    @Override
    public Player getCurrentPlayer() {
        return this.currentPlayer; 
    }

    @Override
    public GameState applyMove(Move move) {
        Objects.requireNonNull(move); 
        if(this.board.getPieceAt(move.getTo()) != null){
            this.board.deletePiece(this.board.getPieceAt(move.getTo()));
        }
        Piece piece = Objects.requireNonNull(this.board.getPieceAt(move.getFrom()));
        piece.setPosition(move.getTo());
        this.switchTurn();
        return this; 
    }

    private void switchTurn(){
        if(player1.equals(currentPlayer)){
            this.currentPlayer = player2; 
        }else{
            this.currentPlayer = player1; 
        }
    }
    
}
