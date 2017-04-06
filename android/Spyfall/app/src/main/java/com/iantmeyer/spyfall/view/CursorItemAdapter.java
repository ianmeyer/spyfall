package com.iantmeyer.spyfall.view;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class CursorItemAdapter<ItemViewType extends CursorItem, Type>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Type mHeaderItem;
    protected Cursor mCursor;
    private ItemViewType mViewType;
    private final ItemClickListener<Type> mListener;
    private View mEmptyView;

    public CursorItemAdapter(ItemViewType viewType, Cursor cursor, ItemClickListener listener) {
        this(viewType, listener);
        updateCursor(cursor);
    }

    public CursorItemAdapter(ItemViewType viewType, ItemClickListener listener) {
        setItemViewType(viewType);
        mListener = listener;
    }

    public void setItemViewType(ItemViewType viewType) {
        mViewType = viewType;
    }

    public void setHeader(Type item) {
        mHeaderItem = item;
    }

    public void updateCursor(Cursor cursor) {
        if (mEmptyView != null) {
            if (cursor == null || cursor.getCount() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        }
        mCursor = cursor;
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
        if(mHeaderItem != null) {
            if(position == 0) {
                return mHeaderItem;
            }
            position--;
        }
        if (mCursor == null || position >= getItemCount()) {
            return null;
        }
        mCursor.moveToPosition(position);
        return (Type) mViewType.getItem(mCursor);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mCursor != null) {
            count += mCursor.getCount();
        }
        if(mHeaderItem != null) {
            count++;
        }
        return count;
    }
}