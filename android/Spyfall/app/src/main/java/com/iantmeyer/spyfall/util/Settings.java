package com.iantmeyer.spyfall.util;

import android.support.annotation.NonNull;

import com.iantmeyer.spyfall.game.Clock;
import com.iantmeyer.spyfall.game.Contact;
import com.iantmeyer.spyfall.game.GameCallback;
import com.iantmeyer.spyfall.game.Role;

import java.util.List;

public interface Settings {
    void setPhoneIds(List<Long> playerIds);

    @NonNull
    List<Long> getPhoneIds();

    void setContacts(List<Contact> contactList);

    List<Contact> getContacts();

    void setGameState(GameCallback.State state);

    GameCallback.State getGameState();

    Clock getClock();

    void setClock(Clock clock);
//    void setEndTime(long timer);
//
//    long getEndTime();
//
//    void setTimeLimit(long timeLimit);
//
//    long getTimeLimit();

    void setRole(Role role);

    Role getRole();

    void setLocked(boolean locked);

    boolean getLocked();

    void setSetupTimeLimit(long timeLimit);

    long getSetupTimeLimit();
}