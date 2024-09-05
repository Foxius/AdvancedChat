package com.saikonohack.advancedChat.twitch;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TwitchAccountManager {

    private final File dataFile;
    private final Map<UUID, String> linkedAccounts = new HashMap<>();
    private final Gson gson = new Gson();

    public TwitchAccountManager(File dataFolder) {
        this.dataFile = new File(dataFolder, "twitch_accounts.json");
        loadAccounts();
    }

    // Добавление привязанного аккаунта
    public void addAccount(UUID playerId, String twitchChannelUrl) {
        linkedAccounts.put(playerId, twitchChannelUrl);
        saveAccounts();
    }

    // Получение привязанного аккаунта
    public String getAccount(UUID playerId) {
        return linkedAccounts.get(playerId);
    }

    // Получение всех привязанных аккаунтов
    public Map<UUID, String> getAllAccounts() {
        return linkedAccounts;
    }

    // Загрузка данных из JSON файла
    private void loadAccounts() {
        if (!dataFile.exists()) {
            return;
        }

        try (Reader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<Map<UUID, String>>() {}.getType();
            Map<UUID, String> loadedAccounts = gson.fromJson(reader, type);
            if (loadedAccounts != null) {
                linkedAccounts.putAll(loadedAccounts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Сохранение данных в JSON файл
    public void saveAccounts() {
        try (Writer writer = new FileWriter(dataFile)) {
            gson.toJson(linkedAccounts, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
