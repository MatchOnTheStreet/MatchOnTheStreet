package com.cse403.matchonthestreet;

import android.location.Location;

import java.util.*;

/**
 * Created by hopecrandall on 2/11/16.
 */
public class ListViewFilteredSearchViewModel {
    // New query String
    private String search;

    // New Filters
    private Date startTime;
    private Date endTime;
    private Location centralLocation;
    private List<String> tags;

    public ListViewFilteredSearchViewModel
            (String search, Date startTime, Date endTime, Location centralLocation, List<String> tags) {
        this.search = search;
        this.startTime = startTime;
        this.endTime = endTime;
        this.centralLocation = centralLocation;
        this.tags = tags;
    }
}
