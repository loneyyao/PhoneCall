package com.ajiew.phonecallapp.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ajiew.phonecallapp.R;
import com.ajiew.phonecallapp.db.Address;
import com.ajiew.phonecallapp.db.DisturbType;
import com.ajiew.phonecallapp.widget.ItemLongClickListener;

import java.util.List;


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
                .inflate(R.layout.fragment_call_address_list_item, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AddressViewHolder holder, int position) {
        Address address = mValues.get(position);
        holder.disturb_type.setText(DisturbType.value(address.getType()).getMsg());
        holder.disturb_content.setText(address.getName());
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
        public final TextView disturb_type;
        public final TextView disturb_content;

        public AddressViewHolder(View view) {
            super(view);
            mView = view;
            disturb_type = (TextView) view.findViewById(R.id.address_type);
            disturb_content = (TextView) view.findViewById(R.id.address_content);
        }
    }
}