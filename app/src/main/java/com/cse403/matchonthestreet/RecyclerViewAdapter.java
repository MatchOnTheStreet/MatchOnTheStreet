package com.cse403.matchonthestreet;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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

import java.util.ArrayList;
import java.util.List;

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
        viewHolder.txtDesc.setText(Event.getDesc());
        viewHolder.txtTitle.setText(Event.getTitle());

        // TODO: Here I hardcoded the image resource
        viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
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
            String filterString = constraint.toString().toLowerCase();

            FilterResults filterResults = new FilterResults();

            final List<Event> originalList = items;
            final List<Event> resultList = new ArrayList<>(originalList.size());

            for (Event item : originalList) {
                String title = item.getTitle().toLowerCase();
                String desc = item.getDesc().toLowerCase();

                if (title.contains(filterString) || desc.contains(filterString)) {
                    resultList.add(item);
                }
            }

            filterResults.values = resultList;
            filterResults.count = resultList.size();

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredItems = (List<Event>) results.values;
            notifyDataSetChanged();
        }
    }

}
