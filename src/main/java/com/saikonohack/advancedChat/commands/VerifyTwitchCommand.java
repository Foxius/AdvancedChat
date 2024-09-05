package com.saikonohack.advancedChat.commands;

import com.saikonohack.advancedChat.AdvancedChat;
import com.saikonohack.advancedChat.twitch.TwitchVerificationData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class VerifyTwitchCommand implements CommandExecutor {

    private final AdvancedChat plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public VerifyTwitchCommand(AdvancedChat plugin) {
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

        TwitchVerificationData verificationData = plugin.getTwitchVerificationManager().getVerificationData(playerId);
        if (verificationData == null) {
            player.sendMessage("Вы не начали процесс привязки Twitch.");
            return true;
        }

        boolean isVerified = plugin.getTwitchService().verifyChannelDescription(
                verificationData.getTwitchChannelUrl(), verificationData.getVerificationPhrase());

        String successMessage = plugin.getConfig().getString("chat.twitch_account_linked");
        String failureMessage = plugin.getConfig().getString("chat.twitch_account_not_linked");

        if (isVerified) {
            plugin.getTwitchAccountManager().addAccount(playerId, verificationData.getTwitchChannelUrl());
            plugin.getTwitchVerificationManager().removeVerification(playerId);
            if (successMessage != null) {
                Component parsedMessage = miniMessage.deserialize(successMessage);
                player.sendMessage(parsedMessage);
            }
        } else {
            if (failureMessage != null) {
                Component parsedMessage = miniMessage.deserialize(failureMessage);
                player.sendMessage(parsedMessage);
            }
        }

        return true;
    }
}
