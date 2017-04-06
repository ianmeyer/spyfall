package com.iantmeyer.spyfall.fragment.setup;

import android.database.Cursor;
import android.net.Uri;

import com.iantmeyer.spyfall.BaseMvp;
import com.iantmeyer.spyfall.game.Contact;

interface SetupMvp {
    interface View extends BaseMvp.View<Presenter> {
        void setHeader(Contact contact);

        void reloadCursor();

        void showFab(boolean show);

        void setTimeLimit(long timeLimit);

        void showErrorMessage(String msg);

        void startGamePrompt(int numTexts);

        void enableStartButton(boolean enable);
    }

    interface Presenter extends BaseMvp.Presenter {
        Uri getPlayersUri();

        String[] getPlayersProjection();

        String getPlayersSelection();

        String[] getPlayersSelectionArgs();

        String getPlayersOrder();

        void removePlayer(Contact contact);

        void startGame();

        boolean reopenLastGame();

        void setTimeLimit(long seconds);

        long getTimeLimit();

        void setContacts(Cursor cursor);

        boolean lastGameAvailable();
    }
}