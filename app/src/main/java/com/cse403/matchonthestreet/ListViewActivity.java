package com.cse403.matchonthestreet;

import android.app.ListActivity;
//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This is the list activity of the application.
 * A representation of all available events in a list format.
 *
 */
public class ListViewActivity extends ListActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        getActionBar().show();

        // get ListView obj from xml
        listView = (ListView) findViewById(android.R.id.list);

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
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this,
                android.R.layout.activity_list_item, android.R.id.text1, sampleVal);

        listView.setAdapter(listAdapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
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
