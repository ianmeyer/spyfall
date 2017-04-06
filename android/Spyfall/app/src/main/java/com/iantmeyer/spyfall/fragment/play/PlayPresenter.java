package com.iantmeyer.spyfall.fragment.play;

import com.iantmeyer.spyfall.App;
import com.iantmeyer.spyfall.game.Clock;
import com.iantmeyer.spyfall.game.Game;
import com.iantmeyer.spyfall.game.LocationManager;
import com.iantmeyer.spyfall.util.GameTimer;

import javax.inject.Inject;

public class PlayPresenter implements PlayMvp.Presenter {

    @Inject protected LocationManager mLocationManager;

    @Inject protected Game mGame;

    private PlayMvp.View mView;

    private GameTimer mTimer;

    PlayPresenter(PlayMvp.View view) {
        App.getComponent().inject(this);
        mView = view;
        mView.setContacts(mGame.getContactList());
        mView.setLocations(mLocationManager.getLocations());
        mView.setRole(mGame.getRole());
    }

    @Override
    public void onResume() {
        mView.hideRole();
        boolean locked = mGame.isRoleLocked();
        mView.disableRevealButton(locked);
        mView.toggleLock(locked);
        Clock clock = mGame.getClock();
        if (clock.paused) {
            mView.setTimer(clock.timeRemaining);
        } else {
            mTimer = GameTimer.newInstance(clock.endTime, mTimerCallback);
        }
        mView.toggleTimer(clock.paused);
    }

    @Override
    public void onPause() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mView.hideRole();
    }

    @Override
    public boolean onBackPressed() {
        mView.endGamePrompt();
        return true;
    }

    @Override
    public void onRevealPress() {
        if (mGame.isRoleLocked()) {
            return;
        }
        mView.revealRole();
    }

    @Override
    public void onRevealUnpress() {
        mView.hideRole();
    }

    @Override
    public void onRevealLockChanged(boolean locked) {
        mGame.lockRole(locked);
        mView.disableRevealButton(locked);
    }

    @Override
    public void onPauseChanged(boolean paused) {
        if (paused) {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
            mGame.pauseTimer();
        } else {
            mGame.restartTimer();
            mTimer = GameTimer.newInstance(mGame.getClock().endTime, mTimerCallback);
        }
        mView.toggleTimer(paused);
    }

    private GameTimer.Callback mTimerCallback = new GameTimer.Callback() {
        @Override
        public void onTimeChange(long seconds) {
            mView.setTimer(seconds);
        }

        @Override
        public void onComplete() {
        }
    };
}