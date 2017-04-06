package com.iantmeyer.spyfall.fragment.search;

import android.net.Uri;
import android.provider.ContactsContract;

import com.iantmeyer.spyfall.App;
import com.iantmeyer.spyfall.game.Contact;
import com.iantmeyer.spyfall.util.PhoneIds;

import javax.inject.Inject;

public class SearchPresenter implements SearchMvp.Presenter {

    @Inject PhoneIds mPhoneIds;

    private final SearchMvp.View mView;

    private String mQuery = "";

    SearchPresenter(SearchMvp.View view) {
        App.getComponent().inject(this);
        mView = view;
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public boolean onBackPressed() {
        mView.backPressed();
        return false;
    }

    @Override
    public Uri getSearchUri() {
        return ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    }

    @Override
    public String[] getSearchProjection() {
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
    public String getSearchSelection() {
        if (mQuery != null && !mQuery.equals("")) {
            return "(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?"
                    + " OR " + ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER + " LIKE ?)";
        }
        return null;
    }

    @Override
    public String[] getSelectionArgs() {
        if (mQuery != null && !mQuery.equals("")) {
            String phoneQuery = mQuery.replaceAll("[ -.()]", "");
            return new String[]{"%" + mQuery + "%", "%" + phoneQuery + "%"};
        }
        return null;
    }

    @Override
    public String getSearchOrder() {
        return ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
    }

    @Override
    public void search(String query) {
        mQuery = query;
        mView.searchAgain();
    }

    @Override
    public boolean addContact(Contact contact) {
        if (mPhoneIds.get().contains(contact.id)) {
            mView.errorDuplicateContact();
            return false;
        }
        return mPhoneIds.add(contact.id);
    }
}