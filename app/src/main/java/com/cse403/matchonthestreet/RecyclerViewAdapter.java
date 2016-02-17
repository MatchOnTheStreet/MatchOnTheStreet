package com.cse403.matchonthestreet;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by whale on 2/17/16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<ListItem> items;
    private Context context;

    public RecyclerViewAdapter(Context context, List<ListItem> items) {
        this.items = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int pos) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.list_item_layout, null
        );
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int pos) {
        ListItem listItem = items.get(pos);

        // Set the title and description of the listed item
        viewHolder.txtDesc.setText(listItem.getDesc());
        viewHolder.txtTitle.setText(listItem.getTitle());

        // TODO: Here I hardcoded the image resource
        viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public int getItemCount() {
        return (items != null ? items.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtDesc;

        public ViewHolder(View v) {
            super(v);
            this.imageView = (ImageView) v.findViewById(R.id.icon);
            this.txtTitle = (TextView) v.findViewById(R.id.title);
            this.txtDesc = (TextView) v.findViewById(R.id.desc);
        }
    }
}
