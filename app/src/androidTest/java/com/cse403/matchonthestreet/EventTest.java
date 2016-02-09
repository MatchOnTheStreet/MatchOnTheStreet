package com.cse403.matchonthestreet;

import android.location.Location;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * Test cases for Eveestnt Class
 */
public class EventTest extends TestCase {

    Event e;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Location location = new Location("");
        location.setLatitude(0.0d);
        location.setLongitude(0.0d);
        e = new Event("Basketball", location, new Date(100), "fun");
    }

    @Test
    public void testIsAttendingFalse() throws Exception {
        Account a = new Account("hello");
        assertFalse(e.isAttendedBy(a));
    }
}