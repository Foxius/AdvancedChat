package com.saikonohack.advancedChat.listeners;

import com.saikonohack.advancedChat.AdvancedChat;
import io.papermc.paper.advancement.AdvancementDisplay;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

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
        String joinMessageFormat = plugin.getConfig().getString("chat.join_message", "<gold>Добро пожаловать, <player> на сервер!");

        Component joinMessage = miniMessage.deserialize(joinMessageFormat)
            .replaceText(builder -> builder.matchLiteral("<player>").replacement(getFormattedPlayerName(player)));

        event.joinMessage(joinMessage);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String quitMessageFormat = plugin.getConfig().getString("chat.quit_message", "<gold><player> покинул сервер. Мы будем скучать!");

        Component quitMessage = miniMessage.deserialize(quitMessageFormat)
            .replaceText(builder -> builder.matchLiteral("<player>").replacement(getFormattedPlayerName(player)));

        event.quitMessage(quitMessage);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String format = plugin.getConfig().getString("chat.death_message_format", "<red><death_message> [Press F]");
        TranslatableComponent originalDeathMessage = (TranslatableComponent) event.deathMessage();

        if (originalDeathMessage != null) {
            Component customDeathMessage = miniMessage.deserialize(format)
                .replaceText(builder -> builder.matchLiteral("<death_message>").replacement(originalDeathMessage))
                .append(Component.space());

            event.deathMessage(customDeathMessage);
        }
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        String advancementKey = event.getAdvancement().getKey().toString();

        // Получаем данные отображения достижения
        AdvancementDisplay display = event.getAdvancement().getDisplay();

        // Пропускаем достижение, если у него нет данных отображения или если оно скрыто
        if (display == null || display.isHidden() || !display.doesAnnounceToChat()) {
            return;
        }

        // Пропускаем достижение, если его ключ находится в списке игнорируемых
        List<String> ignoredAdvancements = plugin.getConfig().getStringList("ignored_advancement");
        if (ignoredAdvancements.contains(advancementKey)) {
            return;
        }

        // Пропускаем рецепты
        if (advancementKey.contains("recipes/")) {
            return;
        }

        // Форматирование и отправка сообщения о достижении
        String format = plugin.getConfig().getString("chat.advancement_message_format", "<gold><player> достиг достижения [<advancement>]");
        String titleTranslationKey = "advancements." + advancementKey.replace("minecraft:", "").replace("/", ".") + ".title";
        String descriptionTranslationKey = "advancements." + advancementKey.replace("minecraft:", "").replace("/", ".") + ".description";

        TranslatableComponent translatedTitle = Component.translatable(titleTranslationKey)
            .color(NamedTextColor.DARK_PURPLE);

        TranslatableComponent translatedDescription = Component.translatable(descriptionTranslationKey)
            .color(NamedTextColor.GRAY);

        HoverEvent<Component> hoverEvent = HoverEvent.showText(Component.text()
            .append(translatedTitle)
            .append(Component.text("\n"))
            .append(translatedDescription)
            .build());

        Component customAdvancementMessage = miniMessage.deserialize(format)
            .replaceText(builder -> builder.matchLiteral("<player>").replacement(getFormattedPlayerName(event.getPlayer())))
            .replaceText(builder -> builder.matchLiteral("<advancement>").replacement(translatedTitle.hoverEvent(hoverEvent)));

        Bukkit.broadcast(customAdvancementMessage);
    }

    private Component getFormattedPlayerName(Player player) {
        // Получаем формат ника из конфига
        String playerFormat = plugin.getConfig().getString("chat.player_format", "<yellow><player_name>");

        // Заменяем <player_name> на фактическое имя игрока
        playerFormat = playerFormat.replace("<player_name>", player.getName());

        // Обрабатываем плейсхолдеры через PlaceholderAPI
        playerFormat = PlaceholderAPI.setPlaceholders(player, playerFormat);

        // Возвращаем компонент, сформированный на основе строки формата
        return miniMessage.deserialize(playerFormat);
    }

}
