package it.unibo.xiangqi.model.api;

import java.util.List;

import it.unibo.xiangqi.model.impl.FakeGameStateImpl;

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
    static GameState createGameState(final Board board, final List<Player> players, final Player currentPlayer) {
        return new FakeGameStateImpl(board, players.get(0), players.get(1), currentPlayer); 
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
     * Applies the specified move and advances to the next player's turn.
     *
     * @param move the move to apply
     * @return the game state resulting from the move, with the turn switched
     *         to the other player
     * @throws NullPointerException if {@code move} is {@code null}
     */
    GameState applyTurn(Move move);
}
