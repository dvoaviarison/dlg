package com.eminor.donnez_luigloire;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class FavoriteListActivity extends ListActivityBase {

    @Override
    protected void createAdapter()
    {
        // String[] values = getIntent().getExtras().getStringArray("songs");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> favorites = sharedPref.getStringSet("favorites", null);
        List<String> values = new ArrayList<>(favorites);
        Collections.sort(values);

        this.arrayAdapter = new CustomArrayAdapter(this, values, this.listView);
    }
}
