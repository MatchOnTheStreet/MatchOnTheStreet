package com.cse403.matchonthestreet;

import android.location.Location;

import com.cse403.matchonthestreet.models.Account;
import com.cse403.matchonthestreet.models.Event;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.Date;

/**
 * Test cases for Event Class
 * Note: the event class is really important to our system.
 */
public class EventTest extends TestCase {

    Event e1;
    Event e2;
    Event e3;
    Account a1;
    Account a2;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Location location = new Location("");
        location.setLatitude(0.0d);
        location.setLongitude(0.0d);

        Location loc2 = new Location("bar");
        loc2.setLatitude(0.0d);
        loc2.setLongitude(0.0d);

        e1 = new Event(0, "Basketball", location, new Date(100), 100, new Date(80), "fun");
        e2 = new Event(0, "Basketball", location, new Date(100), 200, new Date(23), "fun");
        e3 = new Event(0, "Basketball", loc2, new Date(100), 200, new Date(23), "fun");

        a1 = new Account(123, "baro");
        a2 = new Account(456, "baro");

        e2.addAttendee(a1);

    }


    @Test
    public void testIsAttendedByFalse() throws Exception {
        assertFalse(e1.isAttendedBy(a1));
    }

    @Test
    public void testIsAttendedByTrue() throws Exception {
        assertTrue(e2.isAttendedBy(a1));
    }


    @Test
    public void testAddAttendeeEmpty() throws Exception {
        assertTrue(e1.addAttendee(a1));
        assertTrue(e1.isAttendedBy(a1));
    }


    @Test
    public void testAddAttendeeNonEmpty() throws Exception {
        assertTrue(e2.addAttendee(a2));
        assertTrue(e2.isAttendedBy(a2));
    }

    @Test
    public void testAddAttendeeFalse() throws Exception {
        assertFalse(e2.addAttendee(a1));
        assertTrue(e2.isAttendedBy(a1));
    }

    @Test
    public void testContainsStringTitle() throws Exception {
        assertTrue(e1.containsString("bask"));
    }

    @Test
    public void testContainsStringDescription() throws Exception {
        assertTrue(e1.containsString("un"));
    }

    @Test
    public void testContainsStringFalse() throws Exception {
        assertFalse(e1.containsString("zzzz"));
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(e2.equals(e3));
    }
}