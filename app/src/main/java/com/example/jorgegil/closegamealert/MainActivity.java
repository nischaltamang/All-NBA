package com.example.jorgegil.closegamealert;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    ShareExternalServer appUtil;
    String regId;
    AsyncTask<Void, Void, String> shareRegidTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appUtil = new ShareExternalServer();

        regId = getIntent().getStringExtra("regId");
        Log.d("MainActivity", "regId: " + regId);

        final Context context = this;
        shareRegidTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String result = appUtil.shareRegIdWithAppServer(context, regId);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                shareRegidTask = null;
                Toast.makeText(getApplicationContext(), result,
                        Toast.LENGTH_LONG).show();
            }

        };
        shareRegidTask.execute(null, null, null);

        String url = "http://phpstack-4722-10615-67130.cloudwaysapps.com/nbcsports.php";

        StringRequest request = new StringRequest("http://phpstack-4722-10615-67130.cloudwaysapps.com/nbcsports.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseData(response);
                System.out.println("JSON:" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error) {
                System.out.println("JSON ERROR");
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);


        //String jsonString = "[{\"homeTeam\":\"CHI\",\"awayTeam\":\"OKC\",\"homeScore\":\"50\",\"awayScore\":\"\",\"clock\":\"8:00 PM\",\"period\":\"\"},{\"homeTeam\":\"MIN\",\"awayTeam\":\"MIA\",\"homeScore\":\"\",\"awayScore\":\"\",\"clock\":\"8:00 PM\",\"period\":\"\"},{\"homeTeam\":\"DAL\",\"awayTeam\":\"CHA\",\"homeScore\":\"\",\"awayScore\":\"\",\"clock\":\"8:30 PM\",\"period\":\"\"},{\"homeTeam\":\"DEN\",\"awayTeam\":\"UTA\",\"homeScore\":\"\",\"awayScore\":\"\",\"clock\":\"9:00 PM\",\"period\":\"\"},{\"homeTeam\":\"POR\",\"awayTeam\":\"MEM\",\"homeScore\":\"\",\"awayScore\":\"\",\"clock\":\"10:30 PM\",\"period\":\"\"}]";


    }

    public void parseData(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            int numOfEvents = jsonArray.length();

            String[] homeTeam = new String[numOfEvents];
            String[] awayTeam = new String[numOfEvents];
            String[] homeScore = new String[numOfEvents];
            String[] awayScore = new String[numOfEvents];
            String[] clock = new String[numOfEvents];
            String[] period = new String[numOfEvents];

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                homeTeam[i] = jsonObject.getString("homeTeam");
                awayTeam[i] = "" + jsonObject.getString("awayTeam");
                homeScore[i] = jsonObject.getString("homeScore");
                awayScore[i] = jsonObject.getString("awayScore");
                clock[i] = jsonObject.getString("clock");
                period[i] = jsonObject.getString("period");

                if (homeScore[i].equals("")) {
                    homeScore[i] = "0";
                }

                if (awayScore[i].equals("")) {
                    awayScore[i] = "0";
                }

            }

            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(new CustomAdapter(this, homeTeam, awayTeam, homeScore,
                    awayScore, clock, period));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
