package com.saikonohack.advancedChat.head;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.storage.PlayerStorage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URL;
import java.util.Base64;
import java.util.Optional;
import java.util.logging.Logger;

public class PlayerHeadDisplayManager {

    private final SkinsRestorer skinsRestorer;
    private static final Logger LOGGER = Logger.getLogger(PlayerHeadDisplayManager.class.getName());
    private final Plugin plugin;

    public PlayerHeadDisplayManager(Plugin plugin) {
        this.plugin = plugin;
        this.skinsRestorer = SkinsRestorerProvider.get();
        if (skinsRestorer != null) {
            LOGGER.info("SkinsRestorer API successfully loaded.");
        } else {
            LOGGER.severe("SkinsRestorer API not found.");
        }
    }

    public void handlePlayerJoin(Player player) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            URL skinUrl = getPlayerSkinUrl(player);

            if (skinUrl != null) {
                com.saikonohack.test.PlayerHeadRenderer headRenderer = new com.saikonohack.test.PlayerHeadRenderer(skinUrl);
                player.sendMessage(headRenderer.getHeadAsciiArtComponent());
            } else {
                // Если скин не найден, используем URL для базового скина
                try {
                    URL defaultSkinUrl = new URL("https://textures.minecraft.net/texture/8f8cd7b1b2b1d8fe435781d768113f531d50b3135cc9b7d0d2d19d14dd4e1e78"); // Пример URL базового скина
                    com.saikonohack.test.PlayerHeadRenderer headRenderer = new com.saikonohack.test.PlayerHeadRenderer(defaultSkinUrl);
                    player.sendMessage(headRenderer.getHeadAsciiArtComponent());
                } catch (Exception e) {
                    player.sendMessage(Component.text("Could not retrieve your skin.").color(NamedTextColor.RED));
                }
            }
        });
    }

    private URL getPlayerSkinUrl(@NotNull Player player) {
        try {
            PlayerStorage playerStorage = skinsRestorer.getPlayerStorage();
            Optional<SkinProperty> skinPropertyOptional = playerStorage.getSkinOfPlayer(player.getUniqueId());

            if (skinPropertyOptional.isPresent()) {
                SkinProperty skinProperty = skinPropertyOptional.get();
                String decodedValue = new String(Base64.getDecoder().decode(skinProperty.getValue()));

                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(decodedValue);

                JSONObject texturesObject = (JSONObject) jsonObject.get("textures");
                JSONObject skinObject = (JSONObject) texturesObject.get("SKIN");
                String textureUrl = (String) skinObject.get("url");

                LOGGER.info("Successfully retrieved skin URL for player: " + player.getName());
                return new URL(textureUrl);
            } else {
                LOGGER.warning("No skin found for player: " + player.getName());
                return null;
            }
        } catch (Exception e) {
            LOGGER.severe("Error retrieving skin URL for player " + player.getName() + ": " + e.getMessage());
            return null;
        }
    }
}
