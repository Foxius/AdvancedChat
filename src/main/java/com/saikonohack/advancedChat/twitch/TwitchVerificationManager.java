package com.saikonohack.advancedChat.twitch;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TwitchVerificationManager {

    private final Map<UUID, TwitchVerificationData> verificationDataMap = new HashMap<>();

    public void addVerification(UUID playerId, String twitchChannelUrl, String verificationPhrase) {
        verificationDataMap.put(playerId, new TwitchVerificationData(twitchChannelUrl, verificationPhrase));
    }

    public TwitchVerificationData getVerificationData(UUID playerId) {
        return verificationDataMap.get(playerId);
    }

    public void removeVerification(UUID playerId) {
        verificationDataMap.remove(playerId);
    }
}

