package org.cerion.webcomicviewer;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.ResultReceiver;
import android.util.Log;

import com.einmalfel.earl.EarlParser;
import com.einmalfel.earl.Feed;
import com.einmalfel.earl.Item;

import org.cerion.webcomicviewer.comics.Comic;
import org.cerion.webcomicviewer.comics.Feeds;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;

public class DownloadService extends IntentService {

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_UPDATE_FEEDS = "org.cerion.webcomicviewer.action.UPDATE_FEEDS";
    private static final String RESULT_RECEIVER = "resultReceiver";
    private static final String ACTION_BAZ = "org.cerion.webcomicviewer.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "org.cerion.webcomicviewer.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "org.cerion.webcomicviewer.extra.PARAM2";
    private static final String TAG = DownloadService.class.getSimpleName();

    ResultReceiver mResultReceiver;

    public DownloadService() {
        super("DownloadService");
    }


    public static void startActionUpdateFeeds(Context context, ResultReceiver receiver) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_UPDATE_FEEDS);
        intent.putExtra(RESULT_RECEIVER,receiver);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"onHandleIntent");
        if (intent != null) {

            if(intent.hasExtra(RESULT_RECEIVER))
                mResultReceiver = intent.getParcelableExtra(RESULT_RECEIVER);

            final String action = intent.getAction();
            if (ACTION_UPDATE_FEEDS.equals(action)) {
                handleActionUpdateFeeds();
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    @Override
    public void onDestroy() {
        mResultReceiver.send(0,null);
        super.onDestroy();
    }

    private void handleActionUpdateFeeds() {

        Date start = new Date();
        Database db = Database.getInstance(this);

        //TODO, make this multithreaded
        for(Comic comic : Feeds.LIST) {
            //Date lastVisited = db.getLastVisited(comic); //TODO, this should already be set in memory and unnecessary
            //if(lastVisited != null)
            //    Log.d(TAG,"visited = " + lastVisited.getTime());
            //comic.setLastVisited(lastVisited); //load this first
            updateFromRSSFeed(comic);

            db.save(comic);
        }

        Date end = new Date();
        long diff = end.getTime() - start.getTime();
        Log.d(TAG,diff + "ms");

        Intent intent = new Intent();
        intent.setAction(UpdateBroadcastReceiver.UPDATE_ACTION);
        sendBroadcast(intent);
    }

    private void updateFromRSSFeed(Comic comic) {

        Log.d(TAG,"Updating " + comic.getFeedUrl());
        InputStream inputStream;
        Feed feed = null;

        try {
            inputStream = new URL(comic.getFeedUrl()).openConnection().getInputStream();
            feed = EarlParser.parseOrThrow(inputStream, 0);
            Log.i(TAG, "Processing feed: '" + feed.getTitle() + "' " + feed.getItems().size() + " items");
        } catch (IOException |XmlPullParserException |DataFormatException e) {
            e.printStackTrace();
        }

        int count = 0;
        if(feed != null) {
            comic.title = feed.getTitle();
            List<Item> items = (List<Item>) feed.getItems();

            //Set fields based on items
            if(items != null) {

                if (items.size() > 0) {
                    //Url for most recent item
                    comic.setUrl( items.get(0).getLink() );

                    //Timestamp of most recent item
                    if(items.get(0).getPublicationDate() != null)
                        comic.setLastUpdated( items.get(0).getPublicationDate() );

                    if(comic.getLastVisited() == null)
                        count = items.size();
                    else {
                        for (Item item : items) {
                            if (item.getPublicationDate() == null)
                                Log.e(TAG, "null date");
                            else if (item.getPublicationDate().getTime() > comic.getLastVisited().getTime())
                                count++;
                        }
                    }

                    comic.setUpdatedCount(count);
                }

            }

        } else
            Log.e(TAG,"failed to get feed");
    }


    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
