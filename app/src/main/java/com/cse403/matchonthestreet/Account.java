package com.cse403.matchonthestreet;

import java.util.List;

/**
 * Created by Iris on 2/7/16.
 *
 * Represents a user's account.
 */
public class Account {

    // User ID given by facebook.
    public String identifier;

    // The name of the user whose account this is.
    public String name;

    // A list of events the user is planning on attending.
    public List<Event> events;

    public Account(String identifier) {
        this.identifier = identifier;
        this.name = null;
        this.events = null;
    }
}
