package com.stratazima.weego;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.stratazima.weego.processes.DataStorage;
import com.stratazima.weego.processes.TripsCustomList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class MainActivity extends Activity {
    static final int ADD_TRIP_REQUEST = 1;
    static final int ACTIVITY_RETURN = 2;
    DataStorage dataStorage;
    ListView mainListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataStorage = DataStorage.getInstance(this);
        mainListView = (ListView) findViewById(R.id.main_listview);

        onButtonConnect();
        onListCreate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            onListCreate();
        }
    }

    public void onListCreate() {
        JSONArray daJSONArray = dataStorage.onReadTripFiles();
        ArrayList<HashMap<String,String>> myList = new ArrayList<HashMap<String, String>>();

        if(daJSONArray != null) {
            String name;
            String flight;
            String daActivity;
            Date date = new Date();

            for (int i = 0; i < daJSONArray.length(); i++) {
                JSONObject tempObj = null;
                name = null;
                flight = null;
                daActivity = null;

                try {
                    tempObj =  daJSONArray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (tempObj != null) {
                    try {
                        name = tempObj.getString("tripName");

                        JSONArray tempArray = tempObj.getJSONArray("tripActivities");

                        boolean one = false;
                        boolean two = false;

                        for (i = 0; i < tempArray.length(); i++) {
                            JSONObject tempJSON = tempArray.getJSONObject(i);

                            long daOne = Long.parseLong(tempJSON.getString("epoch"));
                            long daSecond = date.getTime();

                            if (daOne > daSecond) {
                                if (tempJSON.getString("type").equals("flight")) {
                                    if (!one) flight = tempJSON.getString("flightFrom") + " to " + tempJSON.getString("flightTo");

                                    one = true;
                                } else {
                                    if (!two) {
                                        if (tempJSON.getString("type").equals("meeting")) {
                                            daActivity = tempJSON.getString("meetingName");
                                        } else if (tempJSON.getString("type").equals("food")) {
                                            daActivity = tempJSON.getString("restaurantName");
                                        }
                                    }

                                    two = true;
                                }

                                if (one && two) i = tempArray.length() + 1;
                            }
                        }

                        if (flight == null) flight = "No Upcoming Flight";
                        if (daActivity == null) daActivity = "No Upcoming Activities";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                HashMap<String,String> displayMap = new HashMap<String, String>();
                displayMap.put("tripName", name);
                displayMap.put("tripFlight", flight);
                displayMap.put("activity", daActivity);

                myList.add(displayMap);
            }
        }

        String[] strings = new String[myList.size()];
        TripsCustomList adapter = new TripsCustomList(this, strings, myList);

        mainListView.setAdapter(adapter);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent viewTripIntent = new Intent(getApplicationContext(), ViewTripActivity.class);
                viewTripIntent.putExtra("position", position);
                startActivityForResult(viewTripIntent, ACTIVITY_RETURN);
            }
        });
    }

    public void onButtonConnect() {
        ImageButton imageButton = (ImageButton) findViewById(R.id.main_imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addTripIntent = new Intent(getApplicationContext(), AddTripActivity.class);
                startActivityForResult(addTripIntent, ADD_TRIP_REQUEST);
            }
        });
    }
}
