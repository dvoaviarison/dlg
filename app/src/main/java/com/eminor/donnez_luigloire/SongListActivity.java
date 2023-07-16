package com.eminor.donnez_luigloire;

import java.util.Arrays;
import java.util.List;

public class SongListActivity extends ListActivityBase {

    @Override
    protected void createAdapter()
    {
        List<String> values = Arrays.asList(getIntent().getExtras().getStringArray("songs"));
        this.arrayAdapter = new CustomArrayAdapter(SongListActivity.this, values, listView);
    }
}
