package com.cse403.matchonthestreet;

import android.location.Location;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class FirstUnitTest {
    @Test
    public void event_test() throws Exception {
        Location location = new Location("");
        location.setLatitude(0.0d);
        location.setLongitude(0.0d);
        Event e = new Event("Basketball", location, new Date(100), "fun");
        Account a = new Account("hello");
        assertFalse(e.isAttendedBy(a));
    }
}