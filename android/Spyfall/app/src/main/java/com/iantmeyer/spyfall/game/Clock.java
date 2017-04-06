package com.iantmeyer.spyfall.game;

public class Clock {
    public boolean paused;
    public long endTime;
    public long timeRemaining;

    static Clock withEndTime(long endTimeMillis) {
        Clock clock = new Clock();
        clock.paused = false;
        clock.timeRemaining = -1;
        clock.endTime = endTimeMillis;
        return clock;
    }

    boolean pause() {
        if (paused) {
            return false;
        }
        timeRemaining = (endTime - System.currentTimeMillis()) / 1000L;
        paused = true;
        return true;
    }

    boolean resume() {
        if (!paused) {
            return false;
        }
        endTime = System.currentTimeMillis() + 1000L * timeRemaining;
        paused = false;
        return true;
    }
}