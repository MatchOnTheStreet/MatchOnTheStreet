package com.cse403.matchonthestreet.controller;

import android.location.Location;

import com.cse403.matchonthestreet.models.Event;

import java.util.*;

/**
 * Created by hopecrandall on 2/11/16.
 */
public class ListViewSorter {
    private List<Event> storedList;

    public ListViewSorter(List<Event> unsortedList) {
        this.storedList = unsortedList;
    }
    public List<Event> sortByDistance(Location centralLocation) {
        Collections.sort(storedList, new EventDistanceComparator(centralLocation));
        return storedList;
    }

    public List<Event> sortByTime() {
        Collections.sort(storedList, new EventTimeComparator());
        return storedList;
    }

    private class EventDistanceComparator implements Comparator<Event> {
        private Location centralLocation;

        public EventDistanceComparator(Location centralLocation) {
            this.centralLocation = centralLocation;
        }

        @Override
        public int compare(Event lhs, Event rhs) {
            float dist1 = lhs.getLocation().distanceTo(centralLocation);
            float dist2 = rhs.getLocation().distanceTo(centralLocation);

            if(dist1 > dist2) {
                return 1;
            } else if (dist1 < dist2) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private class EventTimeComparator implements Comparator<Event> {

        @Override
        public int compare(Event lhs, Event rhs) {
            return lhs.getTime().compareTo(rhs.getTime());
        }
    }
}
