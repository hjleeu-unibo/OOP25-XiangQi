package it.unibo.xiangqi.model.api;

import java.util.List;

import it.unibo.xiangqi.common.Move;
import it.unibo.xiangqi.model.impl.GameStateImpl;

/**
 * Represents the current state of a Xiangqi game.
 * A game state stores the board configuration and the player whose turn it is.
 */
public interface GameState {
    /**
     * Creates a new game state.
     *
     * @param board the game board
     * @param players the list containing the two players
     * @param currentPlayer the player whose turn it is
     * @return a new game state
     * @throws NullPointerException if any argument is {@code null}
     */
    static GameState createGameState(Board board, List<Player> players, Player currentPlayer){
        return new GameStateImpl(board, players.get(0), players.get(1), currentPlayer); 
    }

    /**
     * Returns the board associated with this game state.
     *
     * @return the current board
     */
    Board getBoard(); 

    /**
     * Returns the player whose turn it is.
     *
     * @return the current player
     */
    Player getCurrentPlayer(); 

    /**
     * Applies the specified move and returns the resulting game state.
     *
     * @param move the move to apply
     * @return the game state resulting from the move
     * @throws NullPointerException if {@code move} is {@code null}
     */
    GameState applyMove(Move move); 
}