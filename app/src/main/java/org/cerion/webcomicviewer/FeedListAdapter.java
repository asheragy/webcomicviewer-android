package org.cerion.webcomicviewer;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.cerion.webcomicviewer.data.Feed;
import org.cerion.webcomicviewer.data.Feeds;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.ViewHolder> {

    private static final String TAG = FeedListAdapter.class.getSimpleName();
    private final List<Feed> mData;
    private static final DateFormat mFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);

    public FeedListAdapter() {
        mData = Feeds.LIST;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Feed feed = mData.get(position);
        holder.id.setText(feed.title == null ? "" : feed.title);
        holder.content.setText(feed.getRootUrl() == null ? "" : feed.getRootUrl());
        holder.updated.setText( formatDate(feed.lastUpdated) );

        if(feed.updatedCount > 0)
            holder.count.setText(feed.updatedCount + " updates");
        else
            holder.count.setText("");
    }

    private String formatDate(Date date) {
        if(date != null)
            return mFormat.format(date);

        return "...";
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }



    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView id;
        final TextView content;
        final TextView updated;
        final TextView count;

        public ViewHolder(View v) {
            super(v);
            id =  (TextView) v.findViewById(R.id.id);
            content = (TextView) v.findViewById(R.id.content);
            updated = (TextView) v.findViewById(R.id.updated);
            count = (TextView) v.findViewById(R.id.update_count);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Feed feed = mData.get(getLayoutPosition());

            Log.d(TAG, "open " + feed.title);
            feed.setVisited(v.getContext());
            notifyItemChanged(getLayoutPosition());

            feed.openWebView(v.getContext());
            //Intent intent = new Intent(v.getContext(),ComicViewActivity.class);
            //intent.putExtra(ComicViewActivity.EXTRA_COMIC_NAME, mData.get(pos).getName());
            //v.getContext().startActivity(intent);
        }
    }


}
