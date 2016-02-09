package com.cse403.matchonthestreet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UserProfileActivity extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        listView = (ListView) findViewById(android.R.id.list);

        String[] sampleVal = new String[]{"Tennis match @ Denny",
                "Casual pool play",
                "Team Potato needs a goalkeeper",
                "Basket ball IMA 5v5",
                "Tennis match @ Denny"};

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.activity_list_item, android.R.id.text1);

        listView.setAdapter(listAdapter);
    }
}
