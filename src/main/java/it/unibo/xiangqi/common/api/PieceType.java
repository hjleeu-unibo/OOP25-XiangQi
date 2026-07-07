package it.unibo.xiangqi.common.api;

/**
 * The type that a piece can be.
 * PieceType
 */
public enum PieceType {
    GENERAL("general"),
    ADVISOR("advisor"),
    ELEPHANT("elephant"),
    HORSE("horse"),
    CHARIOT("chariot"),
    CANNON("cannon"),
    SOLDIER("soldier");

    private final String name;

    PieceType(final String name) {
        this.name = name;
    }

    /**
     * Get the name of that type.
     * 
     * @return the type name
     */
    public String getName() {
        return this.name;
    }
}
