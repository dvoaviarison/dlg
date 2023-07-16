package com.eminor.donnez_luigloire;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Daniel on 10/7/15.
 */
public class FileManager
{
    private String favoritesFilePath;
    private String optionsFilePath;
    private String logFilePath;
    public Context context;

    public FileManager(Context context)
    {
        this.context = context;
        this.favoritesFilePath = this.context.getFilesDir() + "/favorites.txt";
        this.optionsFilePath = this.context.getFilesDir() + "/options.txt";
        this.logFilePath = this.context.getFilesDir() + "/dlg.log";
        // this.DeleteFavoriteFile();
    }

    public void GetFavorites()
    {
        Set<String> favorites = new HashSet<>();
        File file = new File(this.favoritesFilePath);
        if(file.exists())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null)
                {
                    favorites.add(line);
                }

                br.close();
            }
            catch (IOException e)
            {
                Toast.makeText(this.context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet("favorites", favorites);
        editor.commit();
    }

    public void WriteFavorites()
    {
        try
        {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            Set<String> favorites = sharedPref.getStringSet("favorites", null);


            File file = new File(favoritesFilePath);
            file.createNewFile();

            try
            {
                FileOutputStream f = new FileOutputStream(file);
                PrintWriter pw = new PrintWriter(f);

                for (String s : favorites)
                {
                    pw.println(s);
                }

                pw.flush();
                pw.close();
                f.close();
            }
            catch (FileNotFoundException e)
            {
                Log(e.getMessage());
            }
            catch (IOException e)
            {
                Log(e.getMessage());
            }
        }
        catch (IOException e) {
            Log(e.getMessage());
        }
    }

    public void Log(String logText)
    {
        try
        {
            File file = new File(this.logFilePath);

            if(!file.exists()) {
                file.createNewFile();
            }

            try
            {
                FileOutputStream f = new FileOutputStream(file);
                PrintWriter pw = new PrintWriter(f);

                pw.println(logText);

                pw.flush();
                pw.close();
                f.close();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        catch (IOException e) {
            Toast.makeText(this.context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void DeleteFavoriteFile()
    {
        File file = new File(favoritesFilePath);
        file.delete();
    }

    public void GetOptions()
    {
        File file = new File(this.optionsFilePath);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        Boolean isNightMode = false;
        Boolean isKeepAwake = false;

        if(file.exists())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                int lineNum = 0;

                while ((line = br.readLine()) != null)
                {
                    switch (lineNum)
                    {
                        case (0):
                        {
                            isNightMode = Boolean.valueOf(line);
                            break;
                        }
                        case (1):
                        {
                            isKeepAwake = Boolean.valueOf(line);
                            break;
                        }
                        default:
                            break;
                    }


                    lineNum++;
                }

                br.close();
                editor.putBoolean("isNightMode", isNightMode);
                editor.putBoolean("isKeepAwake", isKeepAwake);
            }
            catch (IOException e)
            {
                Log(e.getMessage());
            }
        }

        editor.commit();
    }

    public void WriteOptions()
    {
        try
        {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            boolean isNightMode = sharedPref.getBoolean("isNightMode", false);
            boolean isKeepAwake = sharedPref.getBoolean("isKeepAwake", false);


            File file = new File(optionsFilePath);
            file.createNewFile();

            try
            {
                FileOutputStream f = new FileOutputStream(file);
                PrintWriter pw = new PrintWriter(f);

                // 1- Night mode option
                pw.println(isNightMode);

                // 2- Keep awake option
                pw.println(isKeepAwake);

                // 2- Others

                pw.flush();
                pw.close();
                f.close();
            }
            catch (FileNotFoundException e)
            {
                Log(e.getMessage());
            }
            catch (IOException e)
            {
                Log(e.getMessage());
            }
        }
        catch (IOException e) {
            Log(e.getMessage());
        }
    }
}
