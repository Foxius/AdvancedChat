package com.saikonohack.advancedChat.commands;

import com.saikonohack.advancedChat.AdvancedChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.UUID;

public class TwitchAccountLinkCommand implements CommandExecutor {

    private final AdvancedChat plugin;
    private final Random random = new Random();
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public TwitchAccountLinkCommand(AdvancedChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только игроки могут использовать эту команду.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        if (args.length != 1) {
            player.sendMessage("Использование: /linktwitch <ссылка_на_канал>");
            return true;
        }

        String twitchChannelUrl = args[0];
        String verificationPhrase = generateRandomPhrase();

        String messageTemplate = plugin.getConfig().getString("chat.twitch_account_link_instructions");
        if (messageTemplate != null) {
            String message = messageTemplate.replace("<phrase>", verificationPhrase);
            Component parsedMessage = miniMessage.deserialize(message);
            player.sendMessage(parsedMessage);
        }

        plugin.getTwitchVerificationManager().addVerification(playerId, twitchChannelUrl, verificationPhrase);
        return true;
    }


    private String generateRandomPhrase() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return sb.toString();
    }
}
