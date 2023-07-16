package com.eminor.donnez_luigloire;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SongsByTopic extends ListActivityBase {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String[] allSongs;
    public MusicMgr musicMgr;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_by_topic);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        allSongs = getIntent().getExtras().getStringArray("songs");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_songs_by_topic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, SongsByTopic.this, SongsByTopic.this.allSongs);
        }

        @Override
        public int getCount() {
            // Show 8 total pages.
            return 8;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return "Dieu, le Père"; // 1 - 19
                case 1:
                    return "Dieu, le Fils"; // 20 - 109
                case 2:
                    return "Dieu, le Saint-Esprit"; // 110 - 121
                case 3:
                    return "Le culte"; // 122 - 261
                case 4:
                    return "La bonne nouvelle"; // 262 - 331
                case 5:
                    return "Convictions"; // 332 - 359
                case 6:
                    return "Lieux et temps de la foi"; // 360 - 420
                case 7:
                    return "Expression de la foi"; // 421 - 520
            }

            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static Activity context;
        private static String[] allSongs;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, Activity _context, String[] songs)
        {
            context = _context;

            if (allSongs == null)
            {
                allSongs = songs;
            }

            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_songs_by_topic, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.list_all_songs_fragment);

            try
            {
                int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
                List<String> portion = new ArrayList<String>();
                switch (sectionNumber)
                {
                    case 1: // 1 - 19

                        int num = 0;
                        for (int i = 0; i < 19; i++)
                        {
                            portion.add(allSongs[i]);
                            num++;
                        }

                        break;
                    case 2: // 20 - 109
                        num = 0;
                        for (int i = 19; i < 109; i++)
                        {
                            portion.add(allSongs[i]);
                            num++;
                        }

                        break;
                    case 3: // 110 - 121
                        num = 0;
                        for (int i = 109; i < 121; i++)
                        {
                            portion.add(allSongs[i]);
                        }

                        break;
                    case 4: // 122 - 261
                        for (int i = 121; i < 261; i++)
                        {
                            portion.add(allSongs[i]);
                        }

                        break;
                    case 5: // 262 - 331
                        num = 0;
                        for (int i = 261; i < 331; i++)
                        {
                            portion.add(allSongs[i]);
                        }

                        break;
                    case 6: // 332 - 359
                        num = 0;
                        for (int i = 331; i < 359; i++)
                        {
                            portion.add(allSongs[i]);
                        }

                        break;
                    case 7: // 360 - 420
                        num = 0;
                        for (int i = 359; i < 420; i++)
                        {
                            portion.add(allSongs[i]);
                        }

                        break;
                    case 8: // 421 - 520
                        num = 0;
                        for (int i = 420; i < 519; i++)
                        {
                            portion.add(allSongs[i]);
                        }

                        break;
                    default:
                        break;
                }

                CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(context, portion, listView);
                listView.setAdapter(arrayAdapter);
            }
            catch (Exception e)
            {
                Log.w("Exception", e.getMessage());
            }

            return rootView;
        }
    }
}
