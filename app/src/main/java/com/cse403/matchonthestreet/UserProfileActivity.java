package com.cse403.matchonthestreet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;
import java.util.List;

/**
 * This class gives the user a view of their profile. Their profile includes their
 * name and a list of events they are currently signed up to attend.
 */
public class UserProfileActivity extends AppCompatActivity {
    ListView listView;
    Button button;

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


        List<String> sampleVals = new ArrayList<String>();
        sampleVals.add("Basketball at 4:30");
        sampleVals.add("Soccer at 6");

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sampleVals);

        listView.setAdapter(listAdapter);
    }
}
