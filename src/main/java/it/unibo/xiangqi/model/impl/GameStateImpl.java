package it.unibo.xiangqi.model.impl;

import java.util.Objects;

import it.unibo.xiangqi.common.Move;
import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameState;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;

/**
 * Concrete implementation of the {@link GameState} interface.
 *
 * This class stores the current board configuration and manages
 * the player turn during the game.
 */
public class GameStateImpl implements GameState{

    private Board board; 
    private Player player1; 
    private Player player2; 
    private Player currentPlayer; 

    /**
     * Creates a new game state with the specified board and players.
     *
     * @param board the board associated with the game
     * @param player1 the first player
     * @param player2 the second player
     * @param currentPlayer the player whose turn it is
     * @throws NullPointerException if any argument is {@code null}
     */
    public GameStateImpl(Board board, Player player1, Player player2, Player currentPlayer) {
        Objects.requireNonNull(board);
        Objects.requireNonNull(player1);
        Objects.requireNonNull(player2);
        Objects.requireNonNull(currentPlayer);

        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = currentPlayer; 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board getBoard() {
        return this.board; 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getCurrentPlayer() {
        return this.currentPlayer; 
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * Switches the active player.
     *
     * @hidden
     */
    private void switchTurn(){
        if(player1.equals(currentPlayer)){
            this.currentPlayer = player2; 
        }else{
            this.currentPlayer = player1; 
        }
    }
    
}
