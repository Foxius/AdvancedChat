package com.saikonohack.advancedChat.head;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlayerHeadRenderer {

    private final MiniMessage miniMessage;
    private final Component headAsciiArt;

    public PlayerHeadRenderer(URL skinUrl) {
        this.miniMessage = MiniMessage.miniMessage();
        this.headAsciiArt = generateHeadAsciiArt(skinUrl);
    }

    /**
     * This method returns the formatted MiniMessage Component for the player's head ASCII art.
     */
    public Component getHeadAsciiArtComponent() {
        return headAsciiArt;
    }

    /**
     * Converts the skin's image to ASCII art using MiniMessage and hex colors, and returns it as a Component.
     */
    private Component generateHeadAsciiArt(URL skinUrl) {
        try (InputStream inputStream = skinUrl.openStream()) {
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            if (bufferedImage == null) {
                return miniMessage.deserialize("<red>Image not found</red>");
            }

            int width = 8;
            int height = 8;

            BufferedImage headImage = bufferedImage.getSubimage(8, 8, width, height);

            List<String> asciiArtLines = new ArrayList<>();

            for (int y = 0; y < height; y++) {
                StringBuilder line = new StringBuilder();
                for (int x = 0; x < width; x++) {
                    Color color = new Color(headImage.getRGB(x, y), true);
                    String hexColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());

                    line.append("<color:").append(hexColor).append(">█</color>");
                }
                asciiArtLines.add(line.toString());
            }

            String miniMessageText = String.join("\n", asciiArtLines);
            return miniMessage.deserialize(miniMessageText);
        } catch (Exception e) {
            return miniMessage.deserialize("<red>Failed to load image</red>");
        }
    }
}
