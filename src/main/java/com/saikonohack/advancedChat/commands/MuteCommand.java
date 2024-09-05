package com.saikonohack.advancedChat.commands;

import com.saikonohack.advancedChat.chat.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class MuteCommand implements CommandExecutor {

    private final ChatManager chatManager;

    public MuteCommand(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("advancedchat.admin")) {
            sender.sendMessage("You don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: /mute <player> <time><s/m/h/d>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        // Проверяем, пытаемся ли снять мут
        if (args[1].equalsIgnoreCase("unmute")) {
            chatManager.unmutePlayer(target);
            sender.sendMessage("Player " + target.getName() + " has been unmuted.");
            return true;
        }

        // Парсинг времени
        long muteDuration = parseTime(args[1]);
        if (muteDuration <= 0) {
            sender.sendMessage("Invalid time format.");
            return true;
        }

        chatManager.mutePlayer(target, muteDuration);
        sender.sendMessage("Player " + target.getName() + " has been muted for " + args[1] + ".");
        return true;
    }

    private long parseTime(String time) {
        try {
            char timeUnit = time.charAt(time.length() - 1);
            long duration = Long.parseLong(time.substring(0, time.length() - 1));
            switch (timeUnit) {
                case 's':
                    return TimeUnit.SECONDS.toMillis(duration);
                case 'm':
                    return TimeUnit.MINUTES.toMillis(duration);
                case 'h':
                    return TimeUnit.HOURS.toMillis(duration);
                case 'd':
                    return TimeUnit.DAYS.toMillis(duration);
                default:
                    return -1;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
