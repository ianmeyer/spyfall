package com.iantmeyer.spyfall.view;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

public interface CursorItem<ViewHolderType extends RecyclerView.ViewHolder, Type> extends Item<ViewHolderType, Type> {
    Type getItem(Cursor cursor);
}