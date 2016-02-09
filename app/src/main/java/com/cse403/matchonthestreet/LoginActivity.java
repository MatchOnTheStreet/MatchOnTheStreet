package com.cse403.matchonthestreet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import com.facebook.login.widget.LoginButton;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;

/**
 * Simple facebook login page
 */
public class LoginActivity extends Activity {

    private CallbackManager callbackManager;
    private TextView info;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
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