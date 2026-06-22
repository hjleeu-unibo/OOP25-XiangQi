package it.unibo.xiangqi.common;

public enum PieceType {

    GENERAL("G"),
    ADVISOR("A"),
    ELEPHANT("E"),
    HORSE("H"),
    CHARIOT("R"),
    CANNON("C"),
    SOLDIER("S");

    private final String symbol;

    PieceType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}