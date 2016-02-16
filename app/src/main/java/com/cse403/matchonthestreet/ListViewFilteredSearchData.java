package com.cse403.matchonthestreet;

import android.location.Location;

import java.util.*;

/**
 * Created by hopecrandall on 2/11/16.
 */
public class ListViewFilteredSearchData implements Comparable<ListViewFilteredSearchData>{
    // New query String
    protected String queryString;

    // New Filters
    protected Date startTime;
    protected Date endTime;
    protected Location centralLocation;
    protected List<String> tags;

    public ListViewFilteredSearchData
            (String queryString, Date startTime, Date endTime, Location centralLocation, List<String> tags) {
        this.queryString = queryString;
        this.startTime = startTime;
        this.endTime = endTime;
        this.centralLocation = centralLocation;
        this.tags = tags;
    }

    // 0 if equal
    // -1 if the search has been narrowed
    // 1 if the search has been widened
    public int compareTo(ListViewFilteredSearchData search) {
        int timeChange = this.compareTimeInterval(search);
        int tagChange = this.compareTags(search);
        int queryStringChange = this.compareQueryString(search);

        if (timeChange == 0 && tagChange == 0 && queryStringChange == 0) {
            return 0;
        } else if (timeChange == 1 || tagChange == 1 || queryStringChange == 1) {
            return 1;
        } else {
            return -1;
        }
    }

    private int compareTimeInterval(ListViewFilteredSearchData search) {
        return 0;
    }

    private int compareTags(ListViewFilteredSearchData search) {
        return 0;
    }

    private int compareQueryString(ListViewFilteredSearchData search) {
        return 0;
    }

}
