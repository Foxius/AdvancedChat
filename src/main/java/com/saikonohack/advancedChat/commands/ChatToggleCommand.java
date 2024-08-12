package com.saikonohack.advancedChat.commands;

import com.saikonohack.advancedChat.settings.ChatSettingsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ChatToggleCommand implements CommandExecutor {

    private final ChatSettingsManager chatSettingsManager;

    public ChatToggleCommand(ChatSettingsManager chatSettingsManager) {
        this.chatSettingsManager = chatSettingsManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только игроки могут использовать эту команду.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        switch (label.toLowerCase()) {
            case "global":
                chatSettingsManager.toggleGlobalChat(playerId);
                player.sendMessage("Видимость глобального чата " + (chatSettingsManager.getChatSettings(playerId).isGlobalChatVisible() ? "включена" : "выключена"));
                break;
            case "local":
                chatSettingsManager.toggleLocalChat(playerId);
                player.sendMessage("Видимость локального чата " + (chatSettingsManager.getChatSettings(playerId).isLocalChatVisible() ? "включена" : "выключена"));
                break;
            case "chat":
                chatSettingsManager.toggleAllChat(playerId);
                player.sendMessage("Видимость всего чата " + (chatSettingsManager.getChatSettings(playerId).isAllChatVisible() ? "включена" : "выключена"));
                break;
            default:
                player.sendMessage("Неизвестная команда.");
                break;
        }

        return true;
    }
}
