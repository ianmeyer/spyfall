package com.iantmeyer.spyfall.view;

import android.view.View;

public interface ItemClickListener<Type> {
    void onItemClick(Type type, View view);
}
