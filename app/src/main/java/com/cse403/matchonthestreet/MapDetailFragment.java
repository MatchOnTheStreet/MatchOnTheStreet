package com.cse403.matchonthestreet;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Lance on 2/7/16.
 */
public class MapDetailFragment extends android.support.v4.app.Fragment {

    private String TAG = "MapDetailFrag";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        /** Inflating the layout for this fragment **/
        View mView = inflater.inflate(R.layout.map_detail_layout, null);
        TextView tv = (TextView) mView.findViewById(R.id.detail_text);
        String passedText = getArguments().getString("detailText");
        if (passedText != null) {
            tv.setText(passedText);
        }

        return mView;
    }


}
