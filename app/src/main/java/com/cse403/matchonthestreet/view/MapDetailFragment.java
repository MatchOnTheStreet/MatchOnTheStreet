package com.cse403.matchonthestreet.view;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cse403.matchonthestreet.R;
import com.cse403.matchonthestreet.backend.DBManager;
import com.cse403.matchonthestreet.controller.MOTSApp;
import com.cse403.matchonthestreet.models.Account;
import com.cse403.matchonthestreet.models.Event;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;

import org.w3c.dom.Text;

import java.sql.SQLException;
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

        int numAttendees = getArguments().getInt("numAttendees");
        setNumAttending(numAttendees, mView);


        ToggleButton toggleButton = (ToggleButton) mView.findViewById(R.id.attendingToggle);
        boolean attending = getArguments().getBoolean("amAttending");
        toggleButton.setChecked(attending);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event event = getArguments().getParcelable("eventObject");
                if (((ToggleButton) v).isChecked()) {
                    Log.d(TAG, "attend event");
                    if (event != null) {
                        attendEvent(event);
                    }
                } else {
                    Log.d(TAG, "unattend event");
                    if (event != null) {
                        unattendEvent(event);
                    }
                }
            }
        });
        return mView;
    }

    private void setNumAttending(int numAttending, View mView) {
        if (numAttending > 0 && mView != null) {
            TextView attendanceText = (TextView) mView.findViewById(R.id.attendees);
            String text = "" + numAttending + " attending";
            attendanceText.setText(text);
        }
    }

    private void unattendEvent(Event event) {
        AsyncTask<Event, Event, Event> task = new AsyncTask<Event, Event, Event>() {
            @Override
            protected Event doInBackground(Event[] params) {
                Account accnt = ((MOTSApp) getActivity().getApplication()).getMyAccount();
                if (accnt != null) {
                    Log.d(TAG, "Account: " + accnt.getName() + " found");
                } else {
                    Log.d(TAG, "no account found");
                }
                try {
                    if (params.length == 1) {
                        Event event1 = params[0];
                        DBManager.removeAttendance(accnt, event1);
                        return event1;
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    Log.d(TAG, "SQL Exception");
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Event passedEvent) {
                if (passedEvent!=null) {
                    Log.d(TAG, "user is not attending: " + passedEvent.title);
                }
            }
        };
        task.execute(event);

    }

    private void attendEvent(Event event) {
        AsyncTask<Event, Event, Event> task = new AsyncTask<Event, Event, Event>() {
            @Override
            protected Event doInBackground(Event[] params) {
                Account accnt = ((MOTSApp) getActivity().getApplication()).getMyAccount();
                if (accnt != null) {
                    Log.d(TAG, "Account: " + accnt.getName() + " found");
                } else {
                    Log.d(TAG, "no account found");
                }
                try {
                    if (params.length == 1) {
                        Event event1 = params[0];
                        DBManager.addAccountToEvent(accnt, event1);
                        return event1;
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    Log.d(TAG, "SQL Exception");
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Event passedEvent) {
                if (passedEvent!=null) {
                    Log.d(TAG, "user is now attending: " + passedEvent.title);
                }
            }
        };
        task.execute(event);

    }
}
