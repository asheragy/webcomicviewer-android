package org.cerion.webcomicviewer;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;
import java.util.Set;

public class Prefs {

    private static final String FEED_UPDATED_PREFIX = "updated_";
    private static final String TAG = Prefs.class.getSimpleName();

    public static void updateLastVisit(Context context, String feed) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(FEED_UPDATED_PREFIX + feed, new Date().getTime());
        editor.apply();
    }

    public static Date getLastVisit(Context context, String feed) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long updated = prefs.getLong(FEED_UPDATED_PREFIX + feed, 0);

        return new Date(updated);
    }

    public static void clearLastUpdates(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> map = prefs.getAll().keySet();

        for(String key : map)
            if(key.contains(FEED_UPDATED_PREFIX))
                editor.remove(key);

        editor.apply();
    }
}
