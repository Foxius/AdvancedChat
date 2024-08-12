package com.saikonohack.advancedChat.commands;

import com.saikonohack.advancedChat.main.AdvancedChat;
import com.saikonohack.advancedChat.settings.ChatSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatSettingsCommand implements CommandExecutor {

    private final AdvancedChat plugin;

    public ChatSettingsCommand(AdvancedChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("advancedchat.chatsettings")) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав на использование этой команды.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Использование: /chatsettings <global|local> <on|off>");
            return true;
        }

        String settingType = args[0];
        String action = args[1];
        boolean toggle;

        if (action.equalsIgnoreCase("on")) {
            toggle = true;
        } else if (action.equalsIgnoreCase("off")) {
            toggle = false;
        } else {
            sender.sendMessage(ChatColor.RED + "Неверный аргумент: используйте 'on' или 'off'.");
            return true;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            ChatSettings settings = plugin.getChatSettingsManager().getChatSettings(player.getUniqueId());
            if (settingType.equalsIgnoreCase("global")) {
                settings.setGlobalChatVisible(toggle);
            } else if (settingType.equalsIgnoreCase("local")) {
                settings.setLocalChatVisible(toggle);
            } else {
                sender.sendMessage(ChatColor.RED + "Неверный тип настройки: используйте 'global' или 'local'.");
                return true;
            }
        }

        sender.sendMessage(ChatColor.GREEN + "Настройки чата обновлены для всех игроков: " + settingType + " чат " + (toggle ? "включен" : "выключен"));
        return true;
    }
}
