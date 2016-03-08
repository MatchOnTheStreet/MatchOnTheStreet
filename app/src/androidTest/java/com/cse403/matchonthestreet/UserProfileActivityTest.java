package com.cse403.matchonthestreet;

import android.support.test.runner.AndroidJUnit4;

import com.cse403.matchonthestreet.view.UserProfileActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class UserProfileActivityTest {
    @Rule
    public final ActivityRule<UserProfileActivity> main = new ActivityRule<>(UserProfileActivity.class);

    @Test
    public void shouldBeAbleToLaunchScreen() {
       // onView(withId(R.id.button1))
    }
}
