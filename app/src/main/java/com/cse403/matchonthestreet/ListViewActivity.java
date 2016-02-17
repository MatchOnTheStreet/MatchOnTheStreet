package com.cse403.matchonthestreet;

import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

//import android.support.v7.app.AppCompatActivity;

/**
 * Created by Hao on 2/6/16.
 *
 * This is the list activity of the application.
 * A representation of all available events in a list format.
 *
 */
public class ListViewActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivityTransitions();
        setContentView(R.layout.activity_list_view);

        //ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), );

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get ListView obj from xml
        listView = (ListView) findViewById(android.R.id.list);

        // Floating action button to the map view
        FloatingActionButton fabToMap =
                (FloatingActionButton) this.findViewById(R.id.fab_list_to_map);


        ListViewAdapter listAdapter = new ListViewAdapter(this, R.layout.list_item_layout, populateDummyData());
        listView.setAdapter(listAdapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item value
                String itemValue = ((ListItem) listView.getItemAtPosition(position)).event.location.toString();

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Item index :" + position + "  Location : " + itemValue, Toast.LENGTH_LONG)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_view_search, menu);

        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        try {
            return super.dispatchTouchEvent(motionEvent);
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }

    private List<ListItem> populateDummyData() {
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

        return listItems;
    }
}
