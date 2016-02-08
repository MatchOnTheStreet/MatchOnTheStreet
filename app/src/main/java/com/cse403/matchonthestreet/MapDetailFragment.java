package com.cse403.matchonthestreet;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Lance on 2/7/16.
 */
public class MapDetailFragment extends android.support.v4.app.Fragment {

    View mView;
   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }*/
    public void setDetailText(String str) {
        TextView tv = (TextView) mView.findViewById(R.id.detail_text);
        tv.setText(str);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        mView = inflater.inflate(R.layout.map_detail_layout, null);
        return mView;
    }
}
