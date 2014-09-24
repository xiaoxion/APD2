package com.stratazima.weego.processes;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.item_trip, parent, false);

            holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.trips_imageView);
            holder.name = (TextView) view.findViewById(R.id.main_item_trip);
            holder.flight = (TextView) view.findViewById(R.id.main_item_flight);
            holder.activity = (TextView) view.findViewById(R.id.main_item_activity);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(daArrayList.get(position).get("tripName"));
        holder.flight.setText(daArrayList.get(position).get("tripFlight"));
        holder.activity.setText(daArrayList.get(position).get("activity"));

        return view;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView name;
        TextView flight;
        TextView activity;
    }
}
