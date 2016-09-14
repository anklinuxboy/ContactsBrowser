package com.example.ankitsharma.contactbrowser;

import android.app.Activity;
import android.app.Notification;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by ankitsharma on 9/14/16.
 * Class to handle user searches. Get the intent and search for data
 */
public class SearchableActivity extends Activity {

    private static final String LOG_TAG = SearchableActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
        }
    }
}
