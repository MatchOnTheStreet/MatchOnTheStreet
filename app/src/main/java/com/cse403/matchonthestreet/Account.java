package com.cse403.matchonthestreet;

import java.util.List;

/**
 * Created by Iris on 2/7/16.
 */
public class Account {
    public String identifier;
    public String name;
    public List<Event> events;

    public Account(String identifier) {
        this.identifier = identifier;
        this.name = null;
        this.events = null;
    }
}
