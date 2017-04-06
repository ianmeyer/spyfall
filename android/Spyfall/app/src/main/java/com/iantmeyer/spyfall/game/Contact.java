package com.iantmeyer.spyfall.game;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.iantmeyer.spyfall.BuildConfig;
import com.iantmeyer.spyfall.R;
import com.iantmeyer.spyfall.sms.SmsCallback;

public class Contact {
    public final boolean isHost;
    public long id;
    public long contactId;
    public String iconUri = "";
    public String name = "";
    public String phone = "";
    public int type = -1;
    public boolean first;
    SmsCallback.SmsStatus status;

    public static Contact hostInstance() {
        Contact contact = new Contact(true);
        contact.name = "You";
        Uri uri = Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID + "/" + R.drawable.ic_person);
        contact.iconUri = uri.toString();
        return contact;
    }

    private Contact(boolean isHost) {
        this.isHost = isHost;
    }

    public Contact(Cursor cursor) {
        isHost = false;
        id = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
        contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
        iconUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
        name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
        type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
    }
}