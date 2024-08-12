package com.saikonohack.advancedChat.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatSettingsManager {

    private final Map<UUID, ChatSettings> playerChatSettings = new HashMap<>();

    public ChatSettings getChatSettings(UUID playerId) {
        return playerChatSettings.computeIfAbsent(playerId, id -> new ChatSettings());
    }

    public void toggleGlobalChat(UUID playerId) {
        ChatSettings settings = getChatSettings(playerId);
        settings.setGlobalChatVisible(!settings.isGlobalChatVisible());
    }

    public void toggleLocalChat(UUID playerId) {
        ChatSettings settings = getChatSettings(playerId);
        settings.setLocalChatVisible(!settings.isLocalChatVisible());
    }

    public void toggleAllChat(UUID playerId) {
        ChatSettings settings = getChatSettings(playerId);
        settings.setAllChatVisible(!settings.isAllChatVisible());
    }
}
