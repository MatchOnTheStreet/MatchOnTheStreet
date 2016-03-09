package com.cse403.matchonthestreet.view;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cse403.matchonthestreet.R;
import com.cse403.matchonthestreet.backend.DBManager;
import com.cse403.matchonthestreet.controller.MOTSApp;
import com.cse403.matchonthestreet.controller.RecyclerViewAdapter;
import com.cse403.matchonthestreet.models.Account;
import com.cse403.matchonthestreet.models.Event;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This class gives the user a view of their profile. Their profile includes their
 * name and a list of events they are currently signed up to attend.
 */
public class UserProfileActivity extends NavActivity {

    // The swipe refresh layout
    private SwipeRefreshLayout swipeRefresh;

    // View that stores the list of events
    private RecyclerView recyclerView;

    // Tag for debugging purposes
    private static final String TAG = "UserProfileActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_user_profile);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefresh.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, null));
        addEventsToList(new ArrayList<Event>());
        
        Profile profile = Profile.getCurrentProfile();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (profile != null) {
                getSupportActionBar().setTitle(profile.getName() + "\'s Events");
            } else {
                getSupportActionBar().setTitle("Not logged in!");
            }
        }

        // show the "refreshing" indicator
        swipeRefresh.post(new Runnable() {
            @Override public void run() {
                swipeRefresh.setRefreshing(true);
            }
        });
        refreshContent();
    }


    /**
     * Adds events to the view.
     *
     * @param events The list of events to add
     */
    private void addEventsToList(List<Event> events) {
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, events);
        recyclerView.setAdapter(adapter);

    }

    private void refreshContent() {
        //swipeRefresh.setRefreshing(true);
        (new setListEventsTask()).execute();
    }

    /**
     * Queries the database for a List of Events and sets them in the view.
     */
    private class setListEventsTask extends AsyncTask<Integer, Integer, List<Event>> {

        @Override
        protected List<Event> doInBackground(Integer... params) {
            try {
                Account account = ((MOTSApp) getApplication()).getMyAccount();
                List<Event> events = DBManager.getEventsAttendedByAccountWithAccounts(account);
                Log.d(TAG, account.getName());
                if (events.isEmpty()) {
                    Log.d(TAG, "empty events");
                } else {
                    for (Event e : events) {
                        Log.d(TAG, e.getTitle());
                        Log.d(TAG, "# of Attendees: " + e.getAttending().size());
                    }
                }
                return events;
            } catch (ClassNotFoundException e) {
                Log.d(TAG, "ClassNotFoundException");
                e.printStackTrace();
            } catch (SQLException e) {
                Log.d(TAG, "SQL Exception");
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(List<Event> events) {
            addEventsToList(events);
            swipeRefresh.setRefreshing(false);
        }

    }

}
