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

    public boolean isAfter(Date time) {
        return this.time.after(time) || this.time.equals(time);
    }

    public boolean isBefore(Date time) {
        return this.time.before(time);
    }

    public boolean isCloser(Location centralLocation, Event e) {
        return true;
    }

    public boolean containsAllTags(List<String> tags) {
        return true;
    }

    public boolean containsString(String s) {
        return true;
    }

    public boolean meetsFilterAndSearchCriteria(ListViewFilteredSearchData currentSearch) {
        return this.isAfter(currentSearch.startTime)
                && this.isBefore(currentSearch.endTime)
                && this.containsAllTags(currentSearch.tags)
                && this.containsString(currentSearch.queryString);
    }
}
