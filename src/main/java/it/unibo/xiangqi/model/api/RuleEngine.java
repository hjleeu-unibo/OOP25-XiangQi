package it.unibo.xiangqi.model.api;

import java.util.List;

/**
 * Describe the rules of the Xiangqi game.
 * Provides the logic for check, checkmate, draw,flying general
 * detection and legal moves selection.
 */
public interface RuleEngine {
    /**
     * Filters a list of candidate moves, keeping only legal ones.
     * A move is illegal if after simulating it:
     * - the player's General is under attack OR
     * - the two Generals face each other with no pieces between them
     * 
     * @param piece the piece whose legal moves are filtered
     * @param board the current board state
     * @return list of legal moves for the piece
     */
    List<Move> getLegalMoves(Piece piece, Board board);

    /**
     * Returns true if an enemy piece can capture the player's
     * General on the next move.
     * 
     * @param player the player to check
     * @param board  the current board state
     * @return true if the player is in check
     */
    boolean isCheck(Player player, Board board);

    /**
     * Returns true if the player is in check and has no legal move
     * that resolves it.
     * 
     * @param player the player to check
     * @param board  the current board state
     * @return true if the player is in checkmate
     */
    boolean isCheckMate(Player player, Board board);

    /**
     * The players are in a DRAW condtion when both of them 
     * have only defensive pieces left. 
     * 
     * @param board   the current board state
     * @return true if the game is a draw
     */
    boolean isDraw(Board board);
}
