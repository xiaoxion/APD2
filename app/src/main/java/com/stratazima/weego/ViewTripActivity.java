package com.stratazima.weego;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.stratazima.weego.processes.ActivityCustomList;
import com.stratazima.weego.processes.DataStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Esau on 9/11/2014.
 */
public class ViewTripActivity extends Activity {
    JSONObject tripObject;
    DataStorage dataStorage;
    ListView viewActivityList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_view);

        dataStorage = DataStorage.getInstance(this);
        tripObject = dataStorage.onReadTrip(getIntent().getIntExtra("position", 0));

        TextView tripName = (TextView) findViewById(R.id.view_trip_textview);
        try {
            tripName.setText(tripObject.getString("tripName"));
        } catch (JSONException e) {
            e.printStackTrace();
            tripName.setText("Error Reading Name");
        }

        viewActivityList = (ListView) findViewById(R.id.view_trip_listview);
        onListCreate();
    }

    public void onListCreate() {
        ArrayList<HashMap<String,String>> myList = new ArrayList<HashMap<String, String>>();

            String name = "Sample Activity";

            for (int i = 0; i < 5; i++) {
                HashMap<String,String> displayMap = new HashMap<String, String>();
                displayMap.put("trip", name);

                myList.add(displayMap);
            }

        String[] strings = new String[myList.size()];
        ActivityCustomList adapter = new ActivityCustomList(this, strings, myList);

        viewActivityList.setAdapter(adapter);
    }
}
