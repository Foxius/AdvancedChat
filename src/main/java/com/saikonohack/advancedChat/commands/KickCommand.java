package com.saikonohack.advancedChat.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class KickCommand implements CommandExecutor {

    private final Plugin plugin;

    public KickCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender.hasPermission("advancedchat.admin"))) {
            sender.sendMessage("You don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("Usage: /kick <player> [reason]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        String reason = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "Кикнут администратором.";
        String kickMessage = plugin.getConfig().getString("messages.kick_message", "<red>Игрок <player> был кикнут. Причина: <reason>")
                .replace("<player>", target.getName())
                .replace("<reason>", reason);

        Bukkit.broadcastMessage(kickMessage);
        target.kickPlayer(reason);
        return true;
    }
}
