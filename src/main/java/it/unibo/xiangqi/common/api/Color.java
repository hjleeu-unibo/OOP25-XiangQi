package it.unibo.xiangqi.common;

public enum Color {

    RED("R"),
    BLACK("B"); 

    private final String symbol;

    Color(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
