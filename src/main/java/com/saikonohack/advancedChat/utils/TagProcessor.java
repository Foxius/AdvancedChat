package com.saikonohack.advancedChat.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagProcessor {

    private static final Pattern URL_PATTERN = Pattern.compile(
        "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    public Component processTags(Player player, String message) {
        TextComponent.Builder messageBuilder = Component.text();

        String[] parts = message.split(" ");
        for (String part : parts) {
            if (part.equals("[pos]")) {
                // Обрабатываем тег [pos]
                Component posComponent = processPosTag(player);
                messageBuilder.append(posComponent);
            } else {
                Matcher matcher = URL_PATTERN.matcher(part);
                if (matcher.matches()) {
                    // Создаем кликабельную ссылку
                    Component urlComponent = Component.text(part)
                        .color(NamedTextColor.BLUE)
                        .clickEvent(ClickEvent.openUrl(part))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to open link", NamedTextColor.GRAY)));
                    messageBuilder.append(urlComponent).append(Component.text(" "));
                } else {
                    // Добавляем обычный текст
                    messageBuilder.append(Component.text(part + " "));
                }
            }
        }

        return messageBuilder.build();
    }

    private Component processPosTag(Player player) {
        return Component.text("XYZ: " + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ()
                + " (" + player.getWorld().getName() + ")", NamedTextColor.YELLOW);
    }
}
