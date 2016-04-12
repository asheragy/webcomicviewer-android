package org.cerion.webcomicviewer.comics;

import java.util.ArrayList;
import java.util.List;

public class Feeds {

    private static final String[] mRSSFeeds = {
            "http://explosm-feed.antonymale.co.uk/feed",
            "http://feeds.feedburner.com/oatmealfeed",
            "http://pbfcomics.com/feed/feed.xml",
            "http://www.smbc-comics.com/rss.php",
            "http://www.xkcd.com/rss.xml"
    };

    public static final List<Comic> LIST = getList();

    private static List<Comic> getList() {
        List<Comic> result = new ArrayList<>();

        for(String feed : mRSSFeeds)
            result.add(new Comic(feed));

        return result;
    }

}
