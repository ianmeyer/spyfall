package com.iantmeyer.spyfall.fragment.search;

import android.net.Uri;

import com.iantmeyer.spyfall.BaseMvp;
import com.iantmeyer.spyfall.game.Contact;

interface SearchMvp {
    interface View extends BaseMvp.View<Presenter> {
        void searchAgain();

        void errorDuplicateContact();

        void backPressed();
    }

    interface Presenter extends BaseMvp.Presenter {
//        void onContactsPermission();

        Uri getSearchUri();

        String[] getSearchProjection();

        String getSearchSelection();

        String[] getSelectionArgs();

        String getSearchOrder();

        void search(String query);

        boolean addContact(Contact contact);
    }
}