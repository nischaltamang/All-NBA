package com.example.jorgegil.closegamealert.View.Activities;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.jorgegil.closegamealert.R;

import java.util.ArrayList;


public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        setUpToolbar();

        ListView optionsListView = (ListView) findViewById(R.id.listview);

        ArrayList<String> options = new ArrayList<>();
        options.add(getResources().getString(R.string.notifications));
        options.add(getResources().getString(R.string.theme));

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.settings_list_item, options);
        optionsListView.setAdapter(adapter);


    }

    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
