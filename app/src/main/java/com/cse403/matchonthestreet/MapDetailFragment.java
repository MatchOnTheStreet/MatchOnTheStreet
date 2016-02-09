package com.cse403.matchonthestreet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        return mView;
    }


}
