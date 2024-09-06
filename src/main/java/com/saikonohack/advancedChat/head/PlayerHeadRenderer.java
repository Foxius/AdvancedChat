package com.saikonohack.advancedChat.head;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerHeadRenderer {

    private final MiniMessage miniMessage;
    private final Component headAsciiArt;
    private final String playerName;

    public PlayerHeadRenderer(URL skinUrl, String playerName) {
        this.miniMessage = MiniMessage.miniMessage();
        this.playerName = playerName;
        this.headAsciiArt = generateHeadAsciiArt(skinUrl);
    }

    /**
     * Returns the formatted MiniMessage Component for the player's head ASCII art.
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
                // On the 4th line (y == 3), add the player's nickname with special characters
                if (y == 3) {
                    line.append("     "); // Approximate 5 spaces
                    String specialName = convertToSpecialCharacters(playerName);
                    line.append(specialName);
                }
                asciiArtLines.add(line.toString());
            }

            String miniMessageText = String.join("\n", asciiArtLines);
            return miniMessage.deserialize(miniMessageText);
        } catch (Exception e) {
            return miniMessage.deserialize("<red>Failed to load image</red>");
        }
    }

    /**
     * Converts a given string to special characters.
     */
    private String convertToSpecialCharacters(String input) {
        Map<Character, String> specialCharsMap = createSpecialCharsMap();
        StringBuilder specialString = new StringBuilder();

        for (char c : input.toCharArray()) {
            char lowerC = Character.toLowerCase(c);
            if (specialCharsMap.containsKey(lowerC)) {
                specialString.append(specialCharsMap.get(lowerC));
            } else {
                specialString.append(c); // If no special character is found, use the original
            }
        }
        return specialString.toString();
    }

    /**
     * Creates a mapping from regular characters to special characters.
     */
    private Map<Character, String> createSpecialCharsMap() {
        Map<Character, String> map = new HashMap<>();
        map.put('0', "𝟎");
        map.put('1', "𝟏");
        map.put('2', "𝟐");
        map.put('3', "𝟑");
        map.put('4', "𝟒");
        map.put('5', "𝟓");
        map.put('6', "𝟔");
        map.put('7', "𝟕");
        map.put('8', "𝟖");
        map.put('9', "𝟗");
        map.put('a', "ᴀ");
        map.put('b', "ʙ");
        map.put('c', "ᴄ");
        map.put('d', "ᴅ");
        map.put('e', "ᴇ");
        map.put('f', "ꜰ");
        map.put('g', "ɢ");
        map.put('h', "ʜ");
        map.put('i', "ɪ");
        map.put('j', "ᴊ");
        map.put('k', "ᴋ");
        map.put('l', "ʟ");
        map.put('m', "ᴍ");
        map.put('n', "ɴ");
        map.put('o', "ᴏ");
        map.put('p', "ᴘ");
        map.put('q', "ǫ");
        map.put('r', "ʀ");
        map.put('s', "ꜱ");
        map.put('t', "ᴛ");
        map.put('u', "ᴜ");
        map.put('v', "ᴠ");
        map.put('w', "ᴡ");
        map.put('x', "x");
        map.put('y', "ʏ");
        map.put('z', "ᴢ");
        return map;
    }
}
