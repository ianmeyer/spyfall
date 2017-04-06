package com.iantmeyer.spyfall.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iantmeyer.spyfall.R;
import com.iantmeyer.spyfall.game.Contact;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerItem implements Item<PlayerItem.ViewHolder, Contact> {

    @Override
    public View getView(View itemView, Contact contact, ItemClickListener<Contact> listener) {
        ViewHolder holder = new ViewHolder(itemView);
        bindViewHolder(holder, contact, listener);
        return holder.itemView;
    }

    @Override
    public void bindViewHolder(ViewHolder holder, final Contact contact, final ItemClickListener listener) {
//        holder.mPlayerIconIv.setImageResource(player.getRole().getIcon());
        holder.mNameTv.setText(contact.name);
        holder.mNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(contact, view);
            }
        });
        if(contact.first) {
            holder.mFirstTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon)
        protected ImageView mIconIv;
        @BindView(R.id.name)
        protected TextView mNameTv;
        @BindView(R.id.first)
        protected TextView mFirstTv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}