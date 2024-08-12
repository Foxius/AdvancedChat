package com.saikonohack.advancedChat.commands;

import com.saikonohack.advancedChat.main.AdvancedChat;
import com.saikonohack.advancedChat.utils.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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

    public MessageCommand(AdvancedChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут использовать эту команду.");
            return true;
        }

        Player playerSender = (Player) sender;

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
        PlayerProfile senderProfile = new PlayerProfile(sender, plugin);
        PlayerProfile recipientProfile = new PlayerProfile(recipient, plugin);

        String format = plugin.getConfig().getString("chat.private_message_format", "&9<sender> &8>> &a<recipient>&f: &f<message>");

        Component senderComponent = senderProfile.getPlayerNameComponent();
        Component recipientComponent = recipientProfile.getPlayerNameComponent();

        // Преобразуем строку формата в компонент с учетом цветов
        String formattedMessage = ChatColor.translateAlternateColorCodes('&', format)
                .replace("<message>", message);

        // Преобразуем строку формата в Component, затем заменяем плейсхолдеры
        return LegacyComponentSerializer.legacySection().deserialize(formattedMessage)
                .replaceText(builder -> builder.matchLiteral("<sender>").replacement(senderComponent))
                .replaceText(builder -> builder.matchLiteral("<recipient>").replacement(recipientComponent));
    }


    private Component formatMessage(Component senderComponent, Component recipientComponent, String message, String format) {
        String senderName = PlainTextComponentSerializer.plainText().serialize(senderComponent);
        String recipientName = PlainTextComponentSerializer.plainText().serialize(recipientComponent);

        String formatted = format
            .replace("<sender>", senderName)
            .replace("<recipient>", recipientName)
            .replace("<message>", message);

        return Component.text(ChatColor.translateAlternateColorCodes('&', formatted));
    }
}
