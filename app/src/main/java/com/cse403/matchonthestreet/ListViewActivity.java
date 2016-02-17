package com.cse403.matchonthestreet;

import android.app.ListActivity;
//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by Hao on 2/6/16.
 *
 * This is the list activity of the application.
 * A representation of all available events in a list format.
 *
 */
public class ListViewActivity extends AppCompatActivity {

    RecyclerView listView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        //getActionBar().show();
        toolbar = (Toolbar) findViewById(R.id.list_view_toolbar);
        setSupportActionBar(toolbar);

        // get ListView obj from xml
        listView = (RecyclerView) findViewById(android.R.id.list);

        // Floating action button to the map view
        FloatingActionButton fabToMap =
                (FloatingActionButton) this.findViewById(R.id.fab_list_to_map);

        // Sample string values to store in list
        String[] sampleVal = new String[]{"Tennis match @ Denny",
                "Casual pool play",
                "Team Potato needs a goalkeeper",
                "Basket ball IMA 5v5",
                "Tennis match @ Denny",
                "Casual pool play",
                "Team Potato needs a goalkeeper",
                "Basket ball IMA 5v5",
                "Tennis match @ Denny",
                "Casual pool play",
                "Team Potato needs a goalkeeper",
                "Basket ball IMA 5v5"};

        // Adapter for the list
        // Params: (Context, Layout for a row, ID of the TextView, Array of data)
        /* ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this,
                android.R.layout.activity_list_item, android.R.id.text1, sampleVal);
         */

        List<ListItem> listItems = new ArrayList<>();
        Random rand = new Random();
        String largeStr = getString(R.string.large_text);

        /* Hardcoded population of list items */
        for (String s : sampleVal) {
            Location l = new Location("dummy");
            l.setLatitude(rand.nextDouble() * 90);
            l.setLongitude(rand.nextDouble() * 90);
            l.setAltitude(rand.nextDouble() * 90);

            int start = rand.nextInt(largeStr.length() - s.length());
            String d = largeStr.substring(start, start + s.length());

            Event e = new Event(s, l, new Date(2000 + rand.nextInt(17), rand.nextInt(12) + 1, rand.nextInt(28) + 1), d);

            listItems.add(new ListItem(e));
        }

        ListViewAdapter listAdapter = new ListViewAdapter(this, R.layout.list_item_layout, listItems);

        listView.setAdapter(listAdapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = ((ListItem) listView.getItemAtPosition(position)).event.location.toString();

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Item index :" + itemPosition + "  Location : " + itemValue, Toast.LENGTH_LONG)
                        .show();

            }

        });

        fabToMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ListViewActivity.this, MapsActivity.class);

                // TODO: send extra msg to map view, e.g. user location

                startActivity(intent);
            }
        });
    }
}
