package com.saikonohack.advancedChat.commands;

import com.saikonohack.advancedChat.AdvancedChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminMenuCommand implements CommandExecutor {

    private final AdvancedChat plugin;

    public AdminMenuCommand(AdvancedChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут использовать эту команду.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("advancedchat.adminmenu")) {
            player.sendMessage(ChatColor.RED + "У вас нет прав на использование этой команды.");
            return true;
        }

        plugin.getAdminGUI().openAdminMenu(player);
        return true;
    }
}
