package com.iantmeyer.spyfall.game;

public interface GameCallback {
    void onGameUpdate(State state);

    void onError(String message);

    enum State {
        PLAY,
        SETUP
    }
}
