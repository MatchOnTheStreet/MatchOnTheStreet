package com.cse403.matchonthestreet.view;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cse403.matchonthestreet.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Lance on 2/7/16.
 *
 * The MapDetailFragment is the small detail view that pops up from the bottom of the screen when
 * a user taps on a marker and will display info regarding the event and will have a button so the user
 * can attend the event.
 */
public class MapDetailFragment extends android.support.v4.app.Fragment {
    // Tag for logging purposes
    private String TAG = "MapDetailFrag";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        /** Inflating the layout for this fragment **/
        View mView = inflater.inflate(R.layout.map_detail_layout, null);

        // Update the title of the event from the passed bundle
        TextView tv = (TextView) mView.findViewById(R.id.detail_text);
        String passedText = getArguments().getString("detailText");
        if (passedText != null) {
            tv.setText(passedText);
        }

        String eventDate = getArguments().getString("date");
        if (eventDate != null) {
            TextView dateText = (TextView) mView.findViewById(R.id.event_date);

            dateText.setText(eventDate);
        }

        String description = getArguments().getString("description");
        if (description != null) {
            TextView descriptionText = (TextView) mView.findViewById(R.id.event_description);
            descriptionText.setText(description);
        }

        final FloatingActionButton fabButton = (FloatingActionButton) mView.findViewById(R.id.fab_attend_event);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Attend Event Button");
                fabButton.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));

            }
        });

        return mView;
    }


}
