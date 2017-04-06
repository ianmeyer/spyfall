package com.iantmeyer.spyfall.fragment.setup;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.iantmeyer.spyfall.App;
import com.iantmeyer.spyfall.game.Contact;
import com.iantmeyer.spyfall.game.Game;
import com.iantmeyer.spyfall.util.PhoneIds;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SetupPresenter implements SetupMvp.Presenter {

    @Inject Game mGame;

    @Inject PhoneIds mPhoneIds;

    private SetupMvp.View mView;

    private List<Contact> mContactList = new ArrayList<>();

    SetupPresenter(SetupMvp.View view) {
        App.getComponent().inject(this);
        mView = view;
        mView.setHeader(Contact.hostInstance());
    }

    @Override
    public void onResume() {
        mView.showFab(true);
        mView.setTimeLimit(mGame.getSetupTimeLimit());
    }

    @Override
    public void onPause() {
        mView.showFab(false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public Uri getPlayersUri() {
        return ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    }

    @Override
    public String[] getPlayersProjection() {
        return new String[]{
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
        };
    }

    @Override
    public String getPlayersSelection() {
        String selection = ContactsContract.CommonDataKinds.Phone._ID + " IN (";
        for (int idx = 0; idx < mPhoneIds.size(); idx++) {
            if (idx > 0) {
                selection += ",";
            }
            selection += "?";
        }
        selection += ")";
        return selection;
    }

    @Override
    public String[] getPlayersSelectionArgs() {
        String[] selectionArgs = new String[mPhoneIds.size()];
        for (int idx = 0; idx < mPhoneIds.size(); idx++) {
            selectionArgs[idx] = String.valueOf(mPhoneIds.get(idx));
        }
        return selectionArgs;
    }

    @Override
    public String getPlayersOrder() {
        if (mPhoneIds.size() == 0) {
            return null;
        }
        String order = "CASE " + ContactsContract.CommonDataKinds.Phone._ID;
        for (int idx = 0; idx < mPhoneIds.size(); idx++) {
            order += " WHEN " + mPhoneIds.get(idx) + " THEN " + idx;
        }
        order += " END";
        return order;
    }

    @Override
    public void removePlayer(Contact contact) {
        if (mPhoneIds.remove(contact.id)) {
            mView.reloadCursor();
        }
    }

    @Override
    public void startGame() {
        mGame.setOnDeckContactList(mContactList);
        mView.startGamePrompt(mContactList.size());
    }

    @Override
    public boolean reopenLastGame() {
        return mGame.reopenLastGame();
    }

    @Override
    public void setTimeLimit(long seconds) {
        mGame.setSetupTimeLimit(seconds);
        mView.setTimeLimit(seconds);
    }

    @Override
    public long getTimeLimit() {
        return mGame.getSetupTimeLimit();
    }

    @Override
    public void setContacts(Cursor cursor) {
        mContactList.clear();
        if (cursor != null) {
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                mContactList.add(new Contact(cursor));
            }
        }
        mView.enableStartButton(mContactList.size() >= 2);
        // Cleanup any ids that no longer have a contact entry
        List<Long> removeids = new ArrayList<>();
        removeids.addAll(mPhoneIds.get());
        for (Contact contact : mContactList) {
            removeids.remove(contact.id);
        }
        for (Long removeId : removeids) {
            mPhoneIds.remove(removeId);
        }
    }

    @Override
    public boolean lastGameAvailable() {
        return mGame.getRole() != null;
    }
}