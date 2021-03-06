package com.cse403.matchonthestreet.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iris on 2/7/16.
 *
 * Represents a user's account.
 */
public class  Account implements Serializable {

    /**
     * Representation Invariant:
     *      uid is in the database.
     *      name is the same name as the name assigned to uid in the database.
     *
     * Abstraction Function:
     *      Account a represents the account of a user with the facebook id a.uid, the full name
     *      given by a.name, who is attending the list of events given by a.events.
     */

    // User ID given by facebook.
    private int uid;

    // The name of the user whose account this is.
    private String name;

    // A list of events the user is planning on attending.
    private List<Event> events;

    public Account(int uid, String name) {
        this.uid = uid;
        this.name = name;
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
        return a.uid == uid && a.name.equals(name);
    }
}
