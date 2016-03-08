package com.cse403.matchonthestreet.view;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import com.cse403.matchonthestreet.R;
import com.cse403.matchonthestreet.backend.DBManager;
import com.cse403.matchonthestreet.controller.MOTSApp;
import com.cse403.matchonthestreet.models.Account;
import com.cse403.matchonthestreet.models.Event;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.widget.LoginButton;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toolbar;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple facebook login page. Allows the user to log into the app through Facebook.
 */
public class LoginActivity extends NavActivity {

    // Callback manager for the Facebook login button
    private CallbackManager callbackManager;

    // Sets information for user on cancelled or failed login
    private TextView info;

    // Facebook login button
    private LoginButton loginButton;

    // Tag for debugging purposes
    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            getActionBar().hide();
        }

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        callbackManager = CallbackManager.Factory.create();

        // Skips to the MapsActivity if a user has already logged in before
        // probably a better way to check this using the facebook API though. --Lance
        Intent navIntent = getIntent();

        SharedPreferences mPrefs = getSharedPreferences("userPrefs", 0);
        String mString = mPrefs.getString("FBUserID", "not found");
        if (!mString.equals("not found") && !navIntent.getBooleanExtra("fromSidebar", false)) {
            getUserFromDB();
            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
            startActivity(intent);
        }

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

                            Profile profile = Profile.getCurrentProfile();
                            if (profile != null) {
                                Account me = new Account((int) Long.parseLong(profile.getId()), profile.getName());
                                ((MOTSApp) getApplication()).setMyAccount(me);
                                Account accnt = ((MOTSApp) getApplication()).getMyAccount();
                                Log.d(TAG, "my account is: " + accnt.getName());
                                saveUserIDtoDB(accnt);
                            }
                        }
                    };
                    mProfileTracker.startTracking();
                } else {
                    Profile profile = Profile.getCurrentProfile();
                    Account me = new Account((int) Long.parseLong(profile.getId()), profile.getName());
                    ((MOTSApp) getApplication()).setMyAccount(me);
                    Account accnt = ((MOTSApp) getApplication()).getMyAccount();
                    Log.d(TAG, "my account is: " + accnt.getName());
                    saveUserIDtoDB(accnt);

                    Log.d(TAG, profile.getName());

                }
                info.setText("User ID:  " + loginResult.getAccessToken().getUserId());



                // Saves the userID to the sharedpreferences which saves to the device memory
                // so we can verify that a user has logged in with FB. probably a better way
                // to do this using the facebook API. --Lance
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
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });
    }

    /**
     * Gets a user out from the database.
     */
    private void getUserFromDB() {
        Log.d(TAG, "GetUserFromDB");
        SharedPreferences mPrefs = getSharedPreferences("userPrefs", 0);
        String mString = mPrefs.getString("FBUserID", "not found");
        AsyncTask<Integer, Account, Account> task = new AsyncTask<Integer, Account, Account>() {
            @Override
            protected Account doInBackground(Integer[] params) {

                try {
                    if (params.length == 1) {
                        int uid = params[0];
                        Account account = DBManager.getAccountById(uid);
                        if (account != null) {
                            return account;
                        }
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
            protected void onPostExecute(Account account) {
                if (account != null) {
                    ((MOTSApp) getApplication()).setMyAccount(account);
                    Log.d(TAG, "received account from db: " + account.getName());
                } else {
                    Log.d(TAG, "account returned from db is null");
                }
            }
        };
        task.execute(Integer.parseInt(mString));

    }

    /**
     * Saves the ID of a given account to the database
     *
     * @param account The account to save the id of
     */
    private void saveUserIDtoDB(Account account) {

        SharedPreferences mPrefs = getSharedPreferences("userPrefs", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString("FBUserID", "" + account.getUid());
        mEditor.apply();

        AsyncTask<Account, Account, Account> task = new AsyncTask<Account, Account, Account>() {
            @Override
            protected Account doInBackground(Account[] params) {
                try {
                    if (params.length == 1) {
                        Account account1 = params[0];
                        String uid = "" + account1.getUid();
                        String name = account1.getName();
                        Log.d(TAG, "Adding account to dB: " + name + " " + uid);
                        DBManager.addAccount(account1);
                    }
                    return null;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    Log.d(TAG, "SQL Exception");
                    e.printStackTrace();
                }
                return null;
            }
        };

        task.execute(account);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}