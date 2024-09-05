package com.saikonohack.advancedChat.commands;

import com.saikonohack.advancedChat.AdvancedChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpyCommand implements CommandExecutor {

    private final AdvancedChat plugin;
    private final Set<UUID> spies = new HashSet<>();

    public SpyCommand(AdvancedChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут использовать эту команду.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("advancedchat.spy")) {
            player.sendMessage(ChatColor.RED + "У вас нет прав на использование этой команды.");
            return true;
        }

        UUID playerId = player.getUniqueId();
        if (spies.contains(playerId)) {
            spies.remove(playerId);
            player.sendMessage(ChatColor.GREEN + "Режим наблюдения выключен.");
        } else {
            spies.add(playerId);
            player.sendMessage(ChatColor.GREEN + "Режим наблюдения включен.");
        }

        return true;
    }

    public boolean isSpying(UUID playerId) {
        return spies.contains(playerId);
    }
}
