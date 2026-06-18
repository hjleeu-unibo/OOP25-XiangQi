package it.unibo.xiangqi.common.api;

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
