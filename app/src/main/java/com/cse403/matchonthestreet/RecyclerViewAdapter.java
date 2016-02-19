package com.cse403.matchonthestreet;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by Hao on 2/17/16.
 *
 */
public class RecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
        implements Filterable {

    private List<Event> items;
    private List<Event> filteredItems;

    private ListViewFilter filter = new ListViewFilter();

    public Context context;


    public RecyclerViewAdapter(Context context, List<Event> items) {
        this.items = items;
        this.filteredItems = items;
        this.context = context;
    }

    @Override
    public Filter getFilter() {
        return this.filter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int pos) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.list_item_layout, null
        );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int pos) {
        Event Event = filteredItems.get(pos);
        viewHolder.currentItem = Event;

        // Set the title and description of the listed item
        viewHolder.txtDesc.setText(Event.getDescription());
        viewHolder.txtTitle.setText(Event.getTitle());

        // Make an icon with the initial letter, colored randomly.
        Random rand = new Random();
        String firstLetter = "" + Event.getTitle().toUpperCase().charAt(0);
        int randomColor = Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
        TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter, randomColor);
        viewHolder.imageView.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return (filteredItems != null ? filteredItems.size() : 0);
    }

    public List<Event> getAllItems() {
        return new ArrayList<>(this.items);
    }

    public List<Event> getFilteredItems() {
        return new ArrayList<>(this.filteredItems);
    }

    /**
     * Custom ViewHolder class for the RecyclerView
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtDesc;
        Event currentItem;

        public ViewHolder(View v) {
            super(v);
            this.imageView = (ImageView) v.findViewById(R.id.icon);
            this.txtTitle = (TextView) v.findViewById(R.id.title);
            this.txtDesc = (TextView) v.findViewById(R.id.desc);
            v.setOnClickListener(new View.OnClickListener() {
                // TODO: dummy onClick
                @Override
                public void onClick(View v) {
                    String locStr = currentItem.location.toString();
                    Toast.makeText(v.getContext(), "Item selected, location: " + locStr, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public class ListViewFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // convert CharSequence constraint into the needed search parameters
            // return this.performFiltering(.....)
            return null;
        }


        private FilterResults performFiltering(String queryString, Date startTime, Date endTime,
                                               Location centralLocation, int searchRadius) {
            ListViewFilterAndSearch searcher
                    = new ListViewFilterAndSearch(queryString, startTime, endTime, centralLocation, searchRadius);

            List<Event> searchAndFilterResults = null;
            try {
                searchAndFilterResults = searcher.getFilterAndSearchResults();
            } catch (Exception e) {
                e.printStackTrace();
            }

            ListViewSorter sorter = new ListViewSorter(searchAndFilterResults);

            List<Event> finalResultsList = sorter.sortByDistance(centralLocation);

            FilterResults filterResults = new FilterResults();
            filterResults.values = finalResultsList;
            filterResults.count = finalResultsList.size();

            return filterResults;
        }


        /* @Override
                        protected FilterResults performFiltering(CharSequence constraint) {
                            String filterString = constraint.toString().toLowerCase();

                            FilterResults filterResults = new FilterResults();

                            final List<Event> originalList = items;
                            final List<Event> resultList = new ArrayList<>(originalList.size());

                            for (Event item : originalList) {
                                String title = item.getTitle().toLowerCase();
                                String desc = item.getDescription().toLowerCase();

                                if (title.contains(filterString) || desc.contains(filterString)) {
                                    resultList.add(item);
                                }
                            }

                            filterResults.values = resultList;
                            filterResults.count = resultList.size();

                            return filterResults;
                        }
                */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredItems = (List<Event>) results.values;
            notifyDataSetChanged();
        }
    }

}
