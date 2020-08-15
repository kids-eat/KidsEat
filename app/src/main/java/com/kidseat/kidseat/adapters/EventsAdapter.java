package com.kidseat.kidseat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kidseat.kidseat.R;
import com.kidseat.kidseat.models.Event;

import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.Query;


public class EventsAdapter extends FirestoreAdapter<EventsAdapter.ViewHolder> {
    // Populates item event
    public interface OnEventSelectedListener {
        void onEventSelected(DocumentSnapshot event);
    }

    private OnEventSelectedListener mListener;

    public EventsAdapter(Query query, OnEventSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_event, parent, false));  // ViewHolder holds the reference to the id of the view resource (and place of item event)
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvEventAddress;
        TextView tvEventName;
        TextView tvEventDate;
        TextView tvEventTime;
        ImageView ivItemImage;

        public ViewHolder(View itemView) {
            super(itemView);
            // Access Item Event UI widgets
            tvEventAddress = itemView.findViewById(R.id.tvEventAddress);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventDate = itemView.findViewById(R.id.tvDate);
            tvEventTime = itemView.findViewById(R.id.tvTime);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnEventSelectedListener listener) {
            // bind each piece of data to its spot on item event
            Event event = snapshot.toObject(Event.class);   // Event model

            tvEventName.setText(event.getName());
            tvEventDate.setText(event.getDate());
            tvEventTime.setText(event.getTime());
            tvEventAddress.setText(event.getAddress());

            // Using glide library to display an image
            Glide.with(ivItemImage.getContext()).load(event.getImage()).into(ivItemImage);

            // On Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onEventSelected(snapshot);  // passes Document Snapshot to OrganizerMainActivity and TimelineFragment
                    }
                }
            });
        }

    }

}
