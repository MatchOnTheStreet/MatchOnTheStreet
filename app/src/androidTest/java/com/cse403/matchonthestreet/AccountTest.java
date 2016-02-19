package com.cse403.matchonthestreet;

import android.location.Location;

import junit.framework.TestCase;


import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * Test cases for Event Class
 */
public class AccountTest extends TestCase {

    Account a1;
    Account a2;
    Account a3;
    Event e1;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        a1 = new Account("123", "John Doe");
        a2 = new Account("456", "Mark Zuck");
        a3 = new Account("123", "John Doe")


        Location location = new Location("");
        location.setLatitude(0.0d);
        location.setLongitude(0.0d);

        e1 = new Event(0, "Basketball", location, new Date(100), "fun");
    }

    @Test
    public void testGetEventsEmpty() {
        List<Event> events = a1.getEvents();
        assertTrue(events.isEmpty());
    }


}