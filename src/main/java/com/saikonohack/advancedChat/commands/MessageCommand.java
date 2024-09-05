package com.saikonohack.advancedChat.commands;

import com.saikonohack.advancedChat.AdvancedChat;
import com.saikonohack.advancedChat.utils.TagProcessor;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MessageCommand implements CommandExecutor {

    private final Map<Player, Player> lastMessaged = new HashMap<>();
    private final AdvancedChat plugin;
    private final TagProcessor tagProcessor;

    public MessageCommand(AdvancedChat plugin) {
        this.plugin = plugin;
        this.tagProcessor = new TagProcessor();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player playerSender)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут использовать эту команду.");
            return true;
        }

        if (label.equalsIgnoreCase("m") || label.equalsIgnoreCase("w") || label.equalsIgnoreCase("tell") || label.equalsIgnoreCase("msg")) {
            if (args.length < 2) {
                playerSender.sendMessage(ChatColor.RED + "Использование: /m <ник> <сообщение>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                playerSender.sendMessage(ChatColor.RED + "Игрок с ником " + args[0] + " не найден.");
                return true;
            }

            String[] messageArray = Arrays.copyOfRange(args, 1, args.length);
            String message = String.join(" ", messageArray);

            Component formattedMessage = formatPrivateMessage(playerSender, target, message);

            target.sendMessage(formattedMessage);
            playerSender.sendMessage(formattedMessage);
            for (Player spy : Bukkit.getOnlinePlayers()) {
                if (plugin.getSpyCommand().isSpying(spy.getUniqueId()) && !spy.equals(sender) && !spy.equals(target)) {
                    spy.sendMessage(Component.text("[SPY] ", NamedTextColor.GRAY).append(formattedMessage));
                }
            }

            lastMessaged.put(target, playerSender);
            lastMessaged.put(playerSender, target);

        } else if (label.equalsIgnoreCase("r")) {
            if (args.length < 1) {
                playerSender.sendMessage(ChatColor.RED + "Использование: /r <сообщение>");
                return true;
            }

            Player target = lastMessaged.get(playerSender);
            if (target == null || !target.isOnline()) {
                playerSender.sendMessage(ChatColor.RED + "Нет игрока, которому можно ответить.");
                return true;
            }

            String message = String.join(" ", args);

            Component formattedMessage = formatPrivateMessage(playerSender, target, message);

            target.sendMessage(formattedMessage);
            playerSender.sendMessage(formattedMessage);

            lastMessaged.put(target, playerSender);
        }

        return true;
    }

    private Component formatPrivateMessage(Player sender, Player recipient, String message) {
        String format = plugin.getConfig().getString("chat.private_message_format", "<dark_aqua><sender> <gray>> <green><recipient><white>: <message>");
        format = PlaceholderAPI.setPlaceholders(sender, format);

        Component senderComponent = getFormattedPlayerName(sender);
        Component recipientComponent = getFormattedPlayerName(recipient);

        Component processedMessageComponent = tagProcessor.processTags(sender, message);

        return formatMessage(senderComponent, recipientComponent, format, processedMessageComponent);
    }

    private Component formatMessage(Component senderComponent, Component recipientComponent, String format, Component messageComponent) {
        String messageText = MiniMessage.miniMessage().serialize(messageComponent);

        format = format.replace("<sender>", MiniMessage.miniMessage().serialize(senderComponent))
                       .replace("<recipient>", MiniMessage.miniMessage().serialize(recipientComponent))
                       .replace("<message>", messageText);

        return MiniMessage.miniMessage().deserialize(format);
    }


    private Component getFormattedPlayerName(Player player) {
        String playerFormat = plugin.getConfig().getString("chat.player_format", "<yellow><player_name>");

        playerFormat = playerFormat.replace("<player_name>", player.getName());
        playerFormat = PlaceholderAPI.setPlaceholders(player, playerFormat);

        return MiniMessage.miniMessage().deserialize(playerFormat);
    }

}