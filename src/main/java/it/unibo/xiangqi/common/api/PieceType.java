package it.unibo.xiangqi.common.api;

/**
 * temp 
 * 
 * @hidden
 */
public enum PieceType {

    GENERAL("general"),
    ADVISOR("advisor"),
    ELEPHANT("elephant"),
    HORSE("horse"),
    CHARIOT("chariot"),
    CANNON("cannon"),
    SOLDIER("soldier");

    private String name;

    PieceType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}