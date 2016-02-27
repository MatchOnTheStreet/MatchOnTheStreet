package com.cse403.matchonthestreet;

import android.support.test.runner.AndroidJUnit4;

import com.cse403.matchonthestreet.view.MapsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by larioj on 2/26/16.
 */
@RunWith(AndroidJUnit4.class)
public class MapsActivityTest {
    @Rule
    public final ActivityRule<MapsActivity> main = new ActivityRule<>(MapsActivity.class);

    @Test
    public void shouldBeAbleToLaunchScreen() {
    }
}
