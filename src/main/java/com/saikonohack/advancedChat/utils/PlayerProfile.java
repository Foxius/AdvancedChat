package com.saikonohack.advancedChat.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class PlayerProfile {

    private final Player player;
    private final Plugin plugin;

    public PlayerProfile(Player player, Plugin plugin) {
        this.player = player;
        this.plugin = plugin;
    }

    public Component getPlayerNameComponent() {
        List<String> profileLines = plugin.getConfig().getStringList("profile");

        // Используем MiniMessage для создания текста с цветами
        MiniMessage miniMessage = MiniMessage.miniMessage();
        StringBuilder hoverText = new StringBuilder();

        for (String line : profileLines) {
            String parsedLine = PlaceholderAPI.setPlaceholders(player, line);
            hoverText.append(parsedLine).append("\n");
        }

        // Создание компонента с HoverEvent и ClickEvent
        return miniMessage.deserialize("<gold>" + player.getName())
                .hoverEvent(HoverEvent.showText(miniMessage.deserialize(hoverText.toString())))
                .clickEvent(ClickEvent.suggestCommand("/m " + player.getName()));
    }
}
