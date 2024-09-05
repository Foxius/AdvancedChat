package com.saikonohack.advancedChat.commands;

import com.saikonohack.advancedChat.AdvancedChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GeolocateCommand implements CommandExecutor {

    private final AdvancedChat plugin;
    private final Map<UUID, BukkitTask> trackingTasks = new HashMap<>();

    public GeolocateCommand(AdvancedChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут использовать эту команду.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("advancedchat.geolocate")) {
            player.sendMessage(ChatColor.RED + "У вас нет прав на использование этой команды.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Использование: /geolocate <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Игрок не найден.");
            return true;
        }

        UUID playerId = player.getUniqueId();
        if (trackingTasks.containsKey(playerId)) {
            // Останавливаем текущую слежку
            trackingTasks.get(playerId).cancel();
            trackingTasks.remove(playerId);
            player.sendMessage(ChatColor.GREEN + "Слежка за игроком " + target.getName() + " прекращена.");
        } else {
            // Получаем интервал из конфига (по умолчанию 5 секунд)
            long intervalTicks = plugin.getConfig().getLong("geolocate.interval", 5) * 20L;

            // Запускаем новую слежку
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (target.isOnline()) {
                    Location loc = target.getLocation();
                    World world = loc.getWorld();
                    player.sendMessage(ChatColor.YELLOW + "Координаты " + target.getName() + " [Мир: " + world.getName() + "]: X=" + loc.getBlockX() + " Y=" + loc.getBlockY() + " Z=" + loc.getBlockZ());
                } else {
                    player.sendMessage(ChatColor.RED + "Игрок " + target.getName() + " вышел из игры. Слежка прекращена.");
                    trackingTasks.get(playerId).cancel();
                    trackingTasks.remove(playerId);
                }
            }, 0L, intervalTicks); // Интервал указан в конфиге

            trackingTasks.put(playerId, task);
            player.sendMessage(ChatColor.GREEN + "Начата слежка за игроком " + target.getName() + ".");
        }

        return true;
    }
}
