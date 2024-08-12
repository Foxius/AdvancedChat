package com.saikonohack.advancedChat.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.saikonohack.advancedChat.main.AdvancedChat;

public class ClearChatCommand implements CommandExecutor {

    private final AdvancedChat plugin;

    public ClearChatCommand(AdvancedChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("advancedchat.clearchat")) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав на использование этой команды.");
            return true;
        }

        int lines = 100; // Количество строк для очистки чата

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < lines; i++) {
                player.sendMessage("");
            }
        }

        Bukkit.broadcastMessage(ChatColor.GREEN + "Чат был очищен администратором.");

        return true;
    }
}
