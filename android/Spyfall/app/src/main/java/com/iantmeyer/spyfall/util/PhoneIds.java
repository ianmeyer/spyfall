package com.iantmeyer.spyfall.util;

import java.util.List;

public class PhoneIds {

    private final Settings mSettings;

    private final List<Long> mPhoneIdList;

    public PhoneIds(Settings settings) {
        mSettings = settings;
        mPhoneIdList = mSettings.getPhoneIds();
    }

    public List<Long> get() {
        return mPhoneIdList;
    }

    public boolean add(long id) {
        boolean result = mPhoneIdList.add(id);
        mSettings.setPhoneIds(mPhoneIdList);
        return result;
    }

    public boolean remove(long id) {
        boolean result = mPhoneIdList.remove(id);
        mSettings.setPhoneIds(mPhoneIdList);
        return result;
    }

    public long get(int idx) {
        return mPhoneIdList.get(idx);
    }

    public int size() {
        return mPhoneIdList.size();
    }
}