package com.iantmeyer.spyfall.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iantmeyer.spyfall.R;
import com.iantmeyer.spyfall.game.Location;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationItem implements Item<LocationItem.ViewHolder, Location> {

    @Override
    public View getView(View itemView, Location location, ItemClickListener<Location> listener) {
        ViewHolder holder = new ViewHolder(itemView);
        bindViewHolder(holder, location, listener);
        return holder.itemView;
    }

    @Override
    public void bindViewHolder(ViewHolder holder, final Location location, final ItemClickListener listener) {
        holder.mNameTv.setText(location.title);
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon)
        protected ImageView mIconIv;
        @BindView(R.id.name)
        protected TextView mNameTv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}