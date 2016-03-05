package com.cse403.matchonthestreet;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.runner.AndroidJUnit4;

import com.cse403.matchonthestreet.view.LoginActivity;
import com.cse403.matchonthestreet.view.MapsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by larioj on 2/26/16.
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    @Rule
    public final ActivityRule<LoginActivity> main = new ActivityRule<>(LoginActivity.class);

    @Test
    public void shouldBeAbleToLaunchScreen() {
        onView(withId(R.id.login_background)).check(matches(isDisplayed()));
        onView(withId(R.id.text_welcome)).check(matches(isDisplayed()));

    }
}
