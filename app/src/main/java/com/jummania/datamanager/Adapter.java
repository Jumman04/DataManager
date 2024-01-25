package com.jummania.datamanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * The Adapter class is responsible for binding SimpleData objects to RecyclerView items.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private final List<SimpleData> dataList;

    /**
     * Constructs an Adapter with the provided list of SimpleData objects.
     *
     * @param dataList The list of SimpleData objects.
     */
    public Adapter(List<SimpleData> dataList) {
        this.dataList = dataList;
    }

    /**
     * Creates a new ViewHolder by inflating the layout for each item view.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The type of the view.
     * @return A new ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new ViewHolder(itemView);
    }

    /**
     * Binds data to the ViewHolder for the given position.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position of the item in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SimpleData simpleData = dataList.get(position);
        int simpleInt = simpleData.getSimpleInt();
        String simpleString = simpleData.getSimpleString();
        holder.textView.setText(String.format("Position: %s, String: %s", simpleInt, simpleString));
    }

    /**
     * Gets the total number of items in the data list.
     *
     * @return The total number of items.
     */
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * The ViewHolder class represents each item view in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView textView;

        /**
         * Constructs a ViewHolder with the provided item view.
         *
         * @param itemView The item view.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
