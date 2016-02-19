package com.cse403.matchonthestreet;

import android.location.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by hopecrandall on 2/18/16.
 */
public class ListViewFilterAndSearch {

    // Database access
    private DBManager database;

    // List of query strings
    protected String queryString;

    // List of filters
    protected Date startTime;
    protected Date endTime;
    protected Location centralLocation;
    protected int searchRadius; // in miles


    public ListViewFilterAndSearch
            (String queryString, Date startTime, Date endTime, Location centralLocation, int searchRadius) {
        this.database = new DBManager();
        this.queryString = queryString;
        this.centralLocation = centralLocation;
        this.searchRadius = searchRadius;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public List<Event> getFilterAndSearchResults() throws Exception {
        List<Event> results = database.transaction_getEvent(centralLocation, searchRadius);
        for(Event e: results) {
            if(DoesNotMeetCriteria(e)) {
                results.remove(e);
            }
        }
        return results;
    }

    public boolean DoesNotMeetCriteria(Event e) {
        return !e.isAfter(startTime)
                || !e.isBefore(endTime)
                || !e.containsString(queryString);
    }
}
