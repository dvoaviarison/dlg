package com.eminor.donnez_luigloire;

import java.io.Serializable;

/**
 * Created by Daniel on 10/4/15.
 */
public class Song implements Serializable
{
    public boolean IsFavorite = false;
    public String Title;
    public int Number;

    public Song(int number, String title, boolean isFavorite)
    {
        this.IsFavorite = isFavorite;
        this.Number = number;
        this.Title = title;
    }
}
