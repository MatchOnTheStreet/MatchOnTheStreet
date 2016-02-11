package com.cse403.matchonthestreet;

import android.location.Location;

import java.util.*;

/**
 * Created by hopecrandall on 2/11/16.
 */
public class ListViewFilteredSearchData implements Comparable<ListViewFilteredSearchData>{
    // New query String
    protected String search;

    // New Filters
    protected Date startTime;
    protected Date endTime;
    protected Location centralLocation;
    protected List<String> tags;

    public ListViewFilteredSearchData
            (String search, Date startTime, Date endTime, Location centralLocation, List<String> tags) {
        this.search = search;
        this.startTime = startTime;
        this.endTime = endTime;
        this.centralLocation = centralLocation;
        this.tags = tags;
    }

    // 0 if equal
    // -1 if the search has been narrowed
    // 1 if the search has been widened
    public int compareTo(ListViewFilteredSearchData search) {
      return 0;
    }

    private int compareTimeInterval(ListViewFilteredSearchData e1, ListViewFilteredSearchData e2) {
        return 0;
    }

    private int compareTags(ListViewFilteredSearchData e1, ListViewFilteredSearchData e2) {
        return 0;
    }

    private int compareQueryString(ListViewFilteredSearchData e1, ListViewFilteredSearchData e2) {
        return 0;
    }
}
