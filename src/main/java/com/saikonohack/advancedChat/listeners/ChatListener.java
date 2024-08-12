package com.saikonohack.advancedChat.listeners;

import com.saikonohack.advancedChat.chat.ChatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final ChatManager chatManager;

    public ChatListener(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        event.setCancelled(true);

        chatManager.handleChat(player, message);
    }
}
