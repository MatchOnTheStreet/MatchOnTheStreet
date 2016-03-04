package com.cse403.matchonthestreet;

import android.location.Location;

import com.cse403.matchonthestreet.models.Account;
import com.cse403.matchonthestreet.models.Event;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * Test cases for Account Class
 */
public class AccountTest extends TestCase {

    private Account a1;
    private Account a2;
    private Account a3;
    private Account a4;
    private Event e1;
    private Event e2;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        a1 = new Account(123, "bar");
        a2 = new Account(456, "bar");
        a3 = new Account(123, "bar");
        a4 = new Account(789, "bar");



        Location location = new Location("");
        //location.setLatitude(0.0d);
        //location.setLongitude(0.0d);

        e1 = new Event(0, "Basketball", location, new Date(100), 60, new Date(80), "fun");
        e2 = new Event(0, "Soccer", location, new Date(100), 80, new Date(50), "cool");

        a2.addEvent(e1);

        a4.addEvent(e1);
        a4.addEvent(e2);
    }

    @Test
    public void testGetEventsEmpty() {
        List<Event> events = a1.getEvents();
        assertTrue(events.isEmpty());
    }

    @Test
    public void testGetEventsOneElement() {
        List<Event> events = a2.getEvents();
        assertTrue(events.contains(e1));
        assertTrue(events.size() == 1);
    }

    @Test
    public void testGetEventsTwoElements() {
        List<Event> events = a4.getEvents();
        assertTrue(events.contains(e1));
        assertTrue(events.contains(e2));
        assertTrue(events.size() == 2);
    }

    @Test
    public void testAddEventEmpty() {
        assertTrue(a1.addEvent(e1));
        assertTrue(a1.getEvents().contains(e1));
    }

    @Test
    public void testAddEventNonEmpty() {
        assertTrue(a2.addEvent(e2));
        assertTrue(a2.getEvents().contains(e2));
    }

    @Test
    public void testAddEventFalse() {
        assertFalse(a2.addEvent(e1));
        assertTrue(a2.getEvents().contains(e1));
    }

    @Test
    public void testEqualsTrue() {
        assertTrue(a1.equals(a3));
    }

    @Test
    public void testEqualsFalse() {
        assertFalse(a1.equals(a2));
    }

}