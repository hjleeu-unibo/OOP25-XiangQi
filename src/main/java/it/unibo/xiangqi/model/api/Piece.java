package it.unibo.xiangqi.model.api;

import java.util.List;

import it.unibo.xiangqi.common.api.PieceType;
 
/**
 * Represents a Xiangqi piece on the board.
 */
public interface Piece {
    /**
     * Returns the type of this piece (i.e. HORSE, CANNON, GENERAL...).
     * 
     * @return the type of that piece
     */
    PieceType getType();

    /**
     * Returns the player who owns this piece.
     * 
     * @return the owner
     */
    Player getOwner();

    /**
     * Returns the current position of this piece on the board.
     * 
     * @return the position of the piece
     */
    Position getPosition();

    /**
     * Moves this piece to a new position.
     * 
     * @param position the new position
     */
    void setPosition(Position position);

    /**
     * Returns all legal moves for this piece given the current board state.
     * 
     * @param board the current board
     * @return list of legal moves
     */
    List<Move> getMoves(Board board);

    /**
     * Returns true if this piece has a defensive role
     * (i.e. Advisor or Elephant, which protect the King).
     * 
     * @return if the piece is a defensor piece
     */
    boolean isDefensor();

    /**
     * Returns the strategic value of this piece, used by the AI scoring system.
     * Each subclass defines its own value.
     * Default values:
     *   General          = 1000
     *   Chariot          = 90
     *   Cannon           = 45
     *   Horse            = 40
     *   Advisor          = 20
     *   Elephant         = 20
     *   Soldier          = 10
     *
     * @return the piece value
     */
    int getInitialValue();

    /**
     * Returns the current strategic value of this piece, which may differ
     * from the initial value depending on the board situation
     * (e.g. a Soldier crossing the river).
     *
     * @return the current piece value
     */
    int getCurrentValue();

    /**
     * Updates the strategic value of this piece.
     * Intended to be called by the AI (MoveCalculator) to adjust
     * the value dynamically during the game (e.g. soldier crossing the river).
     *
     * @param value the new strategic value
     */
    void setValue(int value);
}
