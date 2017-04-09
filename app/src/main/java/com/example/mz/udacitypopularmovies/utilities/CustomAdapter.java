package com.example.mz.udacitypopularmovies.utilities;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.mz.udacitypopularmovies.DetailActivity;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mateusz.zak on 09.04.2017.
 */
public class CustomAdapter<CustomEntry> extends ArrayAdapter<CustomEntry> {
    private DetailActivity detailActivity;
    private final String TAG = CustomAdapter.class.getSimpleName();
    private ArrayList<CustomEntry> mEntries;

    public CustomAdapter(DetailActivity detailActivity, ArrayList<CustomEntry> entries) {
        super(detailActivity, 0, entries);
        this.detailActivity = detailActivity;
        mEntries = entries;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CustomEntry entry = getItem(position);
        Log.i(TAG, "GetView called for position " + position);
        // Check if an existing view is being reused, otherwise inflate the view
        convertView = detailActivity.inflate(getContext(), convertView, parent, entry);
        // Return the completed view to render on screen
        return convertView;
    }

    public void setData(CustomEntry[] entryData) {
        Log.i(TAG, "before setData there is " + getCount() + " elements in a view");
        if (entryData == null) {
            mEntries.clear();
        } else {
            mEntries.addAll(Arrays.asList(entryData));
        }
        notifyDataSetChanged();
        Log.i(TAG, "after setData there is " + getCount() + " elements in a view");
    }
}
