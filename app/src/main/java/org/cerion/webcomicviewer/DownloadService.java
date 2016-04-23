package org.cerion.webcomicviewer;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.ResultReceiver;
import android.util.Log;

import com.einmalfel.earl.EarlParser;
import com.einmalfel.earl.Item;

import org.cerion.webcomicviewer.data.Database;
import org.cerion.webcomicviewer.data.Feed;
import org.cerion.webcomicviewer.data.Feeds;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.DataFormatException;

public class DownloadService extends IntentService {

    private static final String TAG = DownloadService.class.getSimpleName();

    private static final String ACTION_UPDATE_FEEDS = "org.cerion.webcomicviewer.action.UPDATE_FEEDS";
    private static final String RESULT_RECEIVER = "resultReceiver";

    private ResultReceiver mResultReceiver;

    //Thread Pool
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    //Mostly for internet requests, can be higher than cpu count
    private static final int NUMBER_OF_CORES = 5;//Runtime.getRuntime().availableProcessors();

    private final BlockingQueue<Runnable> mWorkQueue = new LinkedBlockingQueue<>();
    private final ThreadPoolExecutor mThreadPool = new ThreadPoolExecutor(
            NUMBER_OF_CORES, // Initial pool size
            NUMBER_OF_CORES, // Max pool size
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            mWorkQueue);

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

        //Multithreaded to speed up internet requests
        for(final Feed feed : Feeds.LIST) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    updateFromRSSFeed(feed);
                }
            };

            mThreadPool.execute(runnable);
        }

        while (mThreadPool.getTaskCount() != mThreadPool.getCompletedTaskCount()) {
            Log.d(TAG,"processing...");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mThreadPool.shutdown();
        Log.d(TAG,"Saving feeds");

        //Save data
        Database db = Database.getInstance(this);
        for(Feed feed : Feeds.LIST)
            db.save(feed);


        Date end = new Date();
        long diff = end.getTime() - start.getTime();
        Log.d(TAG,diff + "ms");

        Intent intent = new Intent();
        intent.setAction(UpdateBroadcastReceiver.UPDATE_ACTION);
        sendBroadcast(intent);
    }

    private void updateFromRSSFeed(Feed comic) {

        Log.d(TAG,"Updating " + comic.getFeedUrl());
        InputStream inputStream;
        com.einmalfel.earl.Feed feed = null;

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

            @SuppressWarnings("unchecked")
            List<Item> items = (List<Item>) feed.getItems();

            //Set fields based on items
            if (items.size() > 0) {
                //Url for most recent item
                comic.url = items.get(0).getLink();

                //Timestamp of most recent item
                if(items.get(0).getPublicationDate() != null)
                    comic.lastUpdated = items.get(0).getPublicationDate();

                if(comic.lastVisited == null)
                    count = items.size();
                else {
                    for (Item item : items) {
                        if (item.getPublicationDate() == null)
                            Log.e(TAG, "null date");
                        else if (item.getPublicationDate().getTime() > comic.lastVisited.getTime())
                            count++;
                    }
                }

                comic.updatedCount = count;
            }

        } else
            Log.e(TAG,"failed to get feed");
    }


}
