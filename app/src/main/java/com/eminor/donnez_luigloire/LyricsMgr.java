package com.eminor.donnez_luigloire;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 10/3/15.
 */
public class LyricsMgr {

    private Context context;
    private FileManager fileMgr;

    public LyricsMgr(Context context)
    {
        this.context = context;
        fileMgr = new FileManager(this.context);
    }

    public String GetLyricsTitle(String numSong, List<String> allSongs)
    {
        for (String s : allSongs)
        {
            if (s.startsWith(numSong + " - "))
            {
                return s;
            }
        }

        return "";
    }

    public StringBuilder GetPlainLyricsFromAssetFile(String assetFileName)
    {
        StringBuilder lyrics = new StringBuilder();

        try
        {
            String str;
            InputStream lyricsStream = this.context.getAssets().open("lyrics/D"+ assetFileName + ".txt");
            String numSong = assetFileName.replace("D", "");
            BufferedReader in = new BufferedReader(new InputStreamReader(lyricsStream, "Cp1252"));

            int lineNum = 0;
            while ((str=in.readLine()) != null)
            {
                lineNum++;

                // If title
                if (lineNum == 1)
                {
                    str = str.toUpperCase();
                    lyrics.append(numSong
                            + " - "
                            + str);
                    lyrics.append(System.getProperty("line.separator"));
                    continue;
                }

                // Lyrics
                lyrics.append(str);
                lyrics.append(System.getProperty("line.separator"));
            }

            in.close();
        }
        catch (IOException e)
        {
            Toast.makeText(this.context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return lyrics;
    }

    public StringBuilder GetLyricsFromAssetFile(String assetFileName)
    {
        StringBuilder lyrics = new StringBuilder();

        try
        {
            String str;
            InputStream lyricsStream = this.context.getAssets().open("lyrics/D"+ assetFileName + ".txt");
            String numSong = assetFileName.replace("D", "");
            BufferedReader in = new BufferedReader(new InputStreamReader(lyricsStream, "Cp1252"));

            int lineNum = 0;
            while ((str=in.readLine()) != null)
            {
                lineNum++;

                // If title
                if (lineNum == 1)
                {
                    str = str.toUpperCase();
                    lyrics.append("<b><font color=\""
                            + this.context.getResources().getColor(R.color.colorPrimaryComp)
                            + "\">"
                            + numSong
                            + " - "
                            + str
                            + "</font></b><br/>");
                    continue;
                }

                // Refrain
                if (str.contains("Refrain") || this.tryParseInt(str.trim()))
                {
                    lyrics.append("<b><font color=\""
                            + this.context.getResources().getColor(R.color.colorPrimaryComp)
                            + "\">"
                            + str
                            + "</font></b><br/>");
                    continue;
                }

                // Lyrics
                lyrics.append(str + "<br/>");
            }

            in.close();
        }
        catch (IOException e)
        {
            fileMgr.Log(e.getMessage());
        }

        return lyrics;
    }

    public List<String> GetListLyrics()
    {
        String[] listLyrics;
        ArrayList<String> listLyricsFormated = new ArrayList<String>();

        try
        {
            listLyrics = this.context.getAssets().list("lyrics");
            if (listLyrics.length > 0)
            {
                // This is a folder
                for (String file : listLyrics)
                {

                    String str;
                    InputStream lyricsStream = this.context.getAssets().open("lyrics/"+ file);
                    BufferedReader in = new BufferedReader(new InputStreamReader(lyricsStream, "Cp1252"));

                    if ((str=in.readLine()) != null)
                    {
                        String songTitle = file.replace("D", "").replace(".txt", "");
                        songTitle = songTitle + " - " + str;
                        listLyricsFormated.add(songTitle);
                    }

                    in.close();
                }
            }
        }
        catch (IOException e)
        {
            fileMgr.Log(e.getMessage());
        }

        return listLyricsFormated;
    }

    public void ShareLyrics(String songTitleFull)
    {
        String songNum = songTitleFull.split(" - ")[0];
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        // Title
        intent.putExtra(Intent.EXTRA_TITLE, songTitleFull);

        // Subject
        intent.putExtra(Intent.EXTRA_SUBJECT, "Paroles du chant: " + songTitleFull);

        // Content
        String packageNAme = this.context.getPackageName();//"com.instagram.android;
        StringBuilder lyrics = GetPlainLyricsFromAssetFile(songNum);
        lyrics.append(System.getProperty("line.separator"));
        lyrics.append(System.getProperty("line.separator"));
        lyrics.append("Vous est proposé par https://play.google.com/store/apps/details?id=" + packageNAme);
        lyrics.append(System.getProperty("line.separator"));
        intent.putExtra(Intent.EXTRA_TEXT, lyrics.toString());

        try
        {
            context.startActivity(Intent.createChooser(intent, "Partager via"));
        }
        catch (Exception e)
        {
            fileMgr.Log(e.getMessage());
        }
    }

    private boolean tryParseInt(String value) {
        try
        {
            Integer.parseInt(value);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

}
