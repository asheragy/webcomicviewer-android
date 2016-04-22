package org.cerion.webcomicviewer.comics;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import org.cerion.webcomicviewer.Database;

import java.util.Date;


public class Comic {

    private static final String TAG = Comic.class.getSimpleName();

    //Static content
    private final String mFeedUrl;

    //Dynamic content
    public String title;
    private Date mLastUpdated;
    private Date mLastVisited;
    private int mCount;
    private String mUrl;


    public Comic(String feed) {
        mFeedUrl = feed;
    }

    public String getFeedUrl() {
        return mFeedUrl;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public int getUpdatedCount() {
        return mCount;
    }

    public void setUpdatedCount(int count) {
        mCount = count;
    }

    public Date getLastUpdated() {
        return mLastUpdated;
    }

    public void setLastUpdated(Date updated) {
        mLastUpdated = updated;
    }

    public void setLastVisited(Date date) {
        mLastVisited = date;
    }

    public Date getLastVisited() {
        return mLastVisited;
    }

    //------------

    public void setVisited(Context context) {
        mLastVisited = new Date();

        mCount = 0; //reset since all updates will be as of right now

        Database db = Database.getInstance(context);
        //db.setLastVisited(this);
        db.save(this);
    }


    public void openWebView(Context context) {
        if(getUrl() != null) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getUrl()));
            context.startActivity(i);
        } else
            Toast.makeText(context, "Unable to find page", Toast.LENGTH_SHORT).show();
    }



}
