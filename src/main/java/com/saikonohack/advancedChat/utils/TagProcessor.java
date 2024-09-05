package com.saikonohack.advancedChat.utils;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.kyori.adventure.text.event.HoverEvent.ShowItem;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.intellij.lang.annotations.Subst;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagProcessor {

    private static final Pattern URL_PATTERN = Pattern.compile(
        "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    private static final Pattern MENTION_PATTERN = Pattern.compile("@\\w+");  // Паттерн для поиска упоминаний

    public Component processTags(Player sender, String message) {
        TextComponent.Builder messageBuilder = Component.text();

        String[] parts = message.split(" ");
        for (String part : parts) {
            if (part.equals("[pos]")) {
                // Обрабатываем тег [pos]
                Component posComponent = processPosTag(sender);
                messageBuilder.append(posComponent);
            } else if (part.equals("[item]")) {
                // Обрабатываем тег [item]
                Component itemComponent = processItemTag(sender);
                messageBuilder.append(itemComponent);
            } else if (part.startsWith("@")) {
                // Обрабатываем упоминания игроков
                handleMention(sender, part.substring(1));  // Отправляем часть без @
                messageBuilder.append(Component.text(part + " ").color(NamedTextColor.AQUA));
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

    private void handleMention(Player sender, String mentionedName) {
        Player mentionedPlayer = Bukkit.getPlayer(mentionedName);
        if (mentionedPlayer != null && mentionedPlayer.isOnline()) {
            // Проигрываем звук при упоминании
            mentionedPlayer.playSound(mentionedPlayer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        }
    }
    // Метод для обработки тега [item]
    private Component processItemTag(Player sender) {
        ItemStack item = sender.getInventory().getItemInMainHand(); // Получаем предмет в руке игрока

        if (item == null || item.getType() == Material.AIR) {
            // Если в руке ничего нет, заменяем тег на "Пусто"
            return Component.text("Пусто").color(NamedTextColor.GRAY);
        }

        // Получаем название предмета и количество
        ItemMeta itemMeta = item.getItemMeta();
        String itemName = itemMeta != null && itemMeta.hasDisplayName()
                ? itemMeta.getDisplayName()
                : item.getType().toString();

        int itemAmount = item.getAmount();

        // Создаем HoverEvent для отображения предмета при наведении
        HoverEvent<ShowItem> hoverEvent = HoverEvent.showItem(ShowItem.showItem(Key.key(item.getType().getKey().toString()), itemAmount));

        // Создаем компонент для отображения предмета с полными данными при наведении
        return Component.text("[" + itemName + " x" + itemAmount + "]")
                .hoverEvent(hoverEvent)
                .color(NamedTextColor.GOLD);
    }
}
