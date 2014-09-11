package com.stratazima.weego;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.stratazima.weego.processes.DataStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Esau on 9/10/2014.
 */
public class AddTripActivity extends Activity {
    //private ImageView tripImage;
    private EditText tripName;
    private EditText tripLocation;
    private EditText tripNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_add);

        tripName = (EditText) findViewById(R.id.add_trip_edittext);
        tripLocation = (EditText) findViewById(R.id.add_trip_edittext2);
        tripNotes = (EditText) findViewById(R.id.add_trip_edittext3);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            if (tripName.getText().toString().equals("") || tripLocation.getText().toString().equals("")) {
                if (tripName.getText().toString().equals("")) tripName.setError("Enter Name");
                if (tripLocation.getText().toString().equals("")) tripLocation.setError("Enter Location");
                return true;
            } else {
                DataStorage dataStorage = DataStorage.getInstance(this);

                Date date = new Date();
                String string = date.getTime() + ".json";
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("tripName", tripName.getText().toString());
                    jsonObject.put("tripLocation", tripLocation.getText().toString());
                    jsonObject.put("tripNotes", tripNotes.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dataStorage.onWriteFile(jsonObject, string);
                setResult(RESULT_OK);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
