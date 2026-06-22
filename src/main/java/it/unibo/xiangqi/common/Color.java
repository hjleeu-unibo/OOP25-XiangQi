package it.unibo.xiangqi.common;

public enum Color {

    RED("red"),
    BLACK("black"); 

    private String name;

    Color(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
