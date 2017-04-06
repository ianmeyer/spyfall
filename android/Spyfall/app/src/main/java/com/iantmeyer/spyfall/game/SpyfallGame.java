package com.iantmeyer.spyfall.game;

import android.util.Log;

import com.iantmeyer.spyfall.BuildConfig;
import com.iantmeyer.spyfall.sms.SmsCallback;
import com.iantmeyer.spyfall.sms.SmsHelper;
import com.iantmeyer.spyfall.util.PhoneUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpyfallGame implements Game {

    private static final String TAG = "SpyfallGame";

    private final SmsHelper mSmsHelper;

    private final LocationManager mLocationManager;

    private final GameSettings mGameSettings;

    private GameCallback mGameCallback;

    private List<Contact> mOnDeckContactList;

    private Clock mClock;

    public SpyfallGame(SmsHelper smsHelper, LocationManager locationManager, GameSettings gameSettings) {
        mSmsHelper = smsHelper;
        mLocationManager = locationManager;
        mGameSettings = gameSettings;
        mClock = gameSettings.getClock();
        mSmsHelper.setSmsCallback(mSmsCallback);

        testLocations();
    }

    public void setGameCallback(GameCallback gameCallback) {
        mGameCallback = gameCallback;
        mGameCallback.onGameUpdate(mGameSettings.getState());
    }

    @Override
    public synchronized boolean startGame() {
        if (!validateContacts(mOnDeckContactList)) {
            return false;
        }
        HashMap<Contact, Role> rolesMap = mLocationManager.assignedRoles(mOnDeckContactList);
        for (Map.Entry<Contact, Role> entry : rolesMap.entrySet()) {
            if (entry.getKey().isHost) {
                mGameSettings.setMyRole(entry.getValue());
            } else {
                String phoneNumber = entry.getKey().phone;
                String message = getMessage(entry.getValue());

                mSmsHelper.sendSms(phoneNumber, message);
            }
        }
        mGameSettings.setLocked(false);
        mGameSettings.setContactList(mOnDeckContactList);
        mClock = Clock.withEndTime(System.currentTimeMillis() + 1000L * mGameSettings.getSetupTimeLimit());
        mGameSettings.setClock(mClock);
        mGameSettings.setState(GameCallback.State.PLAY);
        mGameCallback.onGameUpdate(GameCallback.State.PLAY);
        return true;
    }

    @Override
    public boolean reopenLastGame() {
        if (getRole() == null) {
            return false;
        }
        mClock = mGameSettings.getClock();
        mGameSettings.setState(GameCallback.State.PLAY);
        mGameCallback.onGameUpdate(GameCallback.State.PLAY);
        return true;
    }

    @Override
    public void setOnDeckContactList(List<Contact> contactList) {
        mOnDeckContactList = new ArrayList<>();
        mOnDeckContactList.addAll(contactList);
        mOnDeckContactList.add(0, Contact.hostInstance());
    }

    @Override
    public void leaveGame() {
        mGameSettings.setState(GameCallback.State.SETUP);
        mGameCallback.onGameUpdate(mGameSettings.getState());
    }

    @Override
    public List<Contact> getContactList() {
        return mGameSettings.getContactList();
    }

    private SmsCallback mSmsCallback = new SmsCallback() {
        @Override
        public synchronized void onSmsStatusUpdate(SmsStatus status) {
            List<Contact> mContactList = getContactList();
            for (Contact contact : mContactList) {
                if (contact.phone != null && contact.phone.equals(status.phoneNumber)) {
                    contact.status = status;
                    mGameSettings.setContactList(mContactList);
                    return;
                }
            }
            Log.d(TAG, "Contact not found with phone number: " + status.phoneNumber);
        }
    };

    private String getMessage(Role role) {
        String lineBreak = System.getProperty("line.separator");
        String message = "";
        if (role.title.equals("Spy")) {
            message += "You are the SPY!" + lineBreak;
        } else {
            message += "You are the" + lineBreak;
            message += role.title.toUpperCase() + lineBreak;
            message += "at the" + lineBreak;
            message += role.location.toUpperCase() + "." + lineBreak;
        }
        message += "https://goo.gl/gNXNDY";
        return message;
    }

    private boolean validateContacts(List<Contact> contactList) {
        if (contactList.size() < 3) {
            mGameCallback.onError("Minimum 3 players");
            return false;
        }
        if (contactList.size() > 8) {
            mGameCallback.onError("Maximum 8 players");
            return false;
        }
        for (Contact contact : contactList) {
            if (contact.isHost) {
                continue;
            }
            if (!PhoneUtil.isValidNumber(contact.phone)) {
                mGameCallback.onError("Invalid phone number: " + contact.phone);
                return false;
            }
        }
        return true;
    }

    private void testLocations() {
        if (BuildConfig.DEBUG) {
            return;
        }
        int maxLength = 0;
        List<Role> badRoleList = new ArrayList<>();
        List<Location> locations = mLocationManager.getLocations();
        for (Location location : locations) {
            for (Role role : location.roles.values()) {
                role.location = location.title;
                String message = getMessage(role);
                if (message.length() > 140) {
                    badRoleList.add(role);
                }
                if (message.length() > maxLength) {
                    maxLength = message.length();
                }
            }
        }
        Log.d(TAG, "There are " + badRoleList.size() + " problem roles. Max length: " + maxLength);
        for (Role role : badRoleList) {
            Log.d(TAG, "    Location: " + role.location + ", Role: " + role.title);
        }
    }

    @Override
    public Clock getClock() {
        return mClock;
    }

    @Override
    public void restartTimer() {
        if (mClock.resume()) {
            mGameSettings.setClock(mClock);
        }
    }

    @Override
    public long pauseTimer() {
        if (mClock.pause()) {
            mGameSettings.setClock(mClock);
        }
        return mClock.endTime;
    }

    @Override
    public Role getRole() {
        return mGameSettings.getRole();
    }

    @Override
    public boolean isRoleLocked() {
        return mGameSettings.getLocked();
    }

    @Override
    public void lockRole(boolean locked) {
        mGameSettings.setLocked(locked);
    }

    @Override
    public long getSetupTimeLimit() {
        return mGameSettings.getSetupTimeLimit();
    }

    @Override
    public void setSetupTimeLimit(long timeLimit) {
        mGameSettings.setSetupTimeLimit(timeLimit);
    }
}