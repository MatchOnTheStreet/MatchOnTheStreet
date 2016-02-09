package com.cse403.matchonthestreet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * This class gives the user a view of their profile. Their profile includes their
 * name and a list of events they are currently signed up to attend.
 */
public class UserProfileActivity extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        listView = (ListView) findViewById(android.R.id.list);

        List<String> sampleVals = new ArrayList<String>();
        sampleVals.add("Basketball at 4:30");
        sampleVals.add("Soccer at 6");

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sampleVals);

        listView.setAdapter(listAdapter);
    }
}
