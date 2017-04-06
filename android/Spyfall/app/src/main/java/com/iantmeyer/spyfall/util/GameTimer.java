package com.iantmeyer.spyfall.util;

import android.os.CountDownTimer;

public class GameTimer extends CountDownTimer {

    private Callback mTimerCallback;

    public interface Callback {
        void onTimeChange(long seconds);

        void onComplete();
    }

    public static GameTimer newInstance(long endTime, Callback timerCallback) {
        long currentTime = System.currentTimeMillis();
        long timerLength = endTime - currentTime;
        GameTimer gameTimer = new GameTimer(timerLength, timerCallback);
        gameTimer.start();
        return gameTimer;
    }

    private GameTimer(long millisInFuture, Callback timerCallback) {
        super(millisInFuture, 1000);
        mTimerCallback = timerCallback;
    }

    @Override
    public void onTick(long millisRemaining) {
        long secondsRemaining = millisRemaining / 1000L;
        mTimerCallback.onTimeChange(secondsRemaining);
    }

    @Override
    public void onFinish() {
        mTimerCallback.onComplete();
    }
}