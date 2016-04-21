package org.cerion.webcomicviewer.comics;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.einmalfel.earl.EarlParser;
import com.einmalfel.earl.Feed;
import com.einmalfel.earl.Item;


import org.cerion.webcomicviewer.Database;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.DataFormatException;

public class Comic {

    private static final String TAG = Comic.class.getSimpleName();
    private static final DateFormat mFormat = DateFormat.getDateInstance(DateFormat.SHORT,Locale.US);

    //Static content
    private final String mFeedUrl;

    //Dynamic content
    private String mTitle;
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

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
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

    //TODO move this to where its used
    public String getUpdated() {
        if(mLastUpdated != null)
            return mFormat.format(mLastUpdated);

        return "...";
    }

    public void setLastUpdated(Date updated) {
        mLastUpdated = updated;
    }


    //------------

    public void setVisited(Context context) {
        mLastVisited = new Date();

        mCount = 0; //reset since all updates will be as of right now

        Database db = Database.getInstance(context);
        //db.setLastVisited(this);
        db.save(this);
    }

    public void setLastVisited(Date date) {
        mLastVisited = date;
    }

    public Date getLastVisited() {
        return mLastVisited;
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
