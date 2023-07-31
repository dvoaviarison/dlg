package com.eminor.donnez_luigloire;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;


/**
 * Created by Daniel on 10/4/15.
 */
public class MusicMgr
{
    private Context context;
    private MediaPlayer mediaPlayer;
    public String currentMusic;

    public MusicMgr(Context context)
    {
        this.context = context;
        this.mediaPlayer = new MediaPlayer();
    }

    public void PlayMusic(String musicFileName)
    {
        try
        {
            if (this.mediaPlayer.isPlaying())
            {
                this.mediaPlayer.stop();
                this.mediaPlayer.release();
                this.mediaPlayer = new MediaPlayer();
            }

            AssetFileDescriptor descriptor = this.context.getAssets().openFd("music/D"+ musicFileName + ".mid");
            this.mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            this.mediaPlayer.prepare();
            this.mediaPlayer.setVolume(1f, 1f);
            this.mediaPlayer.setLooping(true);
            this.mediaPlayer.start();
            this.currentMusic = musicFileName;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void StopMusic()
    {

        if (this.mediaPlayer.isPlaying())
        {
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.currentMusic = "";
            this.mediaPlayer = new MediaPlayer();
        }
    }

    public boolean IsPlaying()
    {
        return this.mediaPlayer.isPlaying();
    }
}
