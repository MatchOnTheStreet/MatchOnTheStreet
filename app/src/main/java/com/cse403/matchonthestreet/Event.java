package com.cse403.matchonthestreet;

import android.location.Location;

import java.util.*;

/**
 * Created by Iris on 2/7/16.
 *
 * Represents a sporting Event.
 */
public class Event {
    // The title of the event
    public String title;

    // Where the event is held.
    protected Location location;

    // The time of the event
    protected Date time;

    // A description of the event.
    protected String description;

    // A list of accounts who have said they will be attending the event.
    protected List<Account> attending;

    public Event(String title, Location location, Date time, String description) {
        this.title = title;
        this.location = location;
        this.time = time;
        this.description = description;
        this.attending = null;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isAttendedBy(Account account) {
        return true;
    }
}
