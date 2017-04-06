package com.iantmeyer.spyfall.game;

import android.content.Context;

import com.google.gson.Gson;
import com.iantmeyer.spyfall.App;
import com.iantmeyer.spyfall.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

public class LocationManager {

    private static final String TAG = "LocationManager";

    @Inject Context mAppContext;

    private final Locations mLocations;

    private Random mRandom;

    public LocationManager() {
        App.getComponent().inject(this);

        InputStream raw = mAppContext.getResources().openRawResource(R.raw.locations);
        Reader reader = new BufferedReader(new InputStreamReader(raw));

        Gson gson = new Gson();
        mLocations = gson.fromJson(reader, Locations.class);
        mRandom = new Random();
    }

    public List<Location> getLocations() {
        return mLocations.locations;
    }

    HashMap<Contact, Role> assignedRoles(List<Contact> contacts) {
        Location location = getRandom(mLocations.locations);

        HashMap<Contact, Role> output = new HashMap<>();
        ArrayList<Map.Entry<String, Role>> rolesList = new ArrayList<>(location.roles.entrySet());
        Collections.shuffle(rolesList);

        int firstPlayerIdx = randomIndex(contacts.size());

        int randIdx = randomIndex(contacts.size());
        for (int idx = 0; idx < contacts.size(); idx++) {
            Contact contact = contacts.get(idx);
            if (idx == firstPlayerIdx) {
                contact.first = true;
            }
            Role role;
            if (idx == randIdx) {
                role = Role.spyInstance();
            } else {
                role = rolesList.get(idx).getValue();
                role.location = location.title;
                role.image = R.drawable.ic_person;
            }
            output.put(contact, role);
        }
        return output;
    }

    private <Object> Object getRandom(List<Object> objects) {
        int randIdx = mRandom.nextInt(objects.size());
        return objects.get(randIdx);
    }

    private int randomIndex(int setSize) {
        if (setSize <= 1) {
            return 0;
        } else {
            return mRandom.nextInt(setSize - 1);
        }
    }
}