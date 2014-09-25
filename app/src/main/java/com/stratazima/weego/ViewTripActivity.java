package com.stratazima.weego;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.stratazima.weego.processes.ActivityCustomList;
import com.stratazima.weego.processes.DataStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Esau on 9/11/2014.
 */
public class ViewTripActivity extends Activity {
    static final int ADD_ACTIVITY_REQUEST = 1;
    static final int VIEW_ACTIVITY_REQUEST = 2;
    JSONObject tripObject;
    DataStorage dataStorage;
    ListView viewActivityList;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_view);

        dataStorage = DataStorage.getInstance(this);
        position = getIntent().getIntExtra("position", 0);
        tripObject = dataStorage.onReadTrip(position);

        TextView tripName = (TextView) findViewById(R.id.view_trip_textview);
        try {
            tripName.setText(tripObject.getString("tripName"));
        } catch (JSONException e) {
            e.printStackTrace();
            tripName.setText("Error Reading Name");
        }

        viewActivityList = (ListView) findViewById(R.id.view_trip_listview);

        onButtonConnect();
        onListCreate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onListCreate();
    }

    public void onButtonConnect() {
        ImageButton imageButton = (ImageButton) findViewById(R.id.trip_imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addActivityIntent = new Intent(getApplicationContext(), ActivityActivity.class);
                addActivityIntent.putExtra("position", position);
                addActivityIntent.putExtra("listPosition", -1);
                startActivityForResult(addActivityIntent, ADD_ACTIVITY_REQUEST);
            }
        });
    }


    public void onListCreate() {
        JSONObject jsonObject = dataStorage.onReadTrip(position);
        ArrayList<JSONObject> myList = new ArrayList<JSONObject>();
        JSONArray jsonArray = null;

        try {
            jsonArray = jsonObject.getJSONArray("tripActivities");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    myList.add(jsonArray.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        String[] strings = new String[myList.size()];
        ActivityCustomList adapter = new ActivityCustomList(this, strings, myList);

        viewActivityList.setAdapter(adapter);
        viewActivityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position1, long id) {
                Intent viewActivityIntent = new Intent(getApplicationContext(), ActivityActivity.class);
                viewActivityIntent.putExtra("position", position);
                viewActivityIntent.putExtra("listPosition", position1);
                startActivityForResult(viewActivityIntent, VIEW_ACTIVITY_REQUEST);
            }
        });
    }
}
