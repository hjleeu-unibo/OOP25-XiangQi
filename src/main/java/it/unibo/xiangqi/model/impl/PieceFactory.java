package it.unibo.xiangqi.model.impl;

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

}
