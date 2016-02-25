package com.cse403.matchonthestreet;

import android.location.Location;

import com.cse403.matchonthestreet.models.Event;

import java.util.Date;
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Created by larioj on 2/25/16.
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
    public void compileTest() {
    }
}
