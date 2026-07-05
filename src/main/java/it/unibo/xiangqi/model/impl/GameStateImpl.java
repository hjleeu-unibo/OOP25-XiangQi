package it.unibo.xiangqi.model.impl;

import java.util.List;
import java.util.Objects;

import it.unibo.xiangqi.model.api.Board;
import it.unibo.xiangqi.model.api.GameState;
import it.unibo.xiangqi.model.api.Move;
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

        //copy the board to simulate on
        List<Piece> copiedPieces = board.getPieces().stream()
                                                    .map(PieceFactory::copyPiece)
                                                    .toList();
        Board simulationBoard = Board.createBoard(copiedPieces);

        // apply the move on the board's copy
        if (simulationBoard.getPieceAt(move.getTo()) != null) {
            simulationBoard.deletePiece(simulationBoard.getPieceAt(move.getTo()));
        }
        Piece piece = Objects.requireNonNull(simulationBoard.getPieceAt(move.getFrom()));
        piece.setPosition(move.getTo());

        GameStateImpl simulation = new GameStateImpl(simulationBoard, player1, player2, currentPlayer);
        simulation.switchTurn();

        return simulation;

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
