package com.cse403.matchonthestreet;

import android.location.Location;

import com.cse403.matchonthestreet.backend.DBManager;
import com.cse403.matchonthestreet.models.Account;
import com.cse403.matchonthestreet.models.Event;

import junit.framework.TestCase;

import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

/**
 * Created by larioj on 2/25/16.
 *
 * Every test name has to start with test!!
 * It is some bull.
 *
 */
public class DBManagerTest extends TestCase {
    private Event makeRandomEvent() {
        Random rand = new Random(System.currentTimeMillis());
        String title = "randomEvent" + rand.nextInt();
        Location loc = new Location("randloc");
        loc.setLatitude(rand.nextInt(180) - 89);
        loc.setLongitude(rand.nextInt(360) - 179);
        Date date = new Date();
        int duration = rand.nextInt(240) + 1;
        Date timeCreated = new Date();
        String description = "This is a random event created for the purpose of testing."
                + rand.nextInt();
        return new Event(title, loc, date, duration, timeCreated, description);
    }

    @Test
    public void testAddGetEvent() throws SQLException, ClassNotFoundException {
        Event e = makeRandomEvent();
        DBManager.addEvent(e);
        Event e2 = DBManager.getEventById(e.eid);
        assertEquals(e.eid, e2.eid);
        assertEquals(e.title, e2.title);
        assertEquals(e.duration, e2.duration);
        assertEquals(e.description, e2.description);
    }
}
