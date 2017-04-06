package com.iantmeyer.spyfall.game;

import java.util.List;

public interface Game {
    void setGameCallback(GameCallback gameCallback);

    void setOnDeckContactList(List<Contact> contactList);

    boolean startGame();

    boolean reopenLastGame();

    void leaveGame();

    List<Contact> getContactList();

    Clock getClock();

    void restartTimer();

    long pauseTimer();

    Role getRole();

    boolean isRoleLocked();

    void lockRole(boolean locked);

    long getSetupTimeLimit();

    void setSetupTimeLimit(long timeLimit);
}