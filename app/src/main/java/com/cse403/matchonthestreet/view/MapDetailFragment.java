package com.cse403.matchonthestreet.view;

import android.os.AsyncTask;
import android.os.Bundle;
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

import java.sql.SQLException;
import java.util.List;

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
    /** The MapDetailFragment view */
    private View mView;
    /** The button that allows the user to attend / unattend an event */
    private ToggleButton toggleButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** Inflating the layout for this fragment **/
        mView = inflater.inflate(R.layout.map_detail_layout, null);

        // Update the title of the event from the passed bundle
        TextView tv = (TextView) mView.findViewById(R.id.detail_text);
        String passedText = getArguments().getString("detailText");
        if (passedText != null) {
            tv.setText(passedText);
        }

        // Update the date of the event
        String eventDate = getArguments().getString("date");
        if (eventDate != null) {
            TextView dateText = (TextView) mView.findViewById(R.id.event_date);

            dateText.setText(eventDate);
        }

        // Update the description of the event
        String description = getArguments().getString("description");
        if (description != null) {
            TextView descriptionText = (TextView) mView.findViewById(R.id.event_description);
            descriptionText.setText(description);
        }

        Event event = getArguments().getParcelable("eventObject");

        // Update the number of people attending the event
        int numAttendees = getArguments().getInt("numAttendees");
        setNumAttending(numAttendees, mView, event);



        // Initialize the toggle button
        toggleButton = (ToggleButton) mView.findViewById(R.id.attendingToggle);
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
                    } else {
                        // don't change the state of the button if the event is null
                        toggleButton.setChecked(getArguments().getBoolean("amAttending"));
                    }
                } else {
                    Log.d(TAG, "unattend event");
                    if (event != null) {
                        unattendEvent(event);
                    } else {
                        // don't change the state of the button if the event is null
                        toggleButton.setChecked(getArguments().getBoolean("amAttending"));
                    }
                }
            }
        });
        return mView;
    }

    /**
     * This sets and updates the number of people attending an event
     * @param numAttending the new value for the number of people attending
     * @param mView the view to update
     */
    private void setNumAttending(int numAttending, View mView, Event event) {
        if (numAttending >= 0 && mView != null) {
            TextView attendanceText = (TextView) mView.findViewById(R.id.attendees);
            String attendees = "";
            if (event != null && event.attending.size() > 0) {
                List<Account> accnts = event.attending;
                for(int i = 0; i < accnts.size() - 1; i++) {
                    Log.d(TAG, "Attendee " + i + ": " + accnts.get(i).getName());
                    attendees += accnts.get(i).getName() + ", ";
                }
                attendees += accnts.get(accnts.size() - 1).getName();
            } else {
                attendanceText.setText("0 attending");
            }
            String text = "" + numAttending + " attending: " + attendees;
            attendanceText.setText(text);
            mView.invalidate();
        } else if (mView != null) {
            TextView attendanceText = (TextView) mView.findViewById(R.id.attendees);
            if (attendanceText != null)
                attendanceText.setText("0 attending");
            mView.invalidate();

        }
    }

    /**
     * Removes the users attendance from the passed event
     * @param event the event to remove the current user from
     */
    private void unattendEvent(Event event) {
        AsyncTask<Event, Event, Event> task = new AsyncTask<Event, Event, Event>() {
            @Override
            protected Event doInBackground(Event[] params) {
                Account accnt = ((MOTSApp) getActivity().getApplication()).getMyAccount();
                if (accnt != null) {
                    Log.d(TAG, "Account: " + accnt.getName() + " found");
                    try {
                        if (params.length == 1) {
                            Event event1 = params[0];
                            DBManager.removeAttendance(accnt, event1);
                            event1.removeAttendee(accnt);
                            return event1;
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        Log.d(TAG, "SQL Exception");
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "no account found");
                }

                return null;
            }
            @Override
            protected void onPostExecute(Event passedEvent) {
                if (passedEvent!=null) {
                    Log.d(TAG, "user is not attending: " + passedEvent.title);
                    int numAttendees = getArguments().getInt("numAttendees");
                    setNumAttending(numAttendees - 1, mView, passedEvent);
                    // Overwrite the old event in the MapsActivity to have the new list of attendees
                    ((MapsActivity) getActivity()).addEventToMap(passedEvent);
                }
            }
        };
        task.execute(event);

    }

    /**
     * Adds the current user to the attendees of the passed event
     * @param event the event to add the current user to
     */
    private void attendEvent(Event event) {
        AsyncTask<Event, Event, Event> task = new AsyncTask<Event, Event, Event>() {
            @Override
            protected Event doInBackground(Event[] params) {
                Account accnt = ((MOTSApp) getActivity().getApplication()).getMyAccount();
                if (accnt != null) {
                    Log.d(TAG, "Account: " + accnt.getName() + " found");
                    try {
                        if (params.length == 1) {
                            Event event1 = params[0];
                            DBManager.addAccountToEvent(accnt, event1);
                            event1.addAttendee(accnt);
                            return event1;
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        Log.d(TAG, "SQL Exception");
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "no account found");
                }

                return null;
            }
            @Override
            protected void onPostExecute(Event passedEvent) {
                if (passedEvent!=null) {
                    Log.d(TAG, "user is now attending: " + passedEvent.title);
                    int numAttendees = getArguments().getInt("numAttendees");
                    setNumAttending(numAttendees + 1, mView, passedEvent);
                    // Overwrite the old event in the MapsActivity to have the new list of attendees
                    ((MapsActivity) getActivity()).addEventToMap(passedEvent);
                }
            }
        };
        task.execute(event);

    }
}
