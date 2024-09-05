package com.saikonohack.advancedChat.commands;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BanCommand implements CommandExecutor {

    private final Plugin plugin;

    public BanCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("advancedchat.admin")) {
            sender.sendMessage(plugin.getConfig().getString("messages.no_permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: /ban <player> <time><s/m/h/d> [reason]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        long banTime = parseTime(args[1]);
        if (banTime <= 0) {
            sender.sendMessage("Invalid time format.");
            return true;
        }

        Date unbanDate = new Date(System.currentTimeMillis() + banTime * 50);
        String reason = args.length > 2 ? String.join(" ", args, 2, args.length) : "Забанен администратором.";
        String banMessage = plugin.getConfig().getString("messages.ban_message", "<red>Игрок <player> был забанен на <time>. Причина: <reason>")
                .replace("<player>", target.getName())
                .replace("<time>", args[1])
                .replace("<reason>", reason);

        Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), reason, unbanDate, sender.getName());
        Bukkit.broadcastMessage(banMessage);
        target.kickPlayer("Вы были забанены на " + args[1] + " по причине: " + reason);
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
