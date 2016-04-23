package org.cerion.webcomicviewer.data;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.util.Date;


public class Feed {

    //Static content
    private final String mFeedUrl;

    //Dynamic content
    public String title;
    public Date lastUpdated;
    public Date lastVisited;
    public int updatedCount;
    public String url;


    public Feed(String feed) {
        mFeedUrl = feed;
    }

    public String getFeedUrl() {
        return mFeedUrl;
    }

    public String getRootUrl() {

        if(url != null) {
            int index = url.indexOf('/', 9); //start after http:// or https://

            if (index > 0)
                return url.substring(0,index);
        }

        return null;
    }

    //------------

    public void setVisited(Context context) {
        lastVisited = new Date();

        updatedCount = 0; //reset since all updates will be as of right now

        Database db = Database.getInstance(context);
        //db.setLastVisited(this);
        db.save(this);
    }


    public void openWebView(Context context) {
        if(url != null) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        } else
            Toast.makeText(context, "Unable to find page", Toast.LENGTH_SHORT).show();
    }



}
