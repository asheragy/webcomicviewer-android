package org.cerion.webcomicviewer;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.cerion.webcomicviewer.data.Feeds;

import java.util.Date;


public class FeedListFragment extends Fragment {

    private static final String TAG = FeedListFragment.class.getSimpleName();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FeedListAdapter mAdapter;
    private static final String PREF_LAST_UPDATED = "lastUpdated";
    private static SharedPreferences mPrefs;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FeedListFragment() {
    }

    public static FeedListFragment newInstance() {
        return new FeedListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_list, container, false);

        //Set recycler view
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mAdapter = new FeedListAdapter();
        recyclerView.setAdapter(mAdapter);

        //Set swipe refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Date lastUpdated = new Date(mPrefs.getLong(PREF_LAST_UPDATED,0));

        //refresh if last update was more than 30 minutes ago
        if(System.currentTimeMillis() - lastUpdated.getTime() > 1000*60*30) {
            Log.d(TAG,"running auto update");
            mSwipeRefreshLayout.post(new Runnable() {
                @Override public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                    refreshList();
                }
            });
        }


        return view;
    }


    @SuppressLint("ParcelCreator")
    private class MyResultReceiver extends ResultReceiver {

        public MyResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.d(TAG, "onReceive");
            mSwipeRefreshLayout.setRefreshing(false);
            mAdapter.notifyDataSetChanged();

            mPrefs.edit().putLong(PREF_LAST_UPDATED, new Date().getTime()).apply();
        }
    }

    private final MyResultReceiver mResultReceiver = new MyResultReceiver(null);


    private void refreshList() {
        DownloadService.startActionUpdateFeeds(getContext(),mResultReceiver);
    }


}
