package com.saikonohack.advancedChat.twitch;

import com.saikonohack.advancedChat.AdvancedChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class TwitchStreamChecker extends BukkitRunnable {

    private final AdvancedChat plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public TwitchStreamChecker(AdvancedChat plugin) {
        this.plugin = plugin;
    }


    @Override
    public void run() {
        for (Map.Entry<UUID, String> entry : plugin.getTwitchAccountManager().getAllAccounts().entrySet()) {
            String twitchChannelUrl = entry.getValue();
            String username = plugin.getTwitchService().extractUsernameFromUrl(twitchChannelUrl);

            if (plugin.getTwitchService().isStreamLive(username)) {
                String messageTemplate = plugin.getConfig().getString("chat.twitch_stream_live");

                if (messageTemplate != null) {
                    String streamTitle = plugin.getTwitchService().getStreamTitle(username);

                    String message = messageTemplate
                            .replace("<username>", username)
                            .replace("<title>", streamTitle)
                            .replace("<url>", twitchChannelUrl);

                    Component parsedMessage = miniMessage.deserialize(message);

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(parsedMessage);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    }
                }
            }
        }
    }

}
