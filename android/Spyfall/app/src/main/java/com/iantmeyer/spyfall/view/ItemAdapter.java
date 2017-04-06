package com.iantmeyer.spyfall.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ItemAdapter<ItemViewType extends Item, Type>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Type mHeaderItem;
    protected List<Type> mData;
    protected ItemViewType mViewType;
    protected final ItemClickListener<Type> mListener;
    protected View mEmptyView;

    public ItemAdapter(ItemViewType viewType, List<Type> data, ItemClickListener listener) {
        this(viewType, listener);
        updateList(data);
    }

    public ItemAdapter(ItemViewType viewType, ItemClickListener listener) {
        setItemViewType(viewType);
        mListener = listener;
    }

    public void setItemViewType(ItemViewType viewType) {
        mViewType = viewType;
    }

    public void setHeader(Type item) {
        mHeaderItem = item;
    }

    public void updateList(List<Type> data) {
        if (mEmptyView != null) {
            if (data.size() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        }
        mData = data;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mViewType.createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Type item = getItem(position);
        mViewType.bindViewHolder(holder, item, mListener);
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }

    protected Type getItem(int position) {
        if (mHeaderItem != null) {
            if (position == 0) {
                return mHeaderItem;
            }
            position--;
        }
        if (mData == null || position >= mData.size()) {
            return null;
        }
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        int count = mData == null ? 0 : mData.size();
        return mHeaderItem == null ? count : count + 1;
    }
}