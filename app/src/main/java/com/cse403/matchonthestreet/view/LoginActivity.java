package com.cse403.matchonthestreet.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.cse403.matchonthestreet.R;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.widget.LoginButton;

import android.util.Log;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;

/**
 * Simple facebook login page. Allows the user to log into the app through Facebook.
 */
public class LoginActivity extends NavActivity {

    private CallbackManager callbackManager;
    private TextView info;
    private LoginButton loginButton;
    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        callbackManager = CallbackManager.Factory.create();


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(LoginResult loginResult) {
                if (Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            Log.d(TAG, currentProfile.getName());
                            mProfileTracker.stopTracking();
                        }
                    };
                    mProfileTracker.startTracking();
                } else {
                    Profile profile = Profile.getCurrentProfile();
                    String s = profile.getName();
                    Log.d(TAG, s);
                }
                info.setText("User ID:  " + loginResult.getAccessToken().getUserId());
                SharedPreferences mPrefs = getSharedPreferences("userPrefs", 0);
                SharedPreferences.Editor mEditor = mPrefs.edit();
                mEditor.putString("userID", loginResult.getAccessToken().getUserId());
                mEditor.commit();



                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {

                info.setText("Login attempt cancelled.");
                //TODO: Remove for final release
                SharedPreferences mPrefs = getSharedPreferences("userPrefs", 0);
                SharedPreferences.Editor mEditor = mPrefs.edit();
                mEditor.putString("userID", "temp account");
                mEditor.commit();

                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                startActivity(intent);

            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}