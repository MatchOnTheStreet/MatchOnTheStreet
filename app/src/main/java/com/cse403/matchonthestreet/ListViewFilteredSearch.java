package com.cse403.matchonthestreet;

import android.location.Location;

import java.util.*;

/**
 * Created by hopecrandall on 2/11/16.
 */
public class ListViewFilteredSearch {
    // Current search/filter result list
    private List<Event> currentResults;

    // Current Search
    private ListViewFilteredSearchData currentSearch;


    public List<Event> FilterAndSearch(ListViewFilteredSearchData newSearch) {
        int changeInSearch = currentSearch.compareTo(newSearch);

        if (searchIsTheSame(changeInSearch)) {
            return currentResults;
        }

        if (searchHasBeenNarrowed(changeInSearch)) {
            SearchWithinCurrentResults();
        } else {
            SearchInDatabase();
        }

        return currentResults;
    }

    /* iterate through current results and remove anything that does not
     * fit the new criteria*/
    private void SearchWithinCurrentResults() {
        for(Event e : currentResults) {
            if (!e.meetsFilterAndSearchCriteria(this.currentSearch)) {
                currentResults.remove(e);
            }
        }
    }

    private void SearchInDatabase() {
    }

    private boolean searchIsTheSame(int changeInSearch) {
        return changeInSearch == 0;
    }

    private boolean searchHasBeenNarrowed(int changeInSearch) {
        return changeInSearch == -1;
    }
}
