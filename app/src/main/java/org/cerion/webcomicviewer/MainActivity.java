package org.cerion.webcomicviewer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Database db = Database.getInstance(this);
        //db.reset();
        db.log();
        db.loadCachedFeeds();
        //TODO, check internet failure case


        //Prefs.clearLastUpdates(this);
    }
}
