package com.saikonohack.advancedChat.commands;

import com.saikonohack.advancedChat.main.AdvancedChat;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BroadcastCommand implements CommandExecutor {

    private final AdvancedChat plugin;  // Убираем повторную инициализацию и используем ссылку на плагин
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public BroadcastCommand(AdvancedChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только игроки могут использовать эту команду.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long cooldownTime = plugin.getConfig().getLong("broadcast.cooldown", 3600) * 1000;

        if (cooldowns.containsKey(playerId)) {
            long lastUsed = cooldowns.get(playerId);
            long timeLeft = (lastUsed + cooldownTime) - currentTime;

            if (timeLeft > 0) {
                String cooldownMessage = plugin.getConfig().getString("broadcast.cooldown_message", "<red>Команду можно использовать раз в час. Осталось: <time> минут.")
                        .replace("<time>", String.valueOf(timeLeft / 60000));
                player.sendMessage(MiniMessage.miniMessage().deserialize(cooldownMessage));
                return true;
            }
        }

        if (args.length == 0) {
            player.sendMessage("Использование: /" + label + " <сообщение>");
            return true;
        }

        String message = String.join(" ", args);
        String broadcastMessage = plugin.getConfig().getString("broadcast.format", "<gold>[Объявление] <reset><message>")
                .replace("<player>", player.getName())
                .replace("<message>", message);

        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(broadcastMessage));

        cooldowns.put(playerId, currentTime);
        return true;
    }
}
