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
    private Location location;

    // The time of the event
    private Date time;

    // A description of the event.
    private String description;

    // A list of accounts who have said they will be attending the event.
    private List<Account> attending;

    public Event(String title, Location location, Date time, String description) {
        this.title = title;
        this.location = location;
        this.time = time;
        this.description = description;
        this.attending = null;
    }

    public boolean isAttendedBy(Account account) {
        return true;
    }
}
