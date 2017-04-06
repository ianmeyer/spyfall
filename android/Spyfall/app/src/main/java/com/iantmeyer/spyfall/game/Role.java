package com.iantmeyer.spyfall.game;

import android.support.annotation.DrawableRes;

import com.iantmeyer.spyfall.R;

public class Role {
    public final boolean isSpy;
    public String title;
    public String location;
    public int image;

    static Role spyInstance() {
        return new Role("Spy", "Unknown", R.drawable.ic_spy, true);
    }

    public Role() {
        this.isSpy = false;
    }

    public Role(String title, String location, @DrawableRes int drawableRes) {
        this(title, location, drawableRes, false);
    }

    protected Role(String title, String location, @DrawableRes int drawableRes, boolean isSpy) {
        this.isSpy = isSpy;
        this.title = title;
        this.location = location;
        this.image = drawableRes;
    }
}