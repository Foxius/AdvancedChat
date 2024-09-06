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
    }

    public void handlePlayerJoin(Player player) {

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            URL skinUrl = getPlayerSkinUrl(player);

            if (skinUrl != null) {
                PlayerHeadRenderer headRenderer = new PlayerHeadRenderer(skinUrl);
                Component headComponent = headRenderer.getHeadAsciiArtComponent();

                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    player.sendMessage(headComponent);
                });
            } else {
                try {
                    URL defaultSkinUrl = new URL("https://mineskin.eu/download/" + player.getName());
                    PlayerHeadRenderer headRenderer = new PlayerHeadRenderer(defaultSkinUrl);
                    Component headComponent = headRenderer.getHeadAsciiArtComponent();

                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        player.sendMessage(headComponent);
                    });
                } catch (Exception e) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        player.sendMessage(Component.text("Could not retrieve your skin.").color(NamedTextColor.RED));
                    });
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

                return new URL(textureUrl);
            } else {
                return null;
            }
        } catch (Exception e) {
            LOGGER.severe("Error retrieving skin URL for player " + player.getName() + ": " + e.getMessage());
            return null;
        }
    }

}
