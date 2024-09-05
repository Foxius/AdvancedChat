package com.saikonohack.advancedChat.twitch;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TwitchService {

    private final String clientId;
    private final String clientSecret;
    private String accessToken;
    private final OkHttpClient httpClient;
    private final Gson gson = new Gson();

    public TwitchService(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.httpClient = new OkHttpClient();
        refreshAccessToken();
    }

    private void refreshAccessToken() {
        RequestBody emptyBody = RequestBody.create("", null); // Создаем пустое тело запроса

        Request request = new Request.Builder()
                .url("https://id.twitch.tv/oauth2/token?client_id=" + clientId + "&client_secret=" + clientSecret + "&grant_type=client_credentials")
                .post(emptyBody) // Указываем пустое тело запроса
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            Map<String, Object> json = gson.fromJson(response.body().string(), Map.class);
            this.accessToken = (String) json.get("access_token");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyChannelDescription(String channelUrl, String verificationPhrase) {
        try {
            String username = extractUsernameFromUrl(channelUrl);
            String url = "https://api.twitch.tv/helix/users?login=" + username;

            Request request = new Request.Builder()
                .url(url)
                .addHeader("Client-ID", clientId)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Map<String, Object> json = gson.fromJson(response.body().string(), Map.class);
                Map<String, Object> userData = ((List<Map<String, Object>>) json.get("data")).get(0);
                String description = (String) userData.get("description");
                return description.contains(verificationPhrase);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getStreamTitle(String username) {
        try {
            String url = "https://api.twitch.tv/helix/streams?user_login=" + username;

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Client-ID", clientId)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Map<String, Object> json = gson.fromJson(response.body().string(), Map.class);
                List<Map<String, Object>> streams = (List<Map<String, Object>>) json.get("data");

                if (!streams.isEmpty()) {
                    return (String) streams.get(0).get("title");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "No Title";
    }

    public boolean isStreamLive(String username) {
        try {
            String url = "https://api.twitch.tv/helix/streams?user_login=" + username;

            Request request = new Request.Builder()
                .url(url)
                .addHeader("Client-ID", clientId)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Map<String, Object> json = gson.fromJson(response.body().string(), Map.class);

                // Приведение data к списку
                List<Map<String, Object>> streams = (List<Map<String, Object>>) json.get("data");

                return !streams.isEmpty(); // Если список не пустой, значит стрим идет
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    String extractUsernameFromUrl(String url) {
        return url.replaceAll("https?://(www\\.)?twitch\\.tv/", "");
    }
}
