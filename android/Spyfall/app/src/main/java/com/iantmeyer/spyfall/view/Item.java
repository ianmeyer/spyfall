package com.iantmeyer.spyfall.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public interface Item<ViewHolderType extends RecyclerView.ViewHolder, Type> {
    View getView(View itemView, Type type, ItemClickListener<Type> listener);

    ViewHolderType createViewHolder(ViewGroup parent);

    void bindViewHolder(ViewHolderType holder, Type type, ItemClickListener<Type> listener);
}
