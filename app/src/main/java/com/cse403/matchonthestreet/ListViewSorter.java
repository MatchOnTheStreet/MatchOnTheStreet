package com.cse403.matchonthestreet;

import android.location.Location;

import java.util.*;

/**
 * Created by hopecrandall on 2/11/16.
 */
public class ListViewSorter {
    public SortedSet<Event> sortByDistance(Set<Event> unsortedSearch, Location centralLocation) {
        SortedSet<Event> distanceSortedSearch = new TreeSet<Event>(new EventDistanceComparator(centralLocation));
        distanceSortedSearch.addAll(unsortedSearch);
        return distanceSortedSearch;
    }

    public SortedSet<Event> sortByTime(Set<Event> unsortedSearch) {
        SortedSet<Event> timeSortedSearch = new TreeSet<Event>(new EventTimeComparator());
        timeSortedSearch.addAll(unsortedSearch);
        return timeSortedSearch;
    }

    public SortedSet<Event> sortByRecentlyCreated(Set<Event> unsortedSearch) {
        SortedSet<Event> creationTimeSortedSearch = new TreeSet<Event>(new EventCreationTimeComparator());
        creationTimeSortedSearch.addAll(unsortedSearch);
        return creationTimeSortedSearch;
    }

    private class EventDistanceComparator implements Comparator<Event> {
        private Location centralLocation;

        public EventDistanceComparator(Location centralLocation) {
            this.centralLocation = centralLocation;
        }

        @Override
        public int compare(Event lhs, Event rhs) {
            float dist1 = lhs.location.distanceTo(centralLocation);
            float dist2 = rhs.location.distanceTo(centralLocation);

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
            return lhs.time.compareTo(rhs.time);
        }
    }

    private class EventCreationTimeComparator implements Comparator<Event> {

        @Override
        public int compare(Event lhs, Event rhs) {
            return lhs.timeStamp.compareTo(rhs.timeStamp);
        }
    }

}




