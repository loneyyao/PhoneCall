package com.ajiew.phonecallapp.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ajiew.phonecallapp.widget.ItemClickListener;
import com.ajiew.phonecallapp.widget.ItemLongClickListener;
import com.ajiew.phonecallapp.R;
import com.ajiew.phonecallapp.db.CallLog;

import java.util.List;

public class MyBadCallItemRecyclerViewAdapter extends RecyclerView.Adapter<MyBadCallItemRecyclerViewAdapter.CallLogViewHolder> {

    private List<CallLog> mValues;
    private ItemLongClickListener itemLongClickListener;
    private ItemClickListener itemClickListener;


    public MyBadCallItemRecyclerViewAdapter() {

    }

    public void setmValues(List<CallLog> mValues) {
        this.mValues = mValues;
        notifyDataSetChanged();
    }

    @Override
    public MyBadCallItemRecyclerViewAdapter.CallLogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_call_log_list_item, parent, false);
        return new MyBadCallItemRecyclerViewAdapter.CallLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyBadCallItemRecyclerViewAdapter.CallLogViewHolder holder, int position) {
        CallLog callLog = mValues.get(position);
        String callTime = callLog.getCallTime();
        holder.call.setText(callLog.getCall() + "    " + (callTime == null ? "" : callTime));
        holder.address.setText(callLog.getAddress());
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (itemLongClickListener != null) {
                    return itemLongClickListener.onLongClickListener(v, holder.getAdapterPosition());
                }
                return false;
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, holder.getAdapterPosition());
                }

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

    public ItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public static class CallLogViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView call;
        public final TextView address;

        public CallLogViewHolder(View view) {
            super(view);
            mView = view;
            call = (TextView) view.findViewById(R.id.call);
            address = (TextView) view.findViewById(R.id.address);
        }
    }
}