package com.saikonohack.advancedChat.settings;

public class ChatSettings {

    private boolean globalChatVisible = true;
    private boolean localChatVisible = true;
    private boolean allChatVisible = true;

    public boolean isGlobalChatVisible() {
        return globalChatVisible;
    }

    public void setGlobalChatVisible(boolean globalChatVisible) {
        this.globalChatVisible = globalChatVisible;
    }

    public boolean isLocalChatVisible() {
        return localChatVisible;
    }

    public void setLocalChatVisible(boolean localChatVisible) {
        this.localChatVisible = localChatVisible;
    }

    public boolean isAllChatVisible() {
        return allChatVisible;
    }

    public void setAllChatVisible(boolean allChatVisible) {
        this.allChatVisible = allChatVisible;
    }
}
