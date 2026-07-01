package it.unibo.xiangqi.model.impl;

import java.util.List;

import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;

/**
 * Factory for creating Xiangqi pieces.
 * Centralizes all piece creation logic:
 *   - standard starting positions for a new game
 *   - copy of a piece for simulation
 */
public final class PieceFactory {
    
    // back rows
    private static final int BACK_ROW_BLACK = 0;
    private static final int BACK_ROW_RED   = 9;

    // cannon rows
    private static final int CANNON_ROW_BLACK = 2;
    private static final int CANNON_ROW_RED   = 7;
    private static final int CANNON_COL_LEFT  = 1;
    private static final int CANNON_COL_RIGHT = 7;

    // soldier rows
    private static final int SOLDIER_ROW_BLACK = 3;
    private static final int SOLDIER_ROW_RED   = 6;

    //back row columns
    private static final int COL_CHARIOT_LEFT   = 0;
    private static final int COL_HORSE_LEFT     = 1;
    private static final int COL_ELEPHANT_LEFT  = 2;
    private static final int COL_ADVISOR_LEFT   = 3;
    private static final int COL_GENERAL        = 4;
    private static final int COL_ADVISOR_RIGHT  = 5;
    private static final int COL_ELEPHANT_RIGHT = 6;
    private static final int COL_HORSE_RIGHT    = 7;
    private static final int COL_CHARIOT_RIGHT  = 8;

    // soldier columns (even columns: 0, 2, 4, 6, 8)
    private static final int[] SOLDIER_COLS = {0, 2, 4, 6, 8};
    
    private PieceFactory() {} // utility class

    /**
     * Creates all 32 pieces for both players at their standard starting positions.
     *
     * @param red   the red player
     * @param black the black player
     * @return immutable list of all 32 pieces
     */
    public static List<Piece> initializePieces(final Player red, final Player black) {
        return List.of(
            // black pieces:
            
            // red pieces:
        );
    }

    /**
     * Creates a copy of a piece at its current position.
     *
     * @param p the piece to copy
     * @return a new piece instance with the same type, owner and position
     */
    public static Piece copyPiece(final Piece p) {
        return null;
    }
}
