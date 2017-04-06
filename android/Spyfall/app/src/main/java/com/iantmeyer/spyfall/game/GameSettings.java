package com.iantmeyer.spyfall.game;

import com.iantmeyer.spyfall.util.Settings;

import java.util.List;

public class GameSettings {

    private transient Settings mSettings;

    private GameCallback.State mState;
    private Clock mClock;
    //    private long mTimeLimit;
    private long mSetupTimeLimit;
    //    private long mEndTime;
    private List<Contact> mContactList;
    private Role mRole;
    private boolean mLocked;

    public GameSettings(Settings settings) {
        mSettings = settings;
        mState = settings.getGameState();
        mClock = settings.getClock();
//        mTimeLimit = settings.getTimeLimit();
//        mEndTime = settings.getEndTime();
        mContactList = settings.getContacts();
        mRole = settings.getRole();
        mLocked = settings.getLocked();
        mSetupTimeLimit = settings.getSetupTimeLimit();
    }

    GameCallback.State getState() {
        return mState;
    }

    void setState(GameCallback.State state) {
        mState = state;
        mSettings.setGameState(state);
    }

    Clock getClock() {
        return mClock;
    }

    void setClock(Clock clock) {
        mClock = clock;
        mSettings.setClock(clock);
    }

//    long getEndTime() {
//        return mEndTime;
//    }
//
//    void setEndTime(long endTime) {
//        mEndTime = endTime;
//        mSettings.setEndTime(endTime);
//    }

//    long getTimeLimit() {
//        return mTimeLimit;
//    }
//
//    void setTimeLimit(long timeLimit) {
//        mTimeLimit = timeLimit;
//        mSettings.setTimeLimit(timeLimit);
//    }

    long getSetupTimeLimit() {
        return mSetupTimeLimit;
    }

    void setSetupTimeLimit(long timeLimit) {
        mSetupTimeLimit = timeLimit;
        mSettings.setSetupTimeLimit(timeLimit);
    }

    List<Contact> getContactList() {
        return mContactList;
    }

    void setContactList(List<Contact> contactList) {
        mContactList = contactList;
        mSettings.setContacts(contactList);
    }

    void setMyRole(Role role) {
        mRole = role;
        mSettings.setRole(role);
    }

    Role getRole() {
        return mRole;
    }

    void setLocked(boolean locked) {
        mLocked = locked;
        mSettings.setLocked(locked);
    }

    boolean getLocked() {
        return mLocked;
    }
}