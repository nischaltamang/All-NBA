package com.gmail.jorgegilcavazos.ballislife.features.data;

import com.google.gson.JsonObject;

/**
 * Represents content of a Streamable video. More info can be found
 * <a href="https://streamable.com/documentation">here</a>.
 */
public class Streamable {
    private int status;
    private String title;
    private String desktopVideoUrl;
    private int desktopVideoWidth;
    private int desktopVideoHeight;
    private String mobileVideoUrl;
    private int mobileVideoWidth;
    private int mobileVideoHeight;
    private String url;
    private String thumbnailUrl;
    private String message;

    public Streamable(JsonObject jsonObject) {
        status = jsonObject.get("status").getAsInt();
        if (jsonObject.get("title") != null) {
            title = jsonObject.get("title").getAsString();
        }
        if (jsonObject.get("url") != null) {
            url = jsonObject.get("url").getAsString();
        }
        if (jsonObject.get("thumbnail_url") != null) {
            thumbnailUrl = jsonObject.get("thumbnail_url").getAsString();
        }
        if (jsonObject.get("message") != null) {
            if (!jsonObject.get("message").isJsonNull()) {
                message = jsonObject.get("message").getAsString();
            }
        }

        JsonObject files = jsonObject.get("files").getAsJsonObject();

        if (files.has("mp4")) {
            JsonObject desktopVideo = files.get("mp4").getAsJsonObject();
            desktopVideoUrl = "http:" + desktopVideo.get("url").getAsString();
            desktopVideoWidth = desktopVideo.get("width").getAsInt();
            desktopVideoHeight = desktopVideo.get("height").getAsInt();
        }
        if (files.has("mp4-mobile")) {
            JsonObject mobileVideo = files.get("mp4-mobile").getAsJsonObject();
            mobileVideoUrl = "http:" + mobileVideo.get("url").getAsString();
            mobileVideoWidth = mobileVideo.get("width").getAsInt();
            mobileVideoHeight = mobileVideo.get("height").getAsInt();
        }
    }

    public int getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDesktopVideoUrl() {
        return desktopVideoUrl;
    }

    public int getDesktopVideoWidth() {
        return desktopVideoWidth;
    }

    public int getDesktopVideoHeight() {
        return desktopVideoHeight;
    }

    public String getMobileVideoUrl() {
        return mobileVideoUrl;
    }

    public int getMobileVideoWidth() {
        return mobileVideoWidth;
    }

    public int getMobileVideoHeight() {
        return mobileVideoHeight;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getMessage() {
        return message;
    }
}
