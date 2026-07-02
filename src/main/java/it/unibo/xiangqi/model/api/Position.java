package it.unibo.xiangqi.model.api;

import java.util.Objects;

public final class Position {

    /** Number of rows on a standard Xiangqi board. */
    public static final int ROWS = 10;
 
    /** Number of columns on a standard Xiangqi board. */
    public static final int COLS = 9;

    private final int row;
    private final int col;

    public Position(int row, int col) {
        if (row < 0 || row >= ROWS) {
            throw new IllegalArgumentException(
                "Row out of bounds: " + row + " (must be 0–" + (ROWS - 1) + ")");
        }
        if (col < 0 || col >= COLS) {
            throw new IllegalArgumentException(
                "Column out of bounds: " + col + " (must be 0–" + (COLS - 1) + ")");
        }
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return row == p.row && col == p.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row,col);
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}