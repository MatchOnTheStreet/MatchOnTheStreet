package com.cse403.matchonthestreet;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.runner.AndroidJUnit4;

import com.cse403.matchonthestreet.view.AddEventActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by larioj on 2/26/16.
 */
@RunWith(AndroidJUnit4.class)
public class AddEventActivityTest {
    @Rule
    public final ActivityRule<AddEventActivity> main = new ActivityRule<>(AddEventActivity.class);

    @Test
    public void shouldBeAbleToLaunchScreen() {
        onView(withId(R.id.event_title)).check(matches(isDisplayed()));
        onView(withId(R.id.event_from_row)).check(matches(isDisplayed()));
    }

    @Test
    public void testClickEventField() {
        onView(withId(R.id.event_from_row)).perform(click());
        onView(withId(R.id.event_from_row)).check(matches(hasFocus()));
    }
}
