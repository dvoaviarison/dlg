package com.eminor.donnez_luigloire;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomArrayAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private List<String> names;
    private ListView mListView;
    private MusicMgr musicMgr;
    private LyricsMgr lyricsMgr;
    private int lastPosition = -1;
    private final SharedPreferences sharedPref;

    static class ViewHolder
    {
        TextView text;
        public LinearLayout item;
        LinearLayout menu;
        ImageView image;
        LinearLayout wholeThing;
    }

    CustomArrayAdapter(Activity context, List<String> names, ListView listView)
    {
        super(context, R.layout.simple_song_item, names);
        this.context = context;
        this.names = names;

        if (context instanceof ListActivityBase)
        {
            this.musicMgr = ((ListActivityBase)context).musicMgr;
            this.lyricsMgr = ((ListActivityBase)context).lyricsMgr;
        }

        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.mListView = listView;
        this.onFabAction();
    }

    private void onFabAction()
    {
        final FloatingActionButton fab = (FloatingActionButton) context.findViewById(R.id.fab_stop);
        //final MenuItem playItem = (MenuItem) this.findViewById(R.id.action_play);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicMgr.IsPlaying())
                {
                    musicMgr.StopMusic();
                    fab.setVisibility(View.INVISIBLE);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.simple_song_item, null);

            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.song_title);
            viewHolder.image = (ImageView) rowView.findViewById(R.id.set_favorite);
            viewHolder.item = (LinearLayout) rowView.findViewById(R.id.item_layout);
            viewHolder.menu = (LinearLayout) rowView.findViewById(R.id.menu_layout);
            viewHolder.wholeThing = (LinearLayout) rowView.findViewById(R.id.whole_thing);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        String s = names.get(position);
        String songNum = s.split(" - ")[0];
        holder.text.setText(s);

        final Set<String> favorites = this.sharedPref.getStringSet("favorites", null);

        // Night Mode vs Normal mode
        final boolean isNightMode = sharedPref.getBoolean("isNightMode", false);
        holder.text.setTextColor(isNightMode ? Color.WHITE : Color.BLACK);
        holder.wholeThing.setBackgroundResource(isNightMode ? R.drawable.item_selector_night : R.drawable.item_selector);
        holder.image.setImageResource(isNightMode ? R.drawable.ic_more_vert_night : R.drawable.ic_more_vert);

        if (context instanceof SongListActivity)
        {
            if (favorites.contains(s))
            {
                holder.text.setTextColor(context.getResources().getColor(R.color.colorPrimaryComp));
            }
            else
            {
                holder.text.setTextColor(isNightMode ? Color.WHITE : Color.BLACK);
            }
        }

        if (songNum.equalsIgnoreCase(musicMgr.currentMusic))
        {
            holder.text.setTypeface(null, Typeface.BOLD);
        }
        else
        {
            holder.text.setTypeface(null, Typeface.NORMAL);
        }

        holder.item.setOnClickListener(mOnTitleClickListener);
        holder.menu.setOnClickListener(mOnMenuClickListener);

        this.doScrollingAnimation(rowView, position);

        return rowView;
    }

    private void doScrollingAnimation(View rowView, int position){
        boolean isAnimated = sharedPref.getBoolean("isAnimated", true);
        if (isAnimated) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            rowView.startAnimation(animation);
            lastPosition = position;
        }
    }

    private String getSongNum(String songTitle)
    {
        String[] components = songTitle.split(" - ");

        if (components.length == 0)
        {
            return null;
        }

        return components[0];
    }

    private View.OnClickListener mOnTitleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            final int position = mListView.getPositionForView((View) v.getParent());
            String songTitle = mListView.getItemAtPosition(position).toString();
            ViewHolder holder = (ViewHolder)((View) v.getParent()).getTag();
            holder.wholeThing.setPressed(true);

            if (songTitle == null)
            {
                return;
            }

            String numSong = CustomArrayAdapter.this.getSongNum(songTitle);

            if (numSong == null)
            {
                return;
            }

            Intent resultIntent = new Intent();

            // TODO Add extras or a data URI to this intent as appropriate.
            resultIntent.putExtra("songKey", numSong);

            context.setResult(Activity.RESULT_OK, resultIntent);

            context.finish();
        }
    };

    private View.OnClickListener mOnMenuClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            final View view = v;
            PopupMenu menu = new PopupMenu(context, view);
            menu.inflate(R.menu.main);

            menu.show();

            final Set<String> favorites = sharedPref.getStringSet("favorites", null);
            final FloatingActionButton fab = (FloatingActionButton) context.findViewById(R.id.fab_stop);

            final int position = mListView.getPositionForView((View) view.getParent());

            MenuItem stopItem = menu.getMenu().findItem(R.id.action_stop);
            stopItem.setVisible(false);

            final String songTitleFull = getItem(position);
            final String songNum = songTitleFull.split(" - ")[0];
            if (favorites.contains(songTitleFull))
            {
                menu.getMenu().removeItem(R.id.action_add_to_favorites);
            }
            else
            {
                menu.getMenu().removeItem(R.id.action_remove_from_favorites);
            }

            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                    int menuId = item.getItemId();

                    if (menuId == R.id.action_add_to_favorites)
                    {
                        favorites.add(songTitleFull);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putStringSet("favorites", favorites);

                        //commits your edits
                        editor.commit();

                        FBEventMgr.logCustumEvent(context, "FAV_LYRICS");
                        FBEventMgr.logCustumEvent(context, "FAV_LYRICS" + songNum);

                        notifyDataSetChanged();
                        return true;
                    }

                    if (menuId == R.id.action_remove_from_favorites)
                    {
                        FBEventMgr.logCustumEvent(context, "REM_FAV_LYRICS");
                        FBEventMgr.logCustumEvent(context, "REM_FAV_LYRICS" + songNum);

                        favorites.remove(songTitleFull);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putStringSet("favorites", new HashSet<String>(favorites));
                        editor.commit();

                        if (!(context instanceof SongListActivity))
                        {
                            names.remove(songTitleFull);
                            if (musicMgr.IsPlaying() && songNum.equalsIgnoreCase(musicMgr.currentMusic))
                            {
                                musicMgr.StopMusic();
                                fab.setVisibility(View.INVISIBLE);
                            }
                        }

                        notifyDataSetChanged();
                        return true;
                    }

                    if (menuId == R.id.action_play)
                    {
                        if (musicMgr.IsPlaying())
                        {
                            musicMgr.StopMusic();
                        }

                        FloatingActionButton fab = (FloatingActionButton) context.findViewById(R.id.fab_stop);
                        fab.setVisibility(View.VISIBLE);
                        Snackbar snackbar = Snackbar.make(view, "Ouverture du cantique numero: " + songNum, Snackbar.LENGTH_LONG)
                                .setAction("Action", null);
                        ViewGroup group = (ViewGroup) snackbar.getView();
                        group.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryComp));
                        snackbar.show();
                        fab.setImageResource(R.drawable.ic_media_stop);
                        musicMgr.PlayMusic(songNum);

                        notifyDataSetChanged();
                        return true;
                    }

                    if (menuId == R.id.action_share)
                    {
                        lyricsMgr.ShareLyrics(songTitleFull);
                    }

                    return false;
                }
            });
        }

    };
}
