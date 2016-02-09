package com.cse403.matchonthestreet;

/**
 * Created by Hao on 2/8/2016.
 *
 * This class represents the list items.
 * Each of the item contains an event object.
 *
 */
public class ListItem {
    protected Event event;

    public ListItem(Event event) {
        this.event = event;
    }

    public String getTitle() {
        return event.title;
    }

    public String getDesc() {
        return event.getDescription();
    }
}
