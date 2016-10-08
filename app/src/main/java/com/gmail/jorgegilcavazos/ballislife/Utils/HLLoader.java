package com.gmail.jorgegilcavazos.ballislife.Utils;

import android.util.Log;

import com.gmail.jorgegilcavazos.ballislife.General.Highlight;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HLLoader {

    private static String TAG = "HLLoader";
    private String raw;

    public HLLoader(String raw) {
        this.raw = raw;
    }

    public ArrayList<Highlight> fetchHighlights() {
        ArrayList<Highlight> highlights = new ArrayList<>();

        try {
                JSONArray videos = new JSONObject(raw).getJSONArray("videos");
                for (int j = 0; j < videos.length(); j++) {
                    JSONObject vData = videos.getJSONObject(j);
                    JSONObject activity = vData.getJSONArray("activity").getJSONObject(0);
                    Highlight hl = new Highlight();
                    hl.dateAdded = vData.getDouble("date_added");
                    hl.ext = vData.getString("ext");
                    hl.fileID = vData.getString("file_id");
                    hl.thumbnailURL = "http:" + vData.getString("poster_url");

                    hl.title = activity.getString("title");
                    if (hl.title == null) {
                        hl.title = vData.getString("reddit_title");
                    }

                    hl.redditURL = vData.getString("reddit_url");

                    JSONObject mobileFile;
                    if (vData.getJSONObject("files").has("mp4-mobile"))
                        mobileFile = vData.getJSONObject("files").getJSONObject("mp4-mobile");
                    else
                        mobileFile = vData.getJSONObject("files").getJSONObject("mp4");

                    hl.height = mobileFile.getInt("height");
                    hl.width = mobileFile.getInt("width");
                    hl.videoURL = "http:" + mobileFile.getString("url");

                    highlights.add(hl);
                }

        }catch (Exception e) {
            if (MyDebug.LOG) {
                Log.e(TAG, "Error leyendo JSON " + e.toString());
            }
        }


        return highlights;
    }
}
