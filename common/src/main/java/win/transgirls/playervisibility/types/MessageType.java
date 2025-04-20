package win.transgirls.playervisibility.types;

public enum MessageType {
    ACTION_BAR("Action Bar"),
    CHAT_MESSAGE("Chat Message"),
    HIDDEN("Hidden");

    private final String displayName;
    MessageType(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}