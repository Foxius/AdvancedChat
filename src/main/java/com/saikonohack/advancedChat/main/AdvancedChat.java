package com.saikonohack.advancedChat.main;

import com.saikonohack.advancedChat.chat.ChatManager;
import com.saikonohack.advancedChat.commands.*;
import com.saikonohack.advancedChat.gui.AdminGUI;
import com.saikonohack.advancedChat.listeners.ChatListener;
import com.saikonohack.advancedChat.listeners.PlayerEventListener;
import com.saikonohack.advancedChat.settings.ChatSettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class AdvancedChat extends JavaPlugin {

    private ChatManager chatManager;
    private ChatSettingsManager chatSettingsManager;
    private SpyCommand spyCommand;
    private AdminGUI adminGUI;  // Добавляем переменную adminGUI

    public ChatSettingsManager getChatSettingsManager() {
        return chatSettingsManager;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        chatSettingsManager = new ChatSettingsManager();
        chatManager = new ChatManager(this, chatSettingsManager);
        spyCommand = new SpyCommand(this);
        adminGUI = new AdminGUI(this);  // Инициализация adminGUI

        getServer().getPluginManager().registerEvents(new ChatListener(chatManager), this);
        getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);

        Objects.requireNonNull(this.getCommand("bc")).setExecutor(new BroadcastCommand(this));
        Objects.requireNonNull(this.getCommand("spy")).setExecutor(spyCommand);

        Objects.requireNonNull(this.getCommand("geolocate")).setExecutor(new GeolocateCommand(this));
        Objects.requireNonNull(this.getCommand("playerinfo")).setExecutor(new PlayerInfoCommand(this));
        Objects.requireNonNull(this.getCommand("chatsettings")).setExecutor(new ChatSettingsCommand(this));
        Objects.requireNonNull(this.getCommand("clearchat")).setExecutor(new ClearChatCommand(this));
        Objects.requireNonNull(this.getCommand("adminmenu")).setExecutor(new AdminMenuCommand(this));

        // Регистрация GUI
        Bukkit.getPluginManager().registerEvents(adminGUI, this);

        Objects.requireNonNull(getCommand("global")).setExecutor(new ChatToggleCommand(chatSettingsManager));
        Objects.requireNonNull(getCommand("local")).setExecutor(new ChatToggleCommand(chatSettingsManager));
        Objects.requireNonNull(getCommand("chat")).setExecutor(new ChatToggleCommand(chatSettingsManager));

        MessageCommand messageCommand = new MessageCommand(this);
        Objects.requireNonNull(getCommand("m")).setExecutor(messageCommand);
        Objects.requireNonNull(getCommand("r")).setExecutor(messageCommand);
    }

    @Override
    public void onDisable() {
        // Cleanup if needed
    }

    public SpyCommand getSpyCommand() {
        return spyCommand;
    }
    public AdminGUI getAdminGUI() {
        return adminGUI;
    }
}
