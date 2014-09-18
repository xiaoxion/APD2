package com.stratazima.weego;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.stratazima.weego.processes.DataStorage;
import com.stratazima.weego.processes.TripsCustomList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity {
    static final int ADD_TRIP_REQUEST = 1;
    DataStorage dataStorage;
    ListView mainListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataStorage = DataStorage.getInstance(this);
        mainListView = (ListView) findViewById(R.id.main_listview);
        onListCreate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent addTripIntent = new Intent(this, AddTripActivity.class);
            startActivityForResult(addTripIntent, ADD_TRIP_REQUEST);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_TRIP_REQUEST) {
            if (resultCode == RESULT_OK) {
                onListCreate();
            }
        }
    }

    public void onListCreate() {
        JSONArray daJSONArray = dataStorage.onReadTripFiles();
        ArrayList<HashMap<String,String>> myList = new ArrayList<HashMap<String, String>>();

        if(daJSONArray != null) {
            String name;
            String flight;
            String daActivity;

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
                        flight = tempObj.getString("tripFlight");
                        daActivity = tempObj.getString("activity");

                        if (flight.equals("")) flight = "No Flight Planned";
                        if (daActivity.equals("")) daActivity = "No Activities Planned";
                    } catch (JSONException e) {
                        if (flight == null) flight = "No Flight Planned";
                        daActivity = "No Activities Planned";
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
                startActivity(viewTripIntent);
            }
        });
    }
}
