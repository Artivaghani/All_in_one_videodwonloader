package com.socialservices.allinonevideodwonloader;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/* renamed from: com.cd.statussaver.model.TwitterResponseModel */
public class TwitterResponseModel implements Serializable {
    @SerializedName("bitrate")
    private int bitrate;
    @SerializedName("duration")
    private int duration;
    @SerializedName("size")
    private int size;
    @SerializedName("source")
    private String source;
    @SerializedName("text")
    private String text;
    @SerializedName("thumb")
    private String thumb;
    @SerializedName("type")
    private String type;
    @SerializedName("url")
    private String url;

    public String getSource() {
        return this.source;
    }

    public void setSource(String str) {
        this.source = str;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String str) {
        this.text = str;
    }

    public String getThumb() {
        return this.thumb;
    }

    public void setThumb(String str) {
        this.thumb = str;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int i) {
        this.duration = i;
    }

    public int getBitrate() {
        return this.bitrate;
    }

    public void setBitrate(int i) {
        this.bitrate = i;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int i) {
        this.size = i;
    }
}
