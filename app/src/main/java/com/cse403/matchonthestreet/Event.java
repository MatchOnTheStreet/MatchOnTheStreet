package com.cse403.matchonthestreet;

import java.util.*;

/**
 * Created by Iris on 2/7/16.
 */
public class Event {
    public String title;
    public Location location;
    public Date time;
    public String description;
    public List<Account> attending;

    public Event(String title, Location location, Date time, String description) {
        this.title = title;
        this.location = location;
        this.time = time;
        this.description = description;
        this.attending = null;
    }

}
