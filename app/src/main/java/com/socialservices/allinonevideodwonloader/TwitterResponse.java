package com.socialservices.allinonevideodwonloader;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

public class TwitterResponse implements Serializable {
    @SerializedName("videos")
    private ArrayList<TwitterResponseModel> videos;

    public ArrayList<TwitterResponseModel> getVideos() {
        return this.videos;
    }

    public void setVideos(ArrayList<TwitterResponseModel> arrayList) {
        this.videos = arrayList;
    }
}
