package com.saikonohack.advancedChat;

import com.saikonohack.advancedChat.chat.ChatManager;
import com.saikonohack.advancedChat.commands.*;
import com.saikonohack.advancedChat.gui.AdminGUI;
import com.saikonohack.advancedChat.head.PlayerHeadDisplayManager;
import com.saikonohack.advancedChat.listeners.ChatListener;
import com.saikonohack.advancedChat.listeners.PlayerEventListener;
import com.saikonohack.advancedChat.settings.ChatSettingsManager;
import com.saikonohack.advancedChat.tabcompleter.MentionTabCompleter;
import com.saikonohack.advancedChat.twitch.TwitchAccountManager;
import com.saikonohack.advancedChat.twitch.TwitchService;
import com.saikonohack.advancedChat.twitch.TwitchStreamChecker;
import com.saikonohack.advancedChat.twitch.TwitchVerificationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class AdvancedChat extends JavaPlugin implements Listener {

    private ChatManager chatManager;
    private ChatSettingsManager chatSettingsManager;
    private SpyCommand spyCommand;
    private AdminGUI adminGUI;  // Добавляем переменную adminGUI
    private PlayerHeadDisplayManager headDisplayManager;

    private TwitchService twitchService;
    private TwitchVerificationManager twitchVerificationManager;
    private TwitchAccountManager twitchAccountManager;

    private final MiniMessage miniMessage = MiniMessage.miniMessage();


    public ChatSettingsManager getChatSettingsManager() {
        return chatSettingsManager;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        headDisplayManager = new PlayerHeadDisplayManager(this);
        Objects.requireNonNull(getCommand("m")).setTabCompleter(new MentionTabCompleter());
        Objects.requireNonNull(getCommand("r")).setTabCompleter(new MentionTabCompleter());
        Objects.requireNonNull(getCommand("global")).setTabCompleter(new MentionTabCompleter());
        Objects.requireNonNull(getCommand("local")).setTabCompleter(new MentionTabCompleter());
        Objects.requireNonNull(getCommand("chat")).setTabCompleter(new MentionTabCompleter());
        Objects.requireNonNull(getCommand("bc")).setTabCompleter(new MentionTabCompleter());

        chatSettingsManager = new ChatSettingsManager();
        chatManager = new ChatManager(this, chatSettingsManager);
        spyCommand = new SpyCommand(this);
        adminGUI = new AdminGUI(this);
        Objects.requireNonNull(getCommand("mute")).setExecutor(new MuteCommand(chatManager));
        Objects.requireNonNull(getCommand("kick")).setExecutor(new KickCommand(this));
        Objects.requireNonNull(getCommand("ban")).setExecutor(new BanCommand(this));


        String clientId = getConfig().getString("twitch.client_id");
        String clientSecret = getConfig().getString("twitch.client_secret");

        if (clientId != null && clientSecret != null && !clientId.equals("-") && !clientSecret.equals("-")) {
            twitchService = new TwitchService(clientId, clientSecret);
            twitchVerificationManager = new TwitchVerificationManager();
            twitchAccountManager = new TwitchAccountManager(getDataFolder());

            Objects.requireNonNull(this.getCommand("linktwitch")).setExecutor(new TwitchAccountLinkCommand(this));
            Objects.requireNonNull(this.getCommand("verifytwitch")).setExecutor(new VerifyTwitchCommand(this));

            new TwitchStreamChecker(this).runTaskTimer(this, 0L, 20L * 180);

            sendConsoleMessage(getConfig().getString("chat.twitch_integration_enabled"));
        } else {
            sendConsoleMessage(getConfig().getString("chat.twitch_integration_disabled"));
        }

        getServer().getPluginManager().registerEvents(this.getAdminGUI(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(chatManager), this);
        getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);
    }

    @Override
    public void onDisable() {
        // Cleanup if needed
    }

    public TwitchService getTwitchService() {
        return twitchService;
    }

    public TwitchVerificationManager getTwitchVerificationManager() {
        return twitchVerificationManager;
    }

    public TwitchAccountManager getTwitchAccountManager() {
        return twitchAccountManager;
    }

    public SpyCommand getSpyCommand() {
        return spyCommand;
    }
    public AdminGUI getAdminGUI() {
        return adminGUI;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        headDisplayManager.handlePlayerJoin(event.getPlayer());
    }

    private void sendConsoleMessage(String message) {
        if (message != null && !message.isEmpty()) {
            Component parsedMessage = miniMessage.deserialize(message);
            getServer().getConsoleSender().sendMessage(parsedMessage);
        }
    }
}
