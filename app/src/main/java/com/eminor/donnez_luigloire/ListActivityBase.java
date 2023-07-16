package com.eminor.donnez_luigloire;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by Daniel on 10/14/15.
 */
public class ListActivityBase extends AppCompatActivity
{
    public MusicMgr musicMgr;
    protected ListView listView;
    protected LyricsMgr lyricsMgr;
    protected CustomArrayAdapter arrayAdapter;
    // private android.support.v7.app.ActionBar mActionBar;
    // private float mLastFirstVisibleItem;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        super.onCreate(savedInstanceState);

        this.musicMgr = new MusicMgr(this);

        this.lyricsMgr = new LyricsMgr(this);

        setContentView(R.layout.activity_song_list);
        this.listView = (ListView) findViewById(R.id.list_all_songs);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Night Mode vs Normal mode
        boolean isNightMode = sharedPref.getBoolean("isNightMode", false);
        this.listView.setBackgroundColor(isNightMode ? Color.BLACK : Color.parseColor("#f5f5f5"));

        // Create appropriate adapter
        this.createAdapter();

        listView.setAdapter(arrayAdapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String songTitle = listView.getItemAtPosition(position).toString();

                if (songTitle == null) {
                    return;
                }

                String[] components = songTitle.split(" - ");

                if (components.length == 0) {
                    return;
                }

                String numSong = components[0];

                Intent resultIntent = new Intent();

                // TODO Add extras or a data URI to this intent as appropriate.
                resultIntent.putExtra("songKey", numSong);

                setResult(Activity.RESULT_OK, resultIntent);

                finish();
            }
        });

        // mActionBar = getSupportActionBar();
        // listView.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener);
    }

    /*private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener()
    {
        @Override
        public void onScrollChanged()
        {
            float currentFirstVisibleItem = listView.getFirstVisiblePosition();

            if (currentFirstVisibleItem > mLastFirstVisibleItem)
            {
                mActionBar.hide();
            }
            else if (currentFirstVisibleItem < mLastFirstVisibleItem)
            {
                mActionBar.show();
            }

            mLastFirstVisibleItem = currentFirstVisibleItem;
        }
    };*/

    @Override
    public void onPause()
    {
        super.onPause();  // Always call the superclass method first
        this.musicMgr.StopMusic();

        final FloatingActionButton fab = (FloatingActionButton) this.findViewById(R.id.fab_stop);
        fab.setVisibility(View.INVISIBLE);
        this.arrayAdapter.notifyDataSetChanged();
    }

    protected void createAdapter(){};
}
