package com.iantmeyer.spyfall.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iantmeyer.spyfall.App;
import com.iantmeyer.spyfall.game.Clock;
import com.iantmeyer.spyfall.game.Contact;
import com.iantmeyer.spyfall.game.GameCallback;
import com.iantmeyer.spyfall.game.Role;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SharedPrefSettings implements Settings {

    private static final String TAG = "SharedPrefSettings";

    private static final String SETTINGS_NAME = "settings";

    private static final String KEY_PLAYER_IDS = "key_player_ids";

    private static final String KEY_GAME_STATE = "key_game_state";

    private static final String KEY_CONTACT_LIST = "key_contact_list";

    private static final String KEY_SETUP_TIMER = "key_setup_timer";

    private static final String KEY_CLOCK = "key_clock";

    private static final String KEY_ROLE = "key_role";

    private static final String KEY_LOCKED = "key_locked";

    private long DEFAULT_TIME_LIMIT = 480L;

    @Inject protected Context mAppContext;

    private final SharedPreferences mPrefs;

    @Inject
    public SharedPrefSettings() {
        Log.d(TAG, "Init");
        App.getComponent().inject(this);
        mPrefs = mAppContext.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void setPhoneIds(List<Long> playerIds) {
        saveValue(KEY_PLAYER_IDS, playerIds, true);
    }

    @NonNull
    @Override
    public List<Long> getPhoneIds() {
        List<Long> contactIds = getValue(KEY_PLAYER_IDS);
        if (contactIds == null) {
            contactIds = new ArrayList<>();
        }
        return contactIds;
    }

    @Override
    public void setContacts(List<Contact> contactList) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(contactList);
        saveValue(KEY_CONTACT_LIST, jsonString, false);
    }

    @Override
    public List<Contact> getContacts() {
        Gson gson = new Gson();
        Type outputType = new TypeToken<List<Contact>>() {
        }.getType();
        String jsonString = mPrefs.getString(KEY_CONTACT_LIST, "[]");
        return gson.fromJson(jsonString, outputType);
    }

    @Override
    public void setGameState(GameCallback.State state) {
        mPrefs.edit().putInt(KEY_GAME_STATE, state.ordinal()).apply();
    }

    @Override
    public GameCallback.State getGameState() {
        int ordinal = mPrefs.getInt(KEY_GAME_STATE, GameCallback.State.SETUP.ordinal());
        return GameCallback.State.values()[ordinal];
    }

    @Override
    public Clock getClock() {
        String jsonString = mPrefs.getString(KEY_CLOCK, "");
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Clock.class);
    }

    @Override
    public void setClock(Clock clock) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(clock);
        saveValue(KEY_CLOCK, jsonString, false);
    }

    @Override
    public void setRole(Role role) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(role);
        saveValue(KEY_ROLE, jsonString, false);
    }

    @Override
    public Role getRole() {
        String jsonString = mPrefs.getString(KEY_ROLE, "");
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Role.class);
    }

    @Override
    public void setLocked(boolean locked) {
        mPrefs.edit().putBoolean(KEY_LOCKED, locked).apply();
    }

    @Override
    public boolean getLocked() {
        return mPrefs.getBoolean(KEY_LOCKED, false);
    }

    @Override
    public void setSetupTimeLimit(long timeLimit) {
        mPrefs.edit().putLong(KEY_SETUP_TIMER, timeLimit).apply();
    }

    @Override
    public long getSetupTimeLimit() {
        return mPrefs.getLong(KEY_SETUP_TIMER, DEFAULT_TIME_LIMIT);
    }

    private void saveValue(String key, String value, boolean immediate) {
        if (immediate) {
            mPrefs.edit().putString(key, value).commit();
        } else {
            mPrefs.edit().putString(key, value).apply();
        }
    }

    private String getValue(String key, String defaultValue) {
        return mPrefs.getString(key, defaultValue);
    }

    private void saveValue(String key, long value) {
        mPrefs.edit().putLong(key, value).apply();
    }

    private long getValue(String key, long defaultValue) {
        return mPrefs.getLong(key, defaultValue);
    }

    private void saveValue(String key, List<Long> playerIds, boolean immediate) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(playerIds);
        saveValue(key, jsonString, immediate);
    }

    private List<Long> getValue(String key) {
        Gson gson = new Gson();
        Type outputType = new TypeToken<List<Long>>() {
        }.getType();
        String jsonString = mPrefs.getString(key, "[]");
        return gson.fromJson(jsonString, outputType);
    }
}