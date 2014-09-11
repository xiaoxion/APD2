package com.stratazima.weego.processes;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.stratazima.weego.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Esau on 9/11/2014.
 */
public class TripsCustomList extends ArrayAdapter {
    private final Activity context;
    private final ArrayList<HashMap<String, String>> daArrayList;

    public TripsCustomList(Activity context, String[] length, ArrayList<HashMap<String, String>> daArrayList) {
        super(context, R.layout.item_trip, length);

        this.context = context;
        this.daArrayList = daArrayList;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.item_trip, null, true);

        TextView name = (TextView) rowView.findViewById(R.id.main_item_trip);
        TextView flight = (TextView) rowView.findViewById(R.id.main_item_flight);
        TextView activity = (TextView) rowView.findViewById(R.id.main_item_activity);

        name.setText(daArrayList.get(position).get("tripName"));
        flight.setText(daArrayList.get(position).get("tripFlight"));
        activity.setText(daArrayList.get(position).get("activity"));

        return rowView;
    }
}
