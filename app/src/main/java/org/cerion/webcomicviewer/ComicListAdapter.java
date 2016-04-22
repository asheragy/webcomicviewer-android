package org.cerion.webcomicviewer;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.cerion.webcomicviewer.comics.Comic;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ComicListAdapter extends RecyclerView.Adapter<ComicListAdapter.ViewHolder> {

    private static final String TAG = ComicListAdapter.class.getSimpleName();
    private final List<Comic> mData;
    private static final DateFormat mFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);

    public ComicListAdapter(List<Comic> data) {
        mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Comic comic = mData.get(position);
        holder.mId.setText(comic.title == null ? "" : comic.title);
        holder.mContent.setText(comic.getFeedUrl());
        holder.mUpdated.setText( formatDate(comic.getLastUpdated()) );

        if(comic.getUpdatedCount() > 0)
            holder.mCount.setText(comic.getUpdatedCount() + " updates");
        else
            holder.mCount.setText("");
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
        final TextView mId;
        final TextView mContent;
        final TextView mUpdated;
        final TextView mCount;

        public ViewHolder(View v) {
            super(v);
            mId =  (TextView) v.findViewById(R.id.id);
            mContent = (TextView) v.findViewById(R.id.content);
            mUpdated = (TextView) v.findViewById(R.id.updated);
            mCount = (TextView) v.findViewById(R.id.update_count);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Comic comic = mData.get(getLayoutPosition());

            Log.d(TAG, "open " + comic.title);
            comic.setVisited(v.getContext());
            notifyItemChanged(getLayoutPosition());

            comic.openWebView(v.getContext());
            //Intent intent = new Intent(v.getContext(),ComicViewActivity.class);
            //intent.putExtra(ComicViewActivity.EXTRA_COMIC_NAME, mData.get(pos).getName());
            //v.getContext().startActivity(intent);
        }
    }


}
