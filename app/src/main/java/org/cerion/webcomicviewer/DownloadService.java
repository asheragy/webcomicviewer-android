package org.cerion.webcomicviewer;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.ResultReceiver;

import org.cerion.webcomicviewer.comics.Comic;
import org.cerion.webcomicviewer.comics.Feeds;

public class DownloadService extends IntentService {

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_UPDATE_FEEDS = "org.cerion.webcomicviewer.action.UPDATE_FEEDS";
    private static final String RESULT_RECEIVER = "resultReceiver";
    private static final String ACTION_BAZ = "org.cerion.webcomicviewer.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "org.cerion.webcomicviewer.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "org.cerion.webcomicviewer.extra.PARAM2";

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

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
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

        for(Comic comic : Feeds.LIST) {
            comic.updateFromRSSFeed(this);
        }

        Intent intent = new Intent();
        intent.setAction(UpdateBroadcastReceiver.UPDATE_ACTION);
        sendBroadcast(intent);
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
