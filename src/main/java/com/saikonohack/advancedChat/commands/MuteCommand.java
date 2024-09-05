package com.saikonohack.advancedChat.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MuteCommand implements CommandExecutor {

    private final Plugin plugin;

    public MuteCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("advancedchat.admin")) {
            sender.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("messages.no_permission")));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("Использование: /mute <player> <time><s/m/h/d>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Игрок не найден.");
            return true;
        }

        long muteTime = parseTime(args[1]);
        if (muteTime <= 0) {
            sender.sendMessage("Ошибка времени.");
            return true;
        }

        target.addAttachment(plugin, "advancedchat.muted", true, (int) muteTime);
        sender.sendMessage("Player " + target.getName() + " has been muted for " + args[1] + ".");
        String muteMessage = plugin.getConfig().getString("messages.mute_message", "<red>Вы не можете писать сообщения, так как находитесь в муте.");
        target.sendMessage(muteMessage);

        // Убираем мут через заданное время
        new BukkitRunnable() {
            @Override
            public void run() {
                target.removeAttachment(target.addAttachment(plugin, "advancedchat.muted", true));
            }
        }.runTaskLater(plugin, muteTime);

        return true;
    }

    private long parseTime(String time) {
        try {
            char timeUnit = time.charAt(time.length() - 1);
            long duration = Long.parseLong(time.substring(0, time.length() - 1));
            switch (timeUnit) {
                case 's':
                    return TimeUnit.SECONDS.toSeconds(duration) * 20;
                case 'm':
                    return TimeUnit.MINUTES.toSeconds(duration) * 20;
                case 'h':
                    return TimeUnit.HOURS.toSeconds(duration) * 20;
                case 'd':
                    return TimeUnit.DAYS.toSeconds(duration) * 20;
                default:
                    return -1;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
