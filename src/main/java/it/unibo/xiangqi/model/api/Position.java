package it.unibo.xiangqi.model.api;

import java.util.Objects;

/**
 * Rapresenting a cell in the game board.
 * Position
 */
public final class Position {

    /** Number of rows on a standard Xiangqi board. */
    public static final int ROWS = 10;
 
    /** Number of columns on a standard Xiangqi board. */
    public static final int COLS = 9;

    private final int row;
    private final int col;

    /**
     * Constructor.
     * 
     * @param row the row number
     * @param col the col number
     */
    public Position(final int row, final int col) {
        if (row < 0 || row >= ROWS) {
            throw new IllegalArgumentException(
                String.format("Row out of bounds: %d (must be 0-%d).", row, ROWS - 1)
            );
        }
        if (col < 0 || col >= COLS) {
            throw new IllegalArgumentException(
                String.format("Column out of bounds: %d (must be 0-%d).", col, COLS - 1)
            );
        }
        this.row = row;
        this.col = col;
    }

    /**
     * Returns the row number.
     * 
     * @return row number
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column number.
     * 
     * @return the column number
     */
    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position)) {
            return false;
        }
        final Position p = (Position) o;
        return row == p.row && col == p.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}
