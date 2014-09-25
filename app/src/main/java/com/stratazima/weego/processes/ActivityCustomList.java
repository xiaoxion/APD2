package com.stratazima.weego.processes;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stratazima.weego.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Esau on 9/11/2014.
 */
public class ActivityCustomList extends ArrayAdapter {
    private final Activity context;
    private final ArrayList<JSONObject> daArrayList;
    private ArrayList<Boolean> temp = new ArrayList<Boolean>();

    public ActivityCustomList(Activity context, String[] length, ArrayList<JSONObject> daArrayList) {
        super(context, R.layout.item_trip, length);

        this.context = context;
        this.daArrayList = daArrayList;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.item_activity, parent, false);

            holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.item_activity_imageview);
            holder.mainTextView = (TextView) view.findViewById(R.id.item_activity_textview_main);
            holder.subOneTextView = (TextView) view.findViewById(R.id.item_activity_textview_sub1);
            holder.subTwoTextView = (TextView) view.findViewById(R.id.item_activity_textview_sub2);
            holder.sideOneTextView = (TextView) view.findViewById(R.id.item_activity_textview_side1);
            holder.sideTwoTextView = (TextView) view.findViewById(R.id.item_activity_textview_side2);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        JSONObject tempObj = daArrayList.get(position);

        holder.subOneTextView.setText("");
        holder.subTwoTextView.setText("");

        if (tempObj != null) {
            String type = null;

            try {
                type = tempObj.getString("type");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (type != null) {
                if (type.equals("meeting")) {
                    try {
                        view.setBackground(context.getResources().getDrawable(R.drawable.weego_yellow_item));
                        holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_meeting_icon));
                        holder.mainTextView.setText(tempObj.getString("meetingName"));
                        holder.subOneTextView.setText(tempObj.getString("meetingWith"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (type.equals("flight")) {
                    try {
                        view.setBackground(context.getResources().getDrawable(R.drawable.weego_red_item));
                        holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_flight_icon));
                        holder.mainTextView.setText(tempObj.getString("flightAirline"));
                        holder.subOneTextView.setText(tempObj.getString("flightConfirmation"));
                        holder.subTwoTextView.setText(tempObj.getString("flightNumber"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (type.equals("food")) {
                    try {
                        view.setBackground(context.getResources().getDrawable(R.drawable.weego_blue_item));
                        holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_food_icon));
                        holder.mainTextView.setText(tempObj.getString("restaurantName"));
                        holder.subOneTextView.setText(tempObj.getString("restaurantConfirmation"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                final Date date = new Date();

                try {
                    holder.sideOneTextView.setText(tempObj.getString("time"));
                    holder.sideTwoTextView.setText(tempObj.getString("date"));

                    long daOne = Long.parseLong(daArrayList.get(position).getString("epoch"));
                    long daSecond = date.getTime();

                    if (daOne > daSecond) {
                        temp.add(position, true);
                    } else {
                        temp.add(position, false);
                        view.setBackground(context.getResources().getDrawable(R.drawable.weego_disabled_item));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return temp.get(position);
    }

    static class ViewHolder {
        ImageView imageView;
        TextView mainTextView;
        TextView subOneTextView;
        TextView subTwoTextView;
        TextView sideOneTextView;
        TextView sideTwoTextView;
    }
}
