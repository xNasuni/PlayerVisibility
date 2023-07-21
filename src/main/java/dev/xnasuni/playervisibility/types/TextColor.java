package dev.xnasuni.playervisibility.types;

import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import org.jetbrains.annotations.NotNull;

public enum TextColor implements SelectionListEntry.Translatable {
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

    private final String DisplayName;
    private final char ColorCharacter;

    TextColor(String DisplayName, char ColorCharacter)
    {
        this.DisplayName = DisplayName;
        this.ColorCharacter = ColorCharacter;
    }

    public @NotNull String GetDisplayName() {
        return this.DisplayName;
    }

    public char GetChar() {
        return this.ColorCharacter;
    }

    @Override
    public @NotNull String getKey() {
        return String.format("§%s%s", this.ColorCharacter, this.DisplayName);
    }
}
