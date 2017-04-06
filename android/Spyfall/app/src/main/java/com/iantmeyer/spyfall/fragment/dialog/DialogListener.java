package com.iantmeyer.spyfall.fragment.dialog;

public interface DialogListener {
    void onConfirm(String tag);

    void onDeny(String tag);
}