package org.cerion.webcomicviewer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.cerion.webcomicviewer.comics.Feeds;


public class ComicListFragment extends Fragment {

    private static final String TAG = ComicListFragment.class.getSimpleName();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ComicListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ComicListFragment() {
    }

    public static ComicListFragment newInstance() {
        return new ComicListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_list, container, false);

        //Set recycler view
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mAdapter = new ComicListAdapter(Feeds.LIST);
        recyclerView.setAdapter(mAdapter);

        //Set swipe refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

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
        }
    }

    private final MyResultReceiver mResultReceiver = new MyResultReceiver(null);


    private void refreshList() {
        DownloadService.startActionUpdateFeeds(getContext(),mResultReceiver);
    }


}
