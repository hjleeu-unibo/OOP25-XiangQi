package it.unibo.xiangqi.common.api;

/**
 * An enum class that defines the players and pieces color.
 * Color
 */
public enum Color {

    RED("red"),
    BLACK("black"); 

    private final String name;

    Color(final String name) {
        this.name = name;
    }

    /**
     * Returns the color name.
     * 
     * @return the color name
     */
    public String getName() {
        return this.name;
    }
}
