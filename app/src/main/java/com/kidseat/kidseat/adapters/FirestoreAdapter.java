package com.kidseat.kidseat.adapters;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public abstract class FirestoreAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>
        implements EventListener<QuerySnapshot> {
    // Listens to any changes in the database in real-time

    private static final String TAG = "FirestoreAdapter";

    private Query query;
    private ListenerRegistration registration;

    private ArrayList<DocumentSnapshot> allEvents = new ArrayList<>();

    public FirestoreAdapter(Query query) {
        this.query = query;
    }

    public void startListening() {
        if (query != null && registration == null) {
            registration = query.addSnapshotListener(this);
        }
    }

    public void stopListening() {
        if (registration != null) {
            registration.remove();
            registration = null;
        }
        allEvents.clear();
        notifyDataSetChanged();
    }

    public void setQuery(Query query) {
        // Stop listening
        stopListening();

        // Clear existing data
        allEvents.clear();
        notifyDataSetChanged();

        // Listen to new query
        this.query = query;
        startListening();
    }

    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

        // Handle errors
        if (e != null) {
            Log.w(TAG, "onEvent:error", e);
            return;
        }

        // Dispatch the event
        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
            switch (change.getType()) {
                case ADDED:
                    onDocumentAdded(change);
                    break;
                case MODIFIED:
                    onDocumentModified(change);
                    break;
                case REMOVED:
                    onDocumentRemoved(change);
                    break;
            }
        }
        onDataChanged();
    }


    protected void onDocumentAdded(DocumentChange change) {
        allEvents.add(change.getNewIndex(), change.getDocument());
        notifyItemInserted(change.getNewIndex());
    }

    protected void onDocumentModified(DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            // Item changed but remained in same position
            allEvents.set(change.getOldIndex(), change.getDocument());
            notifyItemChanged(change.getOldIndex());
        } else {
            // Item changed and changed position
            allEvents.remove(change.getOldIndex());
            allEvents.add(change.getNewIndex(), change.getDocument());
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
    }

    protected void onDocumentRemoved(DocumentChange change) {
        allEvents.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
    }

    @Override
    public int getItemCount() {
        return allEvents.size();
    }
    protected DocumentSnapshot getSnapshot(int index) {
        return allEvents.get(index);
    }
    protected void onError(FirebaseFirestoreException e) {}
    protected void onDataChanged() {}
}
