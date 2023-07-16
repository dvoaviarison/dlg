package com.eminor.donnez_luigloire;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private LyricsMgr lyricsMgr;
    private MusicMgr musicMgr;
    private FileManager fileMgr;
    private String currentLyricsNumber = "001";
    private List<String> allSongsList = new ArrayList<>();
    public Set<String> favoriteSongsList = new HashSet<>();
    private SimpleCursorAdapter mAdapter;
    private NavigationView navigationView;
    private ColorStateList navMenuColorLight;
    private ColorStateList navMenuColorNight;
    //private android.support.v7.app.ActionBar mActionBar;
    //private float mActionBarHeight;
    //private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        this.doDefaultStuffOnCreate(savedInstanceState);

        /* START HERE */
        this.doDisplayLyrics(currentLyricsNumber);
        this.doPrepareSearchView();
    }

    private void doDisplayLyrics(String lyricsSourceFileName)
    {
        TextView edtLyrics = (TextView)findViewById(R.id.edtLyrics);
        StringBuilder lyrics = this.lyricsMgr.GetLyricsFromAssetFile(lyricsSourceFileName);

        edtLyrics.setText((Html.fromHtml(lyrics.toString())));

        this.currentLyricsNumber = lyricsSourceFileName;
    }

    private void doDefaultStuffOnCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.onFLoatingAction();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navMenuColorLight = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_selected},
                        new int[]{android.R.attr.state_selected}
                },
                new int[]{
                        getResources().getColor(R.color.colorMenuItemLight),
                        getResources().getColor(R.color.colorMenuItemLight)
                });

        navMenuColorNight = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_selected},
                        new int[]{android.R.attr.state_selected}
                },
                new int[]{
                        getResources().getColor(R.color.colorMenuItemNight),
                        getResources().getColor(R.color.colorMenuItemNight)
                });

        if (this.lyricsMgr == null)
        {
            this.lyricsMgr = new LyricsMgr(this);
        }

        if (this.musicMgr == null)
        {
            this.musicMgr = new MusicMgr(this);
        }
    }

    private void onFLoatingAction()
    {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //final MenuItem playItem = (MenuItem) this.findViewById(R.id.action_play);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.this.musicMgr.IsPlaying()) {
                    // Stop playing
                    //playItem.setTitle(R.string.action_stop);
                    fab.setImageResource(android.R.drawable.ic_media_play);
                    MainActivity.this.musicMgr.StopMusic();
                } else {
                    Snackbar snackbar = Snackbar.make(view, "Ouverture du cantique numero: " + MainActivity.this.currentLyricsNumber, Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    ViewGroup group = (ViewGroup) snackbar.getView();
                    group.setBackgroundColor(getResources().getColor(R.color.colorPrimaryComp));
                    snackbar.show();

                    // Start playing
                    //playItem.setTitle(R.string.action_stop);
                    fab.setImageResource(R.drawable.ic_media_stop);
                    MainActivity.this.musicMgr.PlayMusic(MainActivity.this.currentLyricsNumber);
                }
            }
        });
    }

    @Override
    public void onPause() {
        try {
            super.onPause();  // Always call the superclass method first
            this.fileMgr.WriteFavorites();
            this.fileMgr.WriteOptions();

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setImageResource(android.R.drawable.ic_media_play);
            this.musicMgr.StopMusic();

            // Logs 'app deactivate' App Event.
            FBEventMgr.deactivateApp(this);
        }
        catch (Exception e){
            // Absorb
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        FBEventMgr.activateApp(this);
    }

    private void doPrepareSearchView()
    {
        this.allSongsList = this.lyricsMgr.GetListLyrics();
        final String[] from = new String[] {"songTitle"};
        final int[] to = new int[] {android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(this.getBaseContext(),
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                boolean isNightMode = sharedPref.getBoolean("isNightMode", false);
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextColor(isNightMode ? getResources().getColor(R.color.colorMenuItemNight) : getResources().getColor(R.color.colorMenuItemLight));
                tv.setBackgroundColor(isNightMode ? getResources().getColor(R.color.colorNavMenuNight) : getResources().getColor(R.color.colorNavMenuLight));

                return view;
            }
        };

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final SearchView searchView = (SearchView) findViewById(R.id.searchLyrics);
        searchView.setSuggestionsAdapter(mAdapter);

        // Getting selected (clicked) item suggestion
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

            @Override
            public boolean onSuggestionClick(int position) {
                MatrixCursor cursor = (MatrixCursor) mAdapter.getCursor();
                String selected = cursor.getString(1);// 1 means title
                String[] components = selected.split("-");

                searchView.onActionViewCollapsed();
                searchView.setQuery("", false);
                if (components.length > 0) {
                    String songNum = components[0].trim();
                    MainActivity.this.doDisplayLyrics(songNum);
                }

                if (MainActivity.this.musicMgr.IsPlaying()) {
                    // Stop playing
                    fab.setImageResource(android.R.drawable.ic_media_play);
                    MainActivity.this.musicMgr.StopMusic();
                }

                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                populateAdapter(s);
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if (this.fileMgr == null)
        {
            this.fileMgr = new FileManager(this.getApplicationContext());

            // Favorites.
            this.fileMgr.GetFavorites();

            // Options.
            this.fileMgr.GetOptions();
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        this.favoriteSongsList = sharedPref.getStringSet("favorites", null);
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout_song);
        EditText text = (EditText)findViewById(R.id.edtLyrics);
        boolean isNightMode = sharedPref.getBoolean("isNightMode", false);
        boolean isKeepAwake = sharedPref.getBoolean("isKeepAwake", false);
        boolean isAnimated = sharedPref.getBoolean("isAnimated", true);

        navigationView.setItemTextColor(isNightMode ? navMenuColorNight : navMenuColorLight);
        navigationView.setItemIconTintList(isNightMode ? navMenuColorNight : navMenuColorLight);
        navigationView.setBackgroundColor(isNightMode ? getResources().getColor(R.color.colorNavMenuNight) : getResources().getColor(R.color.colorNavMenuLight));

        MenuItem nightItem = navigationView.getMenu().findItem(R.id.nav_night_mode);
        MenuItem awakeItem = navigationView.getMenu().findItem(R.id.nav_keep_awake);
        MenuItem animationItem = navigationView.getMenu().findItem(R.id.nav_enable_Animation);

        layout.setBackgroundColor(isNightMode ? getResources().getColor(R.color.colorLayoutNight) : getResources().getColor(R.color.colorLayoutLight));
        text.setTextColor(isNightMode ? getResources().getColor(R.color.colorTextNight) : getResources().getColor(R.color.colorTextLight));
        nightItem.setIcon(isNightMode ? R.drawable.ic_night : R.drawable.ic_day);

        awakeItem.setIcon(isKeepAwake ? R.drawable.ic_toggle_on : R.drawable.ic_toggle_off);
        if (isKeepAwake)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        animationItem.setIcon(isAnimated ? R.drawable.ic_toggle_on : R.drawable.ic_toggle_off);

        String songTitle = this.lyricsMgr.GetLyricsTitle(this.currentLyricsNumber, this.allSongsList);

        // Add and Remove menu items
        MenuItem addItem = menu.findItem(R.id.action_add_to_favorites);
        MenuItem removeItem = menu.findItem(R.id.action_remove_from_favorites);
        if (this.favoriteSongsList != null && this.favoriteSongsList.contains(songTitle))
        {
            removeItem.setVisible(true);
            addItem.setVisible(false);
        }
        else
        {
            addItem.setVisible(true);
            removeItem.setVisible(false);
        }

        MenuItem playItem = menu.findItem(R.id.action_play);
        MenuItem stopItem = menu.findItem(R.id.action_stop);
        if (MainActivity.this.musicMgr.IsPlaying())
        {
            playItem.setVisible(false);
            stopItem.setVisible(true);
        }
        else
        {
            stopItem.setVisible(false);
            playItem.setVisible(true);
        }

        this.invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String songTitle = this.lyricsMgr.GetLyricsTitle(this.currentLyricsNumber, this.allSongsList);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (id == R.id.action_add_to_favorites)
        {
            FBEventMgr.logCustumEvent(this, "FAV_LYRICS");
            FBEventMgr.logCustumEvent(this, "FAV_LYRICS" + this.currentLyricsNumber);
            this.addToFavorite(songTitle);
            return true;
        }

        if (id == R.id.action_remove_from_favorites)
        {
            FBEventMgr.logCustumEvent(this, "REM_FAV_LYRICS");
            FBEventMgr.logCustumEvent(this, "REM_FAV_LYRICS" + this.currentLyricsNumber);
            this.removeFromFavorite(songTitle);
            return true;
        }

        if (id == R.id.action_share)
        {
            String songTitleFull = this.lyricsMgr.GetLyricsTitle(currentLyricsNumber, allSongsList);
            lyricsMgr.ShareLyrics(songTitleFull);

            return true;
        }

        if (id == R.id.action_play)
        {

            View view = this.findViewById(android.R.id.content).getRootView();
            Snackbar snackbar = Snackbar.make(view, "Ouverture du cantique numero: " + MainActivity.this.currentLyricsNumber, Snackbar.LENGTH_LONG)
                    .setAction("Action", null);
            ViewGroup group = (ViewGroup) snackbar.getView();
            group.setBackgroundColor(getResources().getColor(R.color.colorPrimaryComp));
            snackbar.show();
            fab.setImageResource(R.drawable.ic_media_stop);
            MainActivity.this.musicMgr.PlayMusic(MainActivity.this.currentLyricsNumber);

            return true;
        }

        if (id == R.id.action_stop)
        {
            fab.setImageResource(android.R.drawable.ic_media_play);
            MainActivity.this.musicMgr.StopMusic();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (id == R.id.nav_all_songs)
        {
            Intent allSongIntent = new Intent(MainActivity.this, SongListActivity.class);
            String[] values = new String[this.allSongsList.size()];
            values = this.allSongsList.toArray(values);

            allSongIntent.putExtra("songs", values);
            startActivityForResult(allSongIntent, 0);
        }
        else if (id == R.id.nav_favorites)
        {
            Intent allSongIntent = new Intent(MainActivity.this, FavoriteListActivity.class);
            startActivityForResult(allSongIntent, 0);
        }
        else if (id == R.id.nav_night_mode)
        {
            this.applyNightModeOptions(item);
            return false;
        }
        else if (id == R.id.nav_keep_awake)
        {
            this.applyKeepAwake(item);
            return false;
        }
        else if (id == R.id.nav_enable_Animation)
        {
            this.applyEnableAnimation(item);
            return false;
        }
        /*else if (id == R.id.nav_donate)
        {
            Intent payPalIntent = new Intent(MainActivity.this, PaypalActivity.class);
            startActivityForResult(payPalIntent, 0);
        }*/
        else if (id == R.id.nav_rate)
        {
            String packageNAme = getPackageName();//"com.eznetsoft.donnezluigloire";//getPackageName();
            Uri uri = Uri.parse("market://details?id=" + packageNAme);
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try
            {
                startActivity(goToMarket);
            }
            catch (ActivityNotFoundException e)
            {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageNAme)));
            }
        }

        fab.setImageResource(android.R.drawable.ic_media_play);
        this.musicMgr.StopMusic();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void applyNightModeOptions(MenuItem item)
    {
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout_song);
        EditText text = (EditText)findViewById(R.id.edtLyrics);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isNightMode = sharedPref.getBoolean("isNightMode", false);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Remove night mode
        item.setIcon(isNightMode ? R.drawable.ic_day : R.drawable.ic_night);
        layout.setBackgroundColor(isNightMode ? Color.WHITE : Color.BLACK);
        text.setTextColor(isNightMode ? Color.BLACK : Color.WHITE);

        editor.putBoolean("isNightMode", !isNightMode);
        editor.apply();
    }

    private void applyKeepAwake(MenuItem item)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isKeepAwake = sharedPref.getBoolean("isKeepAwake", false);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Remove night mode
        item.setIcon(isKeepAwake ? R.drawable.ic_toggle_off : R.drawable.ic_toggle_on);
        if (isKeepAwake)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        else
        {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        editor.putBoolean("isKeepAwake", !isKeepAwake);
        editor.apply();
    }

    private void applyEnableAnimation(MenuItem item)
    {
        // Get animation pref
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isAnimated = sharedPref.getBoolean("isAnimated", true);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Enable/Disable animation
        item.setIcon(isAnimated ? R.drawable.ic_toggle_off : R.drawable.ic_toggle_on);
        editor.putBoolean("isAnimated", !isAnimated);
        editor.apply();
    }

    private void addToFavorite(String songTitle)
    {
        if (!this.favoriteSongsList.contains(songTitle))
        {
            this.favoriteSongsList.add(songTitle);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putStringSet("favorites", this.favoriteSongsList);
            editor.apply();
        }
    }

    private void removeFromFavorite(String songTitle)
    {
        if (this.favoriteSongsList.contains(songTitle))
        {
            this.favoriteSongsList.remove(songTitle);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putStringSet("favorites", this.favoriteSongsList);
            editor.apply();
        }
    }

    private void populateAdapter(String query) {
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "songTitle" });
        int i = 0;
        for (String s : this.allSongsList)
        {
            if (s.toLowerCase().contains(query.toLowerCase()))
            {
                c.addRow(new Object[]{i, s});
            }

            i++;
        }

        mAdapter.changeCursor(c);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case (0) :
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    // TODO Extract the data returned from the child Activity.
                    String songNum = data.getExtras().getString("songKey");

                    this.doDisplayLyrics(songNum);
                }

                break;
            }
        }
    }
}
