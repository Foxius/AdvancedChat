package com.saikonohack.advancedChat.listeners;

import com.saikonohack.advancedChat.main.AdvancedChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventListener implements Listener {

    private final AdvancedChat plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public PlayerEventListener(AdvancedChat plugin) {
        this.plugin = plugin;
        // Отключаем стандартное сообщение о достижении
        Bukkit.getWorlds().forEach(world -> world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String joinMessage = plugin.getConfig().getString("chat.join_message", "<gold>Добро пожаловать, <player> на сервер!")
            .replace("<player>", player.getName());
        event.joinMessage(miniMessage.deserialize(joinMessage));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String quitMessage = plugin.getConfig().getString("chat.quit_message", "<gold><player> покинул сервер. Мы будем скучать!")
            .replace("<player>", player.getName());
        event.quitMessage(miniMessage.deserialize(quitMessage));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String format = plugin.getConfig().getString("chat.death_message_format", "<red><death_message> [Press F]");
        TranslatableComponent originalDeathMessage = (TranslatableComponent) event.deathMessage();

        if (originalDeathMessage != null) {
            // Создаем кликабельное сообщение "Press F"

            Component customDeathMessage = miniMessage.deserialize(format)
                .replaceText(builder -> builder.matchLiteral("<death_message>").replacement(originalDeathMessage))
                .append(Component.space());

            event.deathMessage(customDeathMessage);
        }
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        String advancementKey = event.getAdvancement().getKey().toString();

        // Пропускаем все достижения, связанные с рецептами
        if (advancementKey.contains("recipes/")) {
            return;
        }

        String format = plugin.getConfig().getString("chat.advancement_message_format", "<gold><player> достиг достижения [<advancement>]");
        String titleTranslationKey = "advancements." + advancementKey.replace("minecraft:", "").replace("/", ".") + ".title";
        String descriptionTranslationKey = "advancements." + advancementKey.replace("minecraft:", "").replace("/", ".") + ".description";

        TranslatableComponent translatedTitle = Component.translatable(titleTranslationKey)
            .color(NamedTextColor.DARK_PURPLE);

        TranslatableComponent translatedDescription = Component.translatable(descriptionTranslationKey)
            .color(NamedTextColor.GRAY);

        // Объединяем заголовок и описание в один hover event
        HoverEvent<Component> hoverEvent = HoverEvent.showText(Component.text()
            .append(translatedTitle)
            .append(Component.text("\n"))
            .append(translatedDescription)
            .build());

        Component customAdvancementMessage = miniMessage.deserialize(format)
            .replaceText(builder -> builder.matchLiteral("<player>").replacement(event.getPlayer().getName()))
            .replaceText(builder -> builder.matchLiteral("<advancement>").replacement(translatedTitle.hoverEvent(hoverEvent)));

        Bukkit.broadcast(customAdvancementMessage);
    }

}
