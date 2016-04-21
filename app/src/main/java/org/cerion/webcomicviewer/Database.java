package org.cerion.webcomicviewer;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.cerion.webcomicviewer.comics.Comic;
import org.cerion.webcomicviewer.comics.Feeds;

import java.util.Date;

public class Database extends SQLiteOpenHelper {

    private static final String TAG = "Database";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "data.db";

    //Singleton class
    private static Database mInstance;
    private Database(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public synchronized static Database getInstance(Context context)
    {
        if(mInstance == null)
            mInstance = new Database(context.getApplicationContext());

        return mInstance;
    }

    private static final String TABLE = "feeds";
    private static final String _FEED_URL = "feed";
    private static final String _TITLE = "title";
    private static final String _UPDATED = "updated";
    private static final String _VISITED = "visited";
    private static final String _COUNT = "count";
    private static final String _URL = "url";

    private static final String SQL_CREATE = "create table " + TABLE + "("
            + _FEED_URL + " TEXT PRIMARY KEY NOT NULL, "
            + _TITLE + " TEXT, "
            + _UPDATED + " INTEGER, "
            + _VISITED + " INTEGER NOT NULL DEFAULT 0, "
            + _COUNT + " INTEGER, "
            + _URL + " TEXT"
            + ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
        reset();
    }

    public void reset() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    /**
     * Add or update existing feed
     * @param comic
     */
    public void save(Comic comic) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(_FEED_URL, comic.getFeedUrl());
        values.put(_TITLE, comic.getTitle());
        values.put(_UPDATED, comic.getLastUpdated().getTime());
        values.put(_COUNT, comic.getUpdatedCount());
        values.put(_URL, comic.getUrl());
        values.put(_VISITED, comic.getLastVisited().getTime());

        Log.d(TAG,"saving " + comic.getFeedUrl() + " with " + values.toString());

        db.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    /*
    public void setVisited(Comic comic) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String where = String.format("%s='%s'", _FEED_URL, comic.getFeedUrl());
        values.put(_VISITED, new Date().getTime());
        values.put(_COUNT, 0);

        //Log.d(TAG,"values = " + values.toString());
        db.update(TABLE,values,where,null);
        db.close();
    }
    */

    /*
    public Date getLastVisited(Comic comic) {
        SQLiteDatabase db = this.getReadableDatabase();

        Date result = null;
        String query = String.format("SELECT %s FROM %s WHERE %s='%s'",_VISITED, TABLE, _FEED_URL, comic.getFeedUrl());
        Cursor c = db.rawQuery(query, null);

        if(c != null) {
            if (c.moveToFirst()) {
                result = new Date(c.getLong(0));
            }
            c.close();
        }

        db.close();
        return result;
    }
    */

    public void loadCachedFeeds() {

        SQLiteDatabase db = this.getWritableDatabase();

        String[] columns = { _TITLE, _URL, _UPDATED, _COUNT, _VISITED };

        for(Comic feed : Feeds.LIST) {
            String selection = String.format("%s='%s'", _FEED_URL, feed.getFeedUrl());
            Cursor c = db.query(TABLE, columns,selection,null,null,null,null);

            if(c != null) {
                if (c.moveToFirst()) {
                    feed.setTitle(c.getString(0));
                    feed.setUrl(c.getString(1));
                    feed.setLastUpdated(new Date(c.getLong(2)));
                    feed.setUpdatedCount(c.getInt(3));
                    feed.setLastVisited(new Date(c.getLong(4)));

                    Log.d(TAG, "lastVisited = " + c.getLong(4));
                }
                c.close();
            }

        }

        db.close();
    }


    /*
    public void deleteFeed(String feedUrl) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(_FEED_URL, feedUrl);

        int result = db.delete(TABLE,_FEED_URL + "=?", new String[] {feedUrl});
        db.close();
    }
    */

    public void log()
    {
        Log.d(TAG,"Table: " + TABLE);
        SQLiteDatabase db = getReadableDatabase();

        String query = String.format("SELECT * FROM %s", TABLE);
        Cursor c = db.rawQuery(query, null);

        if(c != null) {

            String[] columns = c.getColumnNames();
            while (c.moveToNext()) {

                String line = "";
                for(String col : columns) {
                    line += col + "=" + c.getString(c.getColumnIndexOrThrow(col)) + ", ";
                }

                //String feed = c.getString(c.getColumnIndexOrThrow(_FEED_URL));
                //String count = c.getString(c.getColumnIndexOrThrow(_COUNT));

                Log.d(TAG,line);
            }
            c.close();
        }

        db.close();
    }
}
