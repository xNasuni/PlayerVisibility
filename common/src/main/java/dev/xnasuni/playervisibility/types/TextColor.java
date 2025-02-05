package dev.xnasuni.playervisibility.types;

public enum TextColor {
    BLACK("Black", '0'),
    DARK_BLUE("Dark Blue", '1'),
    DARK_GREEN("Dark Green", '2'),
    DARK_AQUA("Dark Aqua", '3'),
    DARK_RED("Dark Red", '4'),
    DARK_PURPLE("Dark Purple", '5'),
    GOLD("Gold", '6'),
    GRAY("Gray", '7'),
    DARK_GRAY("Dark Gray", '8'),
    BLUE("Blue", '9'),
    GREEN("Green", 'a'),
    AQUA("Aqua", 'b'),
    RED("Red", 'c'),
    LIGHT_PURPLE("Magenta", 'd'),
    YELLOW("Yellow", 'e'),
    WHITE("White", 'f');

    private final String displayName;
    private final char colorCharacter;

    TextColor(String displayName, char colorCharacter)
    {
        this.displayName = displayName;
        this.colorCharacter = colorCharacter;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public char getChar() {
        return this.colorCharacter;
    }
}