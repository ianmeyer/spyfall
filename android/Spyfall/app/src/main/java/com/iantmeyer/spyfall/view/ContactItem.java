package com.iantmeyer.spyfall.view;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iantmeyer.spyfall.R;
import com.iantmeyer.spyfall.game.Contact;
import com.iantmeyer.spyfall.util.PhoneUtil;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactItem implements CursorItem<ContactItem.ViewHolder, Contact> {

    private boolean mRemovable = false;

    public ContactItem() {
    }

    public ContactItem(boolean removable) {
        mRemovable = removable;
    }

    @Override
    public View getView(View itemView, Contact contact, ItemClickListener<Contact> listener) {
        ViewHolder holder = new ViewHolder(itemView);
        bindViewHolder(holder, contact, listener);
        return holder.itemView;
    }

    @Override
    public void bindViewHolder(ViewHolder holder, final Contact contact, final ItemClickListener listener) {
        Context context = holder.itemView.getContext();
        Picasso
                .with(context)
                .load(contact.iconUri)
                .placeholder(R.drawable.ic_person)
                .into(holder.mIconIv);
        holder.mNameTv.setText(contact.name);
        if (contact.phone != null) {
            String phoneNumber = PhoneUtil.displayNumber(context, contact);
            holder.mAddressTv.setText(phoneNumber);
        }
        if (!contact.isHost) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(contact, view);
                }
            });
        }
        if (mRemovable && !contact.isHost) {
            holder.mRemoveIv.setVisibility(View.VISIBLE);
            holder.mRemoveIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(contact, view);
                }
            });
        } else {
            holder.mRemoveIv.setVisibility(View.GONE);
        }

    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public Contact getItem(Cursor cursor) {
        return new Contact(cursor);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon)
        protected ImageView mIconIv;
        @BindView(R.id.name)
        protected TextView mNameTv;
        @BindView(R.id.address)
        protected TextView mAddressTv;
        @BindView(R.id.remove)
        protected ImageView mRemoveIv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}