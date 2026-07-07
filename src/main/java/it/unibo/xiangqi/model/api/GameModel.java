package it.unibo.xiangqi.model.api;

import java.util.List;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.GameModeType;
import it.unibo.xiangqi.common.api.GameStatus;

/**
 * The central model of the Xiangqi game.
 * GameModel is the source of truth for the real game state.
 * The controller reads from and writes to this interface.
 */
public interface GameModel {

    /**
     * Initialises the game for the given mode.
     * Creates the board, places all pieces at their starting positions using
     * PieceFactory, and sets up the two players (human/bot depending on mode).
     * The red player always moves first.
     *
     * @param mode PVP or PVE
     */
    void startGame(GameModeType mode);

    /**
     * Ends the game after checkmate is detected.
     * Marks the game as finished so that a resumed load is no longer possible.
     */
    void endGame();

    /**
     * Returns true if the game has ended (endGame() has been called).
     * 
     * @return true if the game ended
     */
    boolean isOver();

    /**
     * Returns the current status of the game.
     * 
     * @return Status of game
     */
    GameStatus getStatus();

    /**
     * Advances the turn to the next player.
     * On the very first call, the red player must go first.
     */
    void switchTurn();

    /**
     * Returns the player whose turn it currently is.
     * 
     * @return player of current turn
     */
    Player getCurrentPlayer();

    // -------------------------------------------------------------------------
    // MOVE
    // -------------------------------------------------------------------------

    /**
     * Applies the given move on the board and records it in the model.
     * Assumes the move has already been validated by RuleEngine.filterLegalMoves().
     * No further legality check is performed here.
     *
     * @param move the move to apply
     * @return true if the move was applied successfully
     */
    boolean movePiece(Move move);

    // -------------------------------------------------------------------------
    // STATE ACCESS
    // -------------------------------------------------------------------------

    /**
     * Returns the current state of the board.
     * 
     * @return Board the current state of board
     */
    Board getBoard();

    /**
     * Returns both players. Red player is at index 0, black at index 1.
     * 
     * @return List of Players
     */
    List<Player> getPlayers();

    /**
     * Returns the current game mode (PVP or PVE).
     * 
     * @return the game mode type
     */
    GameModeType getMode();

    /**
     * Returns an immutable snapshot of the current game state.
     * Used by GameState / MoveCalculator to simulate moves without
     * altering the real model.
     *
     * @return a read-only snapshot of the current GameState
     */
    GameState copyState();

    /**
     * Restores the game model to a previously saved state.
     *
     * @param mode                  the saved game mode PVP or PVE
     * @param currentPlayerColor    the color of the player whose turn it was when saved
     * @param redHints              hints/suggestions saved for the red side
     * @param blackHints            hints/suggestions saved for the black side
     * @param pieces                the saved list of pieces with their positions
     */
    void setStatus(GameModeType mode,
                    Color currentPlayerColor,
                    int redHints,
                    int blackHints,
                    List<StoredPiece> pieces);

    /**
     * Returns the number of hints remaining for the given player.
     *
     * @param player the player to check
     * @return number of hints remaining (0-3)
     */
    int getHintsRemaining(Player player);

    /**
     * Decrements the hint counter for the given player.
     *
     * @param player the player who used a hint
     */
    void useHint(Player player);

    /**
     * Returns the opponent of the player whose turn it currently is.
     *
     * @return the player who is not currently taking their turn
     */
    Player getOpponentPlayer();
}
