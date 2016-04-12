package org.cerion.webcomicviewer.comics;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.einmalfel.earl.EarlParser;
import com.einmalfel.earl.Feed;
import com.einmalfel.earl.Item;


import org.cerion.webcomicviewer.Prefs;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.DataFormatException;

public class Comic {

    private static final String TAG = Comic.class.getSimpleName();
    private static final DateFormat mFormat = DateFormat.getDateInstance(DateFormat.SHORT,Locale.US);

    //Static content
    private final String mFeedUrl;

    //Dynamic content
    private String mTitle;
    private List<Item> mItems;
    private Date mLastVisited;

    public Comic(String feed) {
        mFeedUrl = feed;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getUrl() {
        if(mItems != null && mItems.size() > 0)
            return mItems.get(0).getLink();

        return null;
    }

    public String getRSS() {
        return mFeedUrl;
    }

    public String getUpdated() {
        if(mItems != null && mItems.size() > 0) {
            if(mItems.get(0).getPublicationDate() != null)
                return mFormat.format(mItems.get(0).getPublicationDate());
        }
        return "...";
    }

    public int getUpdatedCount() {

        if(mItems == null) {
            Log.d(TAG,"getUpdatedCount() null items");
            return 0;
        }

        if(mLastVisited == null) {
            Log.d(TAG,"getUpdatedCount() null last visit");
            return mItems.size();
        }

        int count = 0;
        for(Item item : mItems) {
            if(item.getPublicationDate() == null)
                Log.d(TAG,"null date on " + mTitle);
            else if(item.getPublicationDate().getTime() > mLastVisited.getTime())
                count++;
        }

        return count;
    }

    public void setVisited(Context context) {
        Prefs.updateLastVisit(context,getRSS());
        mLastVisited = new Date();
    }

    public void openWebView(Context context) {
        if(getUrl() != null) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getUrl()));
            context.startActivity(i);
        } else
            Toast.makeText(context, "Unable to find page", Toast.LENGTH_SHORT).show();
    }


    public void updateFromRSSFeed(Context context) {

        Log.d(TAG,"Updating " + getRSS());
        InputStream inputStream;
        Feed feed = null;

        try {
            inputStream = new URL(getRSS()).openConnection().getInputStream();
            feed = EarlParser.parseOrThrow(inputStream, 0);
            Log.i(TAG, "Processing feed: '" + feed.getTitle() + "' " + feed.getItems().size() + " items");
        } catch (IOException|XmlPullParserException|DataFormatException e) {
            e.printStackTrace();
        }

        if(feed != null) {
            this.mTitle = feed.getTitle();
            this.mLastVisited = Prefs.getLastVisit(context, mFeedUrl);
            this.mItems = (List<Item>) feed.getItems();
        } else
            Log.e(TAG,"failed to get feed");
    }



}
