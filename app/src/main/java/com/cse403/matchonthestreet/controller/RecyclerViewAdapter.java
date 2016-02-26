package com.cse403.matchonthestreet.controller;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cse403.matchonthestreet.view.MapsActivity;
import com.cse403.matchonthestreet.R;
import com.cse403.matchonthestreet.models.Event;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by Hao on 2/17/16.
 *
 * The RecyclerViewAdapter represents the list of events to be displayed
 * in the list view. It assigns necessary attributes to these events.
 */
public class RecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
        implements Filterable {

    /**
     * The list of all items (before filtering)
     */
    private List<Event> items;

    /**
     * The list of items after filtering.
     */
    private List<Event> filteredItems;

    /**
     * The filter used on the events.
     */
    private ListViewFilter filter;

    /**
     * The current context of the adapter. Normally would be the
     * ListViewActivity class.
     */
    public Context context;

    /**
     * The constructor of RecyclerViewAdapter
     *
     * @param context The current context of the adapter
     * @param items   List of events to be put into the adapter
     */
    public RecyclerViewAdapter(Context context, List<Event> items) {
        this.items = items;
        this.filteredItems = items;
        this.context = context;
        this.filter = new ListViewFilter(context);
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
        Event event = filteredItems.get(pos);
        viewHolder.currentItem = event;

        // Set the title and description of the listed item
        viewHolder.txtDesc.setText(event.getDescription());
        viewHolder.txtTitle.setText(event.getTitle());
        viewHolder.txtDate.setText(new SimpleDateFormat("EEE, MM/dd, yy", Locale.US).format(event.time));

        Drawable drawable = SportsIconFinder.getInstance().matchString(context, event.getTitle().toLowerCase());
        if (drawable != null) {
            drawable.setColorFilter(
                    Color.rgb(event.title.hashCode() % 255,
                            event.description.hashCode() % 255,
                            event.time.hashCode() % 255),
                    PorterDuff.Mode.MULTIPLY
            );
        }

        if (drawable == null) {
            // Make an icon with the initial letter, colored randomly.
            Random rand = new Random();
            String firstLetter = "" + event.getTitle().toUpperCase().charAt(0);
            int randomColor = Color.rgb(20 + rand.nextInt(200), 20 + rand.nextInt(220), 20 + rand.nextInt(220));
            drawable = TextDrawable.builder().buildRound(firstLetter, randomColor);
        }

        viewHolder.imageView.setImageDrawable(drawable);
        viewHolder.imageView.invalidateDrawable(drawable);

    }

    @Override
    public int getItemCount() {
        return (filteredItems != null ? filteredItems.size() : 0);
    }

    /**
     * Returns all events
     *
     * @return all events (Before filtering).
     */
    public List<Event> getAllItems() {
        return new ArrayList<>(this.items);
    }

    /**
     * Returns the filtered results
     *
     * @return the filtered results.
     */
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
        ImageView imageDate;
        TextView txtDate;
        Event currentItem;

        public ViewHolder(View v) {
            super(v);
            this.imageView = (ImageView) v.findViewById(R.id.icon);
            this.txtTitle = (TextView) v.findViewById(R.id.title);
            this.txtDesc = (TextView) v.findViewById(R.id.desc);
            this.txtDate = (TextView) v.findViewById(R.id.item_date);
            v.setOnClickListener(new View.OnClickListener() {
                // TODO: dummy onClick
                @Override
                public void onClick(View v) {
                    Intent showOnMapIntent = new Intent(context, MapsActivity.class);
                    showOnMapIntent.putExtra(context.toString() + ".VIEW_EVENT", currentItem.eid);
                    showOnMapIntent.putExtra("fromListItem", true);
                    showOnMapIntent.putExtra("selectedEid", currentItem.eid);
                    showOnMapIntent.putExtra("selectedEvent", currentItem);
                    showOnMapIntent.putExtra("selectedEventLocation", currentItem.location);
                    Log.d("Recycler", "passing eid: " + currentItem.eid);
                    context.startActivity(showOnMapIntent);
                }
            });
        }
    }

    public class ListViewFilter extends Filter {

        private Context context;
        private ViewController viewController;

        public ListViewFilter(Context context) {
            this.context = context;
            this.viewController = ((MOTSApp)context.getApplicationContext()).getViewController();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // convert CharSequence constraint into the needed search parameters
            // return this.performFiltering(.....)
            String[] queryTokens = constraint.toString().toLowerCase().split(Pattern.quote("::"), -1);
            String keyword = queryTokens[0];
            String fromDateStr = queryTokens[1];
            String toDateStr = queryTokens[2];
            String radiusStr = queryTokens[3];
            String latLongStr = queryTokens[4];

            for (String q : queryTokens) {
                System.out.println("Query tokens: " + q);
            }
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy", Locale.US);
            Date fromDate = null;
            Date toDate = null;
            if (!fromDateStr.isEmpty() && !toDateStr.isEmpty()) {
                try {
                    fromDate = format.parse(fromDateStr);
                    toDate = format.parse(toDateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            int radius = -1;
            double lat = Double.NaN, lon = Double.NaN;
            if (!radiusStr.isEmpty() && !latLongStr.isEmpty()) {
                radius = Integer.parseInt(radiusStr);
                String[] latLong = latLongStr.split(Pattern.quote(">$<"));
                lat = Double.parseDouble(latLong[0]);
                lon = Double.parseDouble(latLong[1]);
            }

            FilterResults filterResults = new FilterResults();

            final List<Event> originalList = items;
            List<Event> resultList = new ArrayList<>(originalList.size());

            for (Event item : originalList) {
                String title = item.getTitle().toLowerCase();
                String desc = item.getDescription().toLowerCase();
                Date eventDate = item.time;

                boolean matchesKeyword = title.contains(keyword) || desc.contains(keyword);
                boolean withinDate = (fromDate == null || toDate == null) ||
                        (eventDate.before(fromDate) && eventDate.after(toDate));
                boolean withinRadius = true;
                if (!(radius < 0 || Double.isNaN(lat) || Double.isNaN(lon))) {
                    Location userLocation = new Location("");
                    userLocation.setLatitude(lat);
                    userLocation.setLongitude(lon);
                    float distance = item.location.distanceTo(userLocation);
                    withinRadius = distance <= (radius * 1000); // Convert to meters
                }

                if (matchesKeyword && withinDate && withinRadius) {
                    resultList.add(item);
                }
            }

            filterResults.values = resultList;
            filterResults.count = resultList.size();

            return filterResults;
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
            viewController.updateEventList(new HashSet<>(getFilteredItems()));
            notifyDataSetChanged();
        }
    }

    public class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private Drawable mDivider;
        private boolean mShowFirstDivider = false;
        private boolean mShowLastDivider = false;


        public DividerItemDecoration(Context context, AttributeSet attrs) {
            final TypedArray a = context
                    .obtainStyledAttributes(attrs, new int[]{android.R.attr.listDivider});
            mDivider = a.getDrawable(0);
            a.recycle();
        }

        public DividerItemDecoration(Context context, AttributeSet attrs, boolean showFirstDivider,
                                     boolean showLastDivider) {
            this(context, attrs);
            mShowFirstDivider = showFirstDivider;
            mShowLastDivider = showLastDivider;
        }

        public DividerItemDecoration(Drawable divider) {
            mDivider = divider;
        }

        public DividerItemDecoration(Drawable divider, boolean showFirstDivider,
                                     boolean showLastDivider) {
            this(divider);
            mShowFirstDivider = showFirstDivider;
            mShowLastDivider = showLastDivider;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (mDivider == null) {
                return;
            }
            if (parent.getChildPosition(view) < 1) {
                return;
            }

            if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
                outRect.top = mDivider.getIntrinsicHeight();
            } else {
                outRect.left = mDivider.getIntrinsicWidth();
            }
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            if (mDivider == null) {
                super.onDrawOver(c, parent, state);
                return;
            }

            // Initialization needed to avoid compiler warning
            int left = 0, right = 0, top = 0, bottom = 0, size;
            int orientation = getOrientation(parent);
            int childCount = parent.getChildCount();

            if (orientation == LinearLayoutManager.VERTICAL) {
                size = mDivider.getIntrinsicHeight();
                left = parent.getPaddingLeft();
                right = parent.getWidth() - parent.getPaddingRight();
            } else { //horizontal
                size = mDivider.getIntrinsicWidth();
                top = parent.getPaddingTop();
                bottom = parent.getHeight() - parent.getPaddingBottom();
            }

            for (int i = mShowFirstDivider ? 0 : 1; i < childCount; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                if (orientation == LinearLayoutManager.VERTICAL) {
                    top = child.getTop() - params.topMargin;
                    bottom = top + size;
                } else { //horizontal
                    left = child.getLeft() - params.leftMargin;
                    right = left + size;
                }
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }

            // show last divider
            if (mShowLastDivider && childCount > 0) {
                View child = parent.getChildAt(childCount - 1);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                if (orientation == LinearLayoutManager.VERTICAL) {
                    top = child.getBottom() + params.bottomMargin;
                    bottom = top + size;
                } else { // horizontal
                    left = child.getRight() + params.rightMargin;
                    right = left + size;
                }
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        private int getOrientation(RecyclerView parent) {
            if (parent.getLayoutManager() instanceof LinearLayoutManager) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
                return layoutManager.getOrientation();
            } else {
                throw new IllegalStateException(
                        "DividerItemDecoration can only be used with a LinearLayoutManager.");
            }
        }

    }
}
