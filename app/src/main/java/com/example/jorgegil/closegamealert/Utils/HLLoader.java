package com.example.jorgegil.closegamealert.Utils;

import android.util.Log;

import com.example.jorgegil.closegamealert.General.Highlight;
import com.fasterxml.jackson.databind.util.JSONPObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jorgegil on 4/30/16.
 */
public class HLLoader {

    private static String TAG = "HLLoader";
    private String raw;

    public HLLoader(String raw) {
        this.raw = raw;
    }

    public ArrayList<Highlight> fetchHighlights() {
        ArrayList<Highlight> highlights = new ArrayList<>();

        try {
            JSONArray groups = new JSONObject(raw).getJSONArray("groups");
            for (int i = 0; i < groups.length(); i++) {
                JSONObject group = groups.getJSONObject(i);
                JSONArray videos = group.getJSONArray("videos");
                for (int j = 0; j < videos.length(); j++) {
                    JSONObject vData = videos.getJSONObject(j);
                    Highlight hl = new Highlight();
                    hl.dateAdded = vData.getDouble("date_added");
                    hl.ext = vData.getString("ext");
                    hl.fileID = vData.getString("file_id");
                    hl.thumbnailURL = "http:" + vData.getString("poster_url");
                    hl.title = vData.getString("result_title");
                    hl.redditURL = vData.getString("reddit_url");

                    JSONObject mobileFile;
                    if (vData.getJSONObject("files").has("mp4-mobile"))
                        mobileFile = vData.getJSONObject("files").getJSONObject("mp4-mobile");
                    else
                        mobileFile = vData.getJSONObject("files").getJSONObject("mp4");

                    hl.height = mobileFile.getInt("height");
                    hl.width = mobileFile.getInt("width");
                    hl.videoURL = "http:" + mobileFile.getString("url");

                    Log.d(TAG, "" + hl.videoURL);
                    highlights.add(hl);
                }
            }
        }catch (Exception e) {
            Log.e(TAG , "Error leyendo JSON");
        }


        return highlights;
    }
}
