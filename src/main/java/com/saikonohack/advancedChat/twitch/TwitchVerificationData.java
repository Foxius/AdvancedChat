package com.saikonohack.advancedChat.twitch;

public class TwitchVerificationData {
    private final String twitchChannelUrl;
    private final String verificationPhrase;

    public TwitchVerificationData(String twitchChannelUrl, String verificationPhrase) {
        this.twitchChannelUrl = twitchChannelUrl;
        this.verificationPhrase = verificationPhrase;
    }

    public String getTwitchChannelUrl() {
        return twitchChannelUrl;
    }

    public String getVerificationPhrase() {
        return verificationPhrase;
    }
}
