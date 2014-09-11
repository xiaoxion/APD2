package com.stratazima.weego.processes;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.stratazima.weego.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Esau on 9/11/2014.
 */
public class ActivityCustomList extends ArrayAdapter {
    private final Activity context;
    private final ArrayList<HashMap<String, String>> daArrayList;

    public ActivityCustomList(Activity context, String[] length, ArrayList<HashMap<String, String>> daArrayList) {
        super(context, R.layout.item_trip, length);

        this.context = context;
        this.daArrayList = daArrayList;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.item_activity, null, true);

        return rowView;
    }
}
