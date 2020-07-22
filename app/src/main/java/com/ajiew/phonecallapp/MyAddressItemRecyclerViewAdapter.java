package com.ajiew.phonecallapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ajiew.phonecallapp.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyAddressItemRecyclerViewAdapter extends RecyclerView.Adapter<MyAddressItemRecyclerViewAdapter.AddressViewHolder> {

    private List<Address> mValues;
    private ItemLongClickListener itemLongClickListener;

    public MyAddressItemRecyclerViewAdapter() {

    }

    public void setmValues(List<Address> mValues) {
        this.mValues = mValues;
        notifyDataSetChanged();
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_address_list_item, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AddressViewHolder holder, int position) {
        Address address = mValues.get(position);
        holder.mContentView.setText(address.getName());
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (itemLongClickListener != null) {
                    return itemLongClickListener.onLongClickListener(v, holder.getAdapterPosition());
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues == null ? 0 : mValues.size();
    }

    public ItemLongClickListener getItemLongClickListener() {
        return itemLongClickListener;
    }

    public void setItemLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;

        public AddressViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}