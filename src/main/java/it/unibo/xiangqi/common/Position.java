package it.unibo.xiangqi.common;

/**
 * temp 
 * 
 * @hidden
 */
public class Position {

    private int row; 
    private int col; 

    public Position(int row, int col) {
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
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Position)) {
            return false;
        }

        Position other = (Position) obj;

        return this.row == other.getRow()
            && this.col == other.getCol();
    }
       
}