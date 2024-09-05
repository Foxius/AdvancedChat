package com.saikonohack.advancedChat.commands;

import com.saikonohack.advancedChat.AdvancedChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerInfoCommand implements CommandExecutor {

    private final AdvancedChat plugin;

    public PlayerInfoCommand(AdvancedChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут использовать эту команду.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("advancedchat.playerinfo")) {
            player.sendMessage(ChatColor.RED + "У вас нет прав на использование этой команды.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Использование: /playerinfo <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Игрок не найден.");
            return true;
        }

        // Информация о игроке
        String playerName = target.getName();
        String uuid = target.getUniqueId().toString();
        String worldName = target.getWorld().getName();
        String coordinates = "X=" + target.getLocation().getBlockX() + " Y=" + target.getLocation().getBlockY() + " Z=" + target.getLocation().getBlockZ();
        long playTime = target.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20; // Время в секундах
        int deaths = target.getStatistic(Statistic.DEATHS);
        int kills = target.getStatistic(Statistic.PLAYER_KILLS);

        // Форматируем и отправляем информацию игроку
        player.sendMessage(ChatColor.GOLD + "Информация об игроке " + ChatColor.YELLOW + playerName + ChatColor.GOLD + ":");
        player.sendMessage(ChatColor.AQUA + "UUID: " + ChatColor.WHITE + uuid);
        player.sendMessage(ChatColor.AQUA + "Мир: " + ChatColor.WHITE + worldName);
        player.sendMessage(ChatColor.AQUA + "Координаты: " + ChatColor.WHITE + coordinates);
        player.sendMessage(ChatColor.AQUA + "Время игры: " + ChatColor.WHITE + playTime + " секунд");
        player.sendMessage(ChatColor.AQUA + "Количество смертей: " + ChatColor.WHITE + deaths);
        player.sendMessage(ChatColor.AQUA + "Количество убийств: " + ChatColor.WHITE + kills);

        return true;
    }
}
