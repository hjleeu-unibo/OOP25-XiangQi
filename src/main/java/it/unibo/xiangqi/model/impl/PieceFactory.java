package it.unibo.xiangqi.model.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.xiangqi.common.api.Color;
import it.unibo.xiangqi.common.api.PieceType;
import it.unibo.xiangqi.model.api.StoredPiece;
import it.unibo.xiangqi.model.api.Piece;
import it.unibo.xiangqi.model.api.Player;
import it.unibo.xiangqi.model.api.Position;

/**
 * Factory for creating Xiangqi pieces.
 * Centralizes all piece creation logic:
 *   - standard starting positions for a new game
 *   - copy of a piece for simulation
 */
public final class PieceFactory {
    // back rows
    private static final int BACK_ROW_BLACK = 0;
    private static final int BACK_ROW_RED = 9;

    // cannon rows
    private static final int CANNON_ROW_BLACK = 2;
    private static final int CANNON_ROW_RED = 7;
    private static final int CANNON_COL_LEFT = 1;
    private static final int CANNON_COL_RIGHT = 7;

    // soldier rows
    private static final int SOLDIER_ROW_BLACK = 3;
    private static final int SOLDIER_ROW_RED = 6;

    //back row columns
    private static final int CHARIOT_COL_LEFT = 0;
    private static final int HORSE_COL_LEFT = 1;
    private static final int ELEPHANT_COL_LEFT = 2;
    private static final int ADVISOR_COL_LEFT = 3;
    private static final int GENERAL_COL = 4;
    private static final int ADVISOR_COL_RIGHT = 5;
    private static final int ELEPHANT_COL_RIGHT = 6;
    private static final int HORSE_COL_RIGHT = 7;
    private static final int CHARIOT_COL_RIGHT = 8;

    // soldier columns (even columns: 0, 2, 4, 6, 8)
    private static final int[] SOLDIER_COLS = {0, 2, 4, 6, 8};
    private PieceFactory() { } // utility class

    /**
     * Creates all 32 pieces for both players at their standard starting positions.
     *
     * @param red   the red player
     * @param black the black player
     * @return immutable list of all 32 pieces
     */
    public static List<Piece> initializePieces(final Player red, final Player black) {
        return new ArrayList<>(List.of(
            // black pieces:
            new General(black, new Position(BACK_ROW_BLACK, GENERAL_COL)),
            new Advisor(black, new Position(BACK_ROW_BLACK, ADVISOR_COL_LEFT)),
            new Advisor(black, new Position(BACK_ROW_BLACK, ADVISOR_COL_RIGHT)),
            new Elephant(black, new Position(BACK_ROW_BLACK, ELEPHANT_COL_LEFT)),
            new Elephant(black, new Position(BACK_ROW_BLACK, ELEPHANT_COL_RIGHT)),
            new Horse(black, new Position(BACK_ROW_BLACK, HORSE_COL_LEFT)),
            new Horse(black, new Position(BACK_ROW_BLACK, HORSE_COL_RIGHT)),
            new Chariot(black, new Position(BACK_ROW_BLACK, CHARIOT_COL_LEFT)),
            new Chariot(black, new Position(BACK_ROW_BLACK, CHARIOT_COL_RIGHT)),
            new Cannon(black, new Position(CANNON_ROW_BLACK, CANNON_COL_LEFT)),
            new Cannon(black, new Position(CANNON_ROW_BLACK, CANNON_COL_RIGHT)),
            new Soldier(black, new Position(SOLDIER_ROW_BLACK, SOLDIER_COLS[0])),
            new Soldier(black, new Position(SOLDIER_ROW_BLACK, SOLDIER_COLS[1])),
            new Soldier(black, new Position(SOLDIER_ROW_BLACK, SOLDIER_COLS[2])),
            new Soldier(black, new Position(SOLDIER_ROW_BLACK, SOLDIER_COLS[3])),
            new Soldier(black, new Position(SOLDIER_ROW_BLACK, SOLDIER_COLS[4])),
            // red pieces:
            new General(red, new Position(BACK_ROW_RED, GENERAL_COL)),
            new Advisor(red, new Position(BACK_ROW_RED, ADVISOR_COL_LEFT)),
            new Advisor(red, new Position(BACK_ROW_RED, ADVISOR_COL_RIGHT)),
            new Elephant(red, new Position(BACK_ROW_RED, ELEPHANT_COL_LEFT)),
            new Elephant(red, new Position(BACK_ROW_RED, ELEPHANT_COL_RIGHT)),
            new Horse(red, new Position(BACK_ROW_RED, HORSE_COL_LEFT)),
            new Horse(red, new Position(BACK_ROW_RED, HORSE_COL_RIGHT)),
            new Chariot(red, new Position(BACK_ROW_RED, CHARIOT_COL_LEFT)),
            new Chariot(red, new Position(BACK_ROW_RED, CHARIOT_COL_RIGHT)),
            new Cannon(red, new Position(CANNON_ROW_RED, CANNON_COL_LEFT)),
            new Cannon(red, new Position(CANNON_ROW_RED, CANNON_COL_RIGHT)),
            new Soldier(red, new Position(SOLDIER_ROW_RED, SOLDIER_COLS[0])),
            new Soldier(red, new Position(SOLDIER_ROW_RED, SOLDIER_COLS[1])),
            new Soldier(red, new Position(SOLDIER_ROW_RED, SOLDIER_COLS[2])),
            new Soldier(red, new Position(SOLDIER_ROW_RED, SOLDIER_COLS[3])),
            new Soldier(red, new Position(SOLDIER_ROW_RED, SOLDIER_COLS[4]))
        ));
    }

    /**
     * Creates a copy of a piece at its current position.
     * reuses createPiece.
     *
     * @param piece the piece to copy
     * @return a new piece instance with the same type, owner and position
     */
    public static Piece copyPiece(final Piece piece) {
        return createPiece(piece.getType(), piece.getOwner(), piece.getPosition());
    }
    
    /**
     * private base method — the single place where pieces are constructed.
     * 
     * @param type type of piece to create
     * @param owner owner of piece to create
     * @param position position of piece to create
     * @return a piece  instance with the giving type, owner, position
     */
    private static Piece createPiece(final PieceType type,
                                    final Player owner,
                                    final Position position) {
        return switch (type) {
            case GENERAL -> new General(owner, position);
            case ADVISOR -> new Advisor(owner, position);
            case ELEPHANT -> new Elephant(owner, position);
            case HORSE -> new Horse(owner, position);
            case CHARIOT -> new Chariot(owner, position);
            case CANNON -> new Cannon(owner, position);
            case SOLDIER -> new Soldier(owner, position);
        };
    }

    /**
     * create piece from stored piece information.
     * 
     * @param stored a record contains the stored piece information(type, color, position)
     * @param red the red player, assigned as owner if stored color is RED
     * @param black the black player, assigned as owner if stored color is BLACK
     * @return a piece instance matching the stored data
     */
    public static Piece fromStoredPiece(final StoredPiece stored, 
                                        final Player red, 
                                        final Player black) {
        final Player owner = stored.color() == Color.RED ? red : black;
        return createPiece(stored.type(), owner, stored.position());
    }
}
