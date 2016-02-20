package com.cse403.matchonthestreet.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cse403.matchonthestreet.R;
import com.cse403.matchonthestreet.models.Account;
import com.cse403.matchonthestreet.models.Event;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
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

        // TODO: change from dummy data to db data
        Account dummyAccount = new Account(123);
        Event e1 = new Event(123, "Basketball", null, null, 60, null, "Cool 1v1 basketball");
        Event e2 = new Event(123, "Soccer", null, null, 60, null, "3v3 Soccer");
        dummyAccount.addEvent(e1);
        dummyAccount.addEvent(e2);
        Profile profile = Profile.getCurrentProfile();
        username = (TextView) findViewById(R.id.username);

        if (profile != null) {
            username.setText(profile.getName());
        } else {
            username.setText("null username");
        }


        List<Event> eventsAttending = dummyAccount.getEvents();
        List<String> sampleVals = new ArrayList<String>();
        for (Event e : eventsAttending) {
            sampleVals.add(e.title + ": " + e.description);
        }

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sampleVals);

        listView.setAdapter(listAdapter);
    }
}