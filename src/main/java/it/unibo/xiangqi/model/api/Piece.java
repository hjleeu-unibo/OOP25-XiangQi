package it.unibo.xiangqi.model.api;

import java.util.List;

import it.unibo.xiangqi.common.api.PieceType;
 
/**
 * Represents a Xiangqi piece on the board.
 */
public interface Piece {

    /**
     * Creates a new instance of this piece with the given parameters.
     * Each concrete piece class implements this as a factory method.
     *
     * @param type             the type of the piece
     * @param owner            the player who owns the piece
     * @param startingPosition the initial position on the board
     * @return a new Piece instance
     */
    public Piece createPiece(PieceType type, Player owner, Position startingPosition );
    
    /**
     * Returns the type of this piece (i.e. HORSE, CANNON, GENERAL...).
     */
    public PieceType getType();

    /**
     * Returns the player who owns this piece.
     */
    public Player getOwner();
    
    /**
     * Returns the current position of this piece on the board.
     */
    public Position getPosition();
    
    /**
     * Moves this piece to a new position.
     *
     * @param position the new position
     */
    public void setPosition(Position position);
    
     /**
     * Returns all legal moves for this piece given the current board state.
     *
     * @param board the current board
     * @return list of legal moves
     */
    public List<Move> getMoves(Board board);
    
    /**
     * Returns true if this piece has a defensive role
     * (i.e. Advisor or Elephant, which protect the King).
     */
    public Boolean isDefensor();

    /**
     * Returns the strategic value of this piece, used by the AI scoring system.
     * Each subclass defines its own value.
     *
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
    public int getInitialValue();

   /**
     * Returns the current strategic value of this piece, which may differ
     * from the initial value depending on the board situation
     * (e.g. a Soldier crossing the river).
     *
     * @return the current piece value
     */
    public int getCurrentValue();

    /**
    * Updates the strategic value of this piece.
    * Intended to be called by the AI (MoveCalculator) to adjust
    * the value dynamically during the game (e.g. soldier crossing the river).
    *
    * @param value the new strategic value
    */
    public void setValue(final int value);
}