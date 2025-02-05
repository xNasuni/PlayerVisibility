package dev.xnasuni.playervisibility.types;

public enum FilterType {
    BLACKLIST("Blacklist"),
    WHITELIST("Whitelist");

    private final String displayName;
    FilterType(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}