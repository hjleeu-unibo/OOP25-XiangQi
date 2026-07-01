package it.unibo.xiangqi.model.api;

import java.util.List;

import it.unibo.xiangqi.common.GameModeType;
import it.unibo.xiangqi.common.GameStatus;
import it.unibo.xiangqi.common.Move;

/**
 * The central model of the Xiangqi game.
 *
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
    public void startGame(GameModeType mode);

    /**
     * Ends the game after checkmate is detected.
     * Marks the game as finished so that a resumed load is no longer possible.
     */
    public void endGame();

    /**
     * Returns true if the game has ended (endGame() has been called).
     */
    public boolean isOver();

    /**
     * Returns the current status of the game.
     */
    public GameStatus getStatus();

    /**
     * Advances the turn to the next player.
     * On the very first call, the red player must go first.
     */
    public void switchTurn();

    /**
     * Returns the player whose turn it currently is.
     */
    public Player getCurrentPlayer();

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
    public boolean movePiece(Move move);

    // -------------------------------------------------------------------------
    // STATE ACCESS
    // -------------------------------------------------------------------------

    /**
     * Returns the current state of the board.
     */
    public Board getBoard();

    /**
     * Returns both players. Red player is at index 0, black at index 1.
     */
    public List<Player> getPlayers();

    /**
     * Returns the current game mode (PVP or PVE).
     */
    public GameModeType getMode();

    /**
     * Returns an immutable snapshot of the current game state.
     * Used by GameState / MoveCalculator to simulate moves without
     * altering the real model.
     *
     * @return a read-only snapshot of the current GameState
     */
    public GameState copyState();

    /**
     * Restores the game model to a previously saved state.
     *
     * @param mode          the saved game mode
     * @param players       the saved player list
     * @param currentPlayer the player whose turn it was when the game was saved
     * @param board         the saved board
     * @param pieces        the saved list of pieces with their positions
     */
    void setStatus(GameModeType mode,
                   List<Player> players,
                   Player currentPlayer,
                   Board board,
                   List<Piece> pieces);
}