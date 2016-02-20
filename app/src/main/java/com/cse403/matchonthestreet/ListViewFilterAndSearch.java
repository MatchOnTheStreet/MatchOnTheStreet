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
    protected List<String> queryStrings;

    // List of filters
    protected Date startTime;
    protected Date endTime;
    protected Location centralLocation;
    protected int searchRadius; // in miles


    public ListViewFilterAndSearch
            (String queryString, Date startTime, Date endTime, Location centralLocation, int searchRadius) {
       // this.database = new DBManager();
        this.queryStrings = SplitQueryString(queryString);
        this.centralLocation = centralLocation;
        this.searchRadius = searchRadius;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public List<Event> getFilterAndSearchResults() throws Exception {
    /*    List<Event> results = database.transaction_getEventByRadius(centralLocation, searchRadius);
        for(Event e: results) {
            if(DoesNotMeetCriteria(e)) {
                results.remove(e);
            }
        }
        return results;*/
        return null;
    }

    public boolean DoesNotMeetCriteria(Event e) {
        if (startTime != null && !e.isAfter(startTime)) {
            return true;
        }

        if (endTime != null && !e.isBefore(endTime)) {
            return true;
        }

        if (queryStrings.size() > 1 && !e.containsStrings(queryStrings)) {
            return true;
        }

        return false;
    }

    private List<String> SplitQueryString(String queryStrings) {
        String[] queries = queryStrings.split(" ");
        return new ArrayList<String>(Arrays.asList(queries));
    }
}
