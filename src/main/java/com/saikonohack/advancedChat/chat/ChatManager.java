package com.saikonohack.advancedChat.chat;

import com.saikonohack.advancedChat.main.AdvancedChat;
import com.saikonohack.advancedChat.settings.ChatSettings;
import com.saikonohack.advancedChat.settings.ChatSettingsManager;
import com.saikonohack.advancedChat.utils.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatManager {

    private final AdvancedChat plugin;

    public ChatManager(AdvancedChat plugin, ChatSettingsManager chatSettingsManager) {
        this.plugin = plugin;
    }

    public void handleChat(Player sender, String message) {
        Component formattedMessage;

        if (message.startsWith("!")) {
            message = message.substring(1).trim();
            formattedMessage = formatGlobalMessage(sender, message);
            broadcastGlobalChat(sender, formattedMessage);
        } else {
            formattedMessage = formatLocalMessage(sender, message);
            broadcastLocalChat(sender, formattedMessage);
        }
    }

    private Component formatGlobalMessage(Player player, String message) {
        PlayerProfile profile = new PlayerProfile(player, plugin);
        Component senderComponent = profile.getPlayerNameComponent();
        MiniMessage miniMessage = MiniMessage.miniMessage();
        String format = plugin.getConfig().getString("chat.global_message_format", "<gold>● <yellow><sender><gold> >> <white><message>");
        return formatMessage(senderComponent, message, format, miniMessage);
    }

    private Component formatLocalMessage(Player player, String message) {
        PlayerProfile profile = new PlayerProfile(player, plugin);
        Component senderComponent = profile.getPlayerNameComponent();
        MiniMessage miniMessage = MiniMessage.miniMessage();
        String format = plugin.getConfig().getString("chat.local_message_format", "<red>● <yellow><sender><red> >> <white><message>");
        return formatMessage(senderComponent, message, format, miniMessage);
    }

    private Component formatMessage(Component senderComponent, String message, String format, MiniMessage miniMessage) {
        Component baseMessage = miniMessage.deserialize(format.replace("<message>", message));
        return baseMessage.replaceText(builder -> builder.matchLiteral("<sender>").replacement(senderComponent));
    }

    private void broadcastGlobalChat(Player sender, Component message) {
        boolean someoneHeard = false;
        sender.sendMessage(message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.equals(sender)) {
                ChatSettings settings = plugin.getChatSettingsManager().getChatSettings(player.getUniqueId());
                if (settings.isAllChatVisible() && settings.isGlobalChatVisible()) {
                    player.sendMessage(message);
                    someoneHeard = true;
                }
            }
        }
        if (!someoneHeard) {
            sender.sendMessage(Component.text("Тебя никто не услышал.", NamedTextColor.RED));
        }
    }

    private void broadcastLocalChat(Player sender, Component message) {
        boolean someoneHeard = false;

        // Отправляем сообщение самому отправителю
        sender.sendMessage(message);

        // Отправляем сообщение всем пользователям с активным режимом /spy
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.equals(sender) && plugin.getSpyCommand().isSpying(player.getUniqueId())) {
                Component spyMessage = Component.text("[SPY] ", NamedTextColor.GRAY).append(message);
                player.sendMessage(spyMessage);
            }
        }

        // Проверяем и отправляем сообщение всем пользователям в радиусе локального чата
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.equals(sender)) {
                ChatSettings settings = plugin.getChatSettingsManager().getChatSettings(player.getUniqueId());
                boolean isInRange = player.getWorld().equals(sender.getWorld()) && player.getLocation().distance(sender.getLocation()) <= 100;

                if (settings.isAllChatVisible() && settings.isLocalChatVisible() && isInRange) {
                    player.sendMessage(message);
                    someoneHeard = true;
                }
            }
        }

        // Если сообщение никто не услышал, кроме отправителя
        if (!someoneHeard) {
            sender.sendMessage(Component.text("Тебя никто не услышал.", NamedTextColor.RED));
        }
    }
}
