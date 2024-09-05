package com.saikonohack.advancedChat.chat;

import com.saikonohack.advancedChat.AdvancedChat;
import com.saikonohack.advancedChat.settings.ChatSettings;
import com.saikonohack.advancedChat.settings.ChatSettingsManager;
import com.saikonohack.advancedChat.utils.TagProcessor;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.WebhookUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatManager {

    private final AdvancedChat plugin;
    private final TagProcessor tagProcessor;
    private final Map<UUID, Long> mutedPlayers = new HashMap<>();  // Хранение заблокированных игроков с таймингом окончания мута.

    public ChatManager(AdvancedChat plugin, ChatSettingsManager chatSettingsManager) {
        this.plugin = plugin;
        this.tagProcessor = new TagProcessor();
    }

    public void handleChat(Player sender, String message) {
        // Проверка на наличие мута
        if (isPlayerMuted(sender)) {
            String muteMessage = plugin.getConfig().getString("chat.mute_message", "<red>Вы не можете отправлять сообщения, вы находитесь в муте.");
            sender.sendMessage(MiniMessage.miniMessage().deserialize(muteMessage));
            return;  // Игрок в муте, сообщение не отправляется.
        }

        boolean isGlobal = message.startsWith("!");

        if (isGlobal) {
            message = message.substring(1).trim();
        }

        Component processedMessageComponent = tagProcessor.processTags(sender, message);

        if (isGlobal) {
            Component formattedMessage = formatGlobalMessage(sender, processedMessageComponent);
            broadcastGlobalChat(sender, formattedMessage);
        } else {
            Component formattedMessage = formatLocalMessage(sender, processedMessageComponent);
            broadcastLocalChat(sender, formattedMessage);
        }
    }

    // Метод для проверки, находится ли игрок в муте
    private boolean isPlayerMuted(Player player) {
        UUID playerId = player.getUniqueId();
        if (mutedPlayers.containsKey(playerId)) {
            long unmuteTime = mutedPlayers.get(playerId);
            if (System.currentTimeMillis() > unmuteTime) {
                mutedPlayers.remove(playerId);  // Снятие мута, если время истекло
                return false;
            }
            return true;  // Игрок еще в муте
        }
        return false;
    }

    public void mutePlayer(Player player, long durationMillis) {
        mutedPlayers.put(player.getUniqueId(), System.currentTimeMillis() + durationMillis);
        String muteMessage = plugin.getConfig().getString("chat.muted_notification", "<red>Игрок <player> был замьючен на <time>.")
                .replace("<player>", player.getName())
                .replace("<time>", formatDuration(durationMillis));
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(muteMessage));
    }

    public void unmutePlayer(Player player) {
        mutedPlayers.remove(player.getUniqueId());
        String unmuteMessage = plugin.getConfig().getString("chat.unmuted_notification", "<green>Игрок <player> был размьючен.")
                .replace("<player>", player.getName());
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(unmuteMessage));
    }

    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        if (seconds < 60) {
            return seconds + " секунд";
        } else if (seconds < 3600) {
            return (seconds / 60) + " минут";
        } else if (seconds < 86400) {
            return (seconds / 3600) + " часов";
        } else {
            return (seconds / 86400) + " дней";
        }
    }

    private Component formatGlobalMessage(Player player, Component messageComponent) {
        String format = plugin.getConfig().getString("chat.global_message_format", "<gold>● <dark_purple><sender><gold> >> <white><message>");
        format = PlaceholderAPI.setPlaceholders(player, format);

        Component senderComponent = getFormattedPlayerName(player);

        return formatMessage(senderComponent, messageComponent, format);
    }

    private Component formatLocalMessage(Player player, Component messageComponent) {
        String format = plugin.getConfig().getString("chat.local_message_format", "<red>● <yellow><sender><red> >> <white><message>");
        format = PlaceholderAPI.setPlaceholders(player, format);

        Component senderComponent = getFormattedPlayerName(player);

        return formatMessage(senderComponent, messageComponent, format);
    }

    private Component getFormattedPlayerName(Player player) {
        String playerFormat = plugin.getConfig().getString("chat.player_format", "<yellow><player_name>");

        playerFormat = playerFormat.replace("<player_name>", player.getName());
        playerFormat = PlaceholderAPI.setPlaceholders(player, playerFormat);

        return MiniMessage.miniMessage().deserialize(playerFormat);
    }

    private Component formatMessage(Component senderComponent, Component messageComponent, String format) {
        String messageText = MiniMessage.miniMessage().serialize(messageComponent);

        format = format.replace("<message>", messageText);

        MiniMessage miniMessage = MiniMessage.miniMessage();
        Component baseComponent = miniMessage.deserialize(format);

        return baseComponent.replaceText(builder ->
                builder.matchLiteral("<sender>").replacement(senderComponent)
        );
    }

    private void broadcastGlobalChat(Player sender, Component message) {
        boolean someoneHeard = false;

        // Send the message to the console
        Bukkit.getConsoleSender().sendMessage(message);

        // Compatibility with DiscordSRV, sending the message via webhook
        if (Bukkit.getPluginManager().getPlugin("DiscordSRV") != null) {
            TextChannel channel = DiscordSRV.getPlugin().getMainTextChannel();
            if (channel != null) {
                // Extract plain text from the Component message
                String plainMessage = PlainTextComponentSerializer.plainText().serialize(message);

                // Remove the prefix symbols, player name, and the separator (>>)
                String playerName = sender.getName();
                String messageWithoutPrefix = plainMessage.replaceFirst("^[●\\s]*" + playerName + "\\s*>>\\s*", "");

                // Process placeholder for avatar using PlaceholderAPI
                String placeholder = PlaceholderAPI.setPlaceholders(sender, "%skinsrestorer_texture_id_or_steve%");
                String avatarUrl = "https://mc-heads.net/avatar/" + placeholder + ".png#" + sender.getName();

                // Check if AccountLinkManager is available before using it
                if (DiscordSRV.getPlugin().getAccountLinkManager() != null) {
                    // Send the message using WebhookUtil
                    WebhookUtil.deliverMessage(channel, sender, messageWithoutPrefix);
                } else {
                    sender.sendMessage(Component.text("DiscordSRV AccountLinkManager is not available.", NamedTextColor.RED));
                }
            }
        }

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
            sender.sendMessage(Component.text(plugin.getConfig().getString("chat.no_one_heard_you"), NamedTextColor.RED));
        }
    }

    private void broadcastLocalChat(Player sender, Component message) {
        boolean someoneHeard = false;

        Bukkit.getConsoleSender().sendMessage(message);
        sender.sendMessage(message);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.equals(sender) && plugin.getSpyCommand().isSpying(player.getUniqueId())) {
                Component spyMessage = Component.text("[SPY] ", NamedTextColor.GRAY).append(message);
                player.sendMessage(spyMessage);
            }
        }

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

        if (!someoneHeard) {
            sender.sendMessage(Component.text("Тебя никто не услышал.", NamedTextColor.RED));
        }
    }
}
