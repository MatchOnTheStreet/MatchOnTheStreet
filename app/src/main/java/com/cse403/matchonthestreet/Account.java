package com.cse403.matchonthestreet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iris on 2/7/16.
 *
 * Represents a user's account.
 */
public class Account {

    // User ID given by facebook.
    private int uid;

    // The name of the user whose account this is.
    private String name;

    // A list of events the user is planning on attending.
    private List<Event> events;

    public Account(int uid) {
        this.uid = uid;
        events = new ArrayList<Event>();
    }

    /**
     * Add an event to list of events this account is planning on attending.
     *
     * @param event The event to add
     * @return true if the event was added, false otherwise. The event will not
     * be added if it was already in the list of events the account was planning
     * on attending.
     */
    public boolean addEvent(Event event) {
        if (!events.contains(event)) {
            events.add(event);
            return true;
        }

        return false;
    }

    /**
     * Get a deep copy of the events this account is attending.
     *
     * @return a deep copy of all the events this account is attending
     */
    public List<Event> getEvents() {
        List<Event> copy = new ArrayList<Event>();
        for (Event e : events) {
            // TODO: Make this a deeper copy
            copy.add(e);
        }
        return copy;
    }

    /**
     * Get the unique id associated with this account
     *
     * @return the unique id of this account
     */
    public int getUid() { return uid; }

    /**
     * Get the name of this account
     *
     * @return the name of this account
     */
    public String getName() {
        return name;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Account)) {
            return false;
        }

        Account a = (Account) o;
        return uid == uid;
    }
}
