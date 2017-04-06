package com.iantmeyer.spyfall.fragment.play;

import com.iantmeyer.spyfall.BaseMvp;
import com.iantmeyer.spyfall.game.Contact;
import com.iantmeyer.spyfall.game.Location;
import com.iantmeyer.spyfall.game.Role;

import java.util.List;

interface PlayMvp {
    interface View extends BaseMvp.View<Presenter> {
        void setRole(Role role);

        void setContacts(List<Contact> players);

        void setLocations(List<Location> locations);

        void setTimer(long seconds);

        void revealRole();

        void hideRole();

        void disableRevealButton(boolean disabled);

        void toggleLock(boolean locked);

        void toggleTimer(boolean paused);

        void endGamePrompt();
    }

    interface Presenter extends BaseMvp.Presenter {
        void onResume();

        void onPause();

        void onRevealPress();

        void onRevealUnpress();

        void onRevealLockChanged(boolean locked);

        void onPauseChanged(boolean paused);
    }
}