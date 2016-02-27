package com.cse403.matchonthestreet.view;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cse403.matchonthestreet.R;
import com.cse403.matchonthestreet.backend.DBManager;
import com.cse403.matchonthestreet.controller.MOTSApp;
import com.cse403.matchonthestreet.models.Account;
import com.cse403.matchonthestreet.models.Event;
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
    private ListView listView;
    private Button button;
    private TextView username;

    private static final String TAG = "UserProfileActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_user_profile);

        listView = (ListView) findViewById(android.R.id.list);

        button = (Button) findViewById(android.R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        Profile profile = Profile.getCurrentProfile();
        username = (TextView) findViewById(R.id.username);

        if (profile != null) {
            username.setText(profile.getName());
        } else {
            username.setText("null username");
        }
        (new setListEventsTask()).execute();
    }



    private void addEventsToList(List<String> vals) {
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, vals);
        listView.setAdapter(listAdapter);

    }

    private class setListEventsTask extends AsyncTask<Integer, Integer, List<Event>> {

        @Override
        protected List<Event> doInBackground(Integer... params) {
            Log.d(TAG, "here");
            try {
                Account account = ((MOTSApp) getApplication()).getMyAccount();
                List<Event> events = DBManager.getEventsAttendedByAccount(account);
                Log.d(TAG, account.getName());
                if (events.isEmpty()) {
                    Log.d(TAG, "empty events");
                } else {
                    for (Event e : events) {
                        Log.d(TAG, e.title);
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
            Log.d(TAG, "On Post Execute");
            if (events != null) {
                List<String> vals = new ArrayList<String>();
                for (Event e : events) {
                    vals.add(e.title + ": " + e.description);
                    addEventsToList(vals);

                }
            }

        }

    };

}
