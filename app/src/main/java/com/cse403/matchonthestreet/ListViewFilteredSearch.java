package com.cse403.matchonthestreet;

import android.location.Location;

import java.util.*;

/**
 * Created by hopecrandall on 2/11/16.
 */
public class ListViewFilteredSearch {
    // Current search/filter result list
    private List<ListItem> currentSearch;

    // Current query String
    private String search;

    // Current Filters
    private Date startTime;
    private Date endTime;
    private Location centralLocation;
    private List<String> tags;


    public List<ListItem> FilterAndSearch(ListViewFilteredSearchViewModel newSearch) {

        /* search within current results if the only changed things are a narrowing of the
           time interval, the search string has changed, or tags have been removed */
        if (false) {
            SearchWithinCurrentResults();
        }

        SearchInDatabase();

        return currentSearch;
    }

    public void SearchWithinCurrentResults() {
    }

    public void SearchInDatabase() {
    }

    protected boolean isAfterStartTime(Event event) {
        return event.time.after(startTime) || event.time.equals(startTime);
    }

    protected boolean isBeforeEndTime(Event event) {
        return event.time.before(endTime);
    }

    protected boolean containsAllTags(Event event) {
        return true;
    }
}
