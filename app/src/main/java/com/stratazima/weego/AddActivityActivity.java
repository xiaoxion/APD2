package com.stratazima.weego;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.stratazima.weego.processes.DataStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Esau on 9/15/2014.
 */
public class AddActivityActivity extends Activity implements ActionBar.OnNavigationListener {
    private static final String STATE_SELECTED_VIEW_ITEM = "selected_navigation_item";
    Button setDateButton;
    Button setTimeButton;
    DataStorage dataStorage;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_add);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        actionBar.setListNavigationCallbacks(
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[] {
                                getString(R.string.meeting),
                                getString(R.string.flight),
                                getString(R.string.food),
                        }),
                this);

        position = getIntent().getIntExtra("position", 0);
        setDateButton = (Button) findViewById(R.id.select_date);
        setTimeButton = (Button) findViewById(R.id.select_time);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_VIEW_ITEM)) {
            getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_VIEW_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_VIEW_ITEM, getActionBar().getSelectedNavigationIndex());
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
            FragmentManager fragmentManager = getFragmentManager();
            dataStorage = DataStorage.getInstance(this);
            JSONObject tempJSON = dataStorage.onReadTrip(position);
            JSONObject data = new JSONObject();

            switch (getActionBar().getSelectedNavigationIndex()) {
                case 0:
                    MeetingFragment meetingFragment = (MeetingFragment) fragmentManager.findFragmentByTag("meeting");
                    try {
                        data = meetingFragment.onGetData();
                        data.put("type", "meeting");
                        data.put("time", getEpochTime());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    break;
                case 1:
                    FlightFragment flightFragment = (FlightFragment) fragmentManager.findFragmentByTag("flight");
                    try {
                        data = flightFragment.onGetData();
                        data.put("type", "flight");
                        data.put("time", getEpochTime());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    break;
                case 2:
                    FoodFragment foodFragment = (FoodFragment) fragmentManager.findFragmentByTag("food");
                    try {
                        data = foodFragment.onGetData();
                        data.put("type", "food");
                        data.put("time", getEpochTime());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    break;
            }

            try {
                tempJSON.getJSONArray("tripActivities").put(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dataStorage.onRewriteFile(tempJSON, position);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        switch (position) {
            case 0:
                getFragmentManager().beginTransaction().replace(R.id.container, MeetingFragment.newInstance(), "meeting").commit();
                break;
            case 1:
                getFragmentManager().beginTransaction().replace(R.id.container, FlightFragment.newInstance(), "flight").commit();
                break;
            case 2:
                getFragmentManager().beginTransaction().replace(R.id.container, FoodFragment.newInstance(), "food").commit();
                break;
        }

        return true;
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void setTimePickerDialog(int hourOfDay, int minute) {
        String hourDay = String.valueOf(hourOfDay);
        String min = String.valueOf(minute);

        if (hourDay.length() == 1) hourDay = "0" + hourDay;
        if (min.length() == 1) min = "0" + min;
        if (min.length() == 0) min = "00";

        setTimeButton.setText(hourDay + ":" + min);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void setDatePickerDialog(int year, int month, int day) {
        String m = String.valueOf(month+1);
        String d = String.valueOf(day);

        if (m.length() == 1) m = "0" + m;
        if (d.length() == 1) d = "0" + d;

        setDateButton.setText(m + "/" + d + "/" + year);
    }

    public String getEpochTime() throws ParseException {
        String fullDate = setDateButton.getText().toString() + " " + setTimeButton.getText().toString();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/dd/yyyy hh:mm");
        Date date = simpleDateFormat.parse(fullDate);
        long epoch = date.getTime();

        return String.valueOf(epoch);
    }

    /**
     * Begin Fragments Implementation
     */

    public static class MeetingFragment extends Fragment {
        public static MeetingFragment newInstance() {
            return new MeetingFragment();
        }

        public MeetingFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_meeting_add, container, false);
            return rootView;
        }

        public JSONObject onGetData () {
            JSONObject tempJSON = new JSONObject();

            EditText meetingName = (EditText) getView().findViewById(R.id.activity_add_edittext);
            EditText meetingWith = (EditText) getView().findViewById(R.id.activity_add_edittext2);
            EditText meetingNotes = (EditText) getView().findViewById(R.id.activity_add_edittext3);

            try {
                tempJSON.put("meetingName", meetingName.getText());
                tempJSON.put("meetingWith", meetingWith.getText());
                tempJSON.put("meetingNotes", meetingNotes.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return tempJSON;
        }
    }

    public static class FlightFragment extends Fragment {
        public static FlightFragment newInstance() {
            return new FlightFragment();
        }

        public FlightFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_flight_add, container, false);
            return rootView;
        }

        public JSONObject onGetData () {
            JSONObject tempJSON = new JSONObject();

            EditText flightNumber = (EditText) getView().findViewById(R.id.flight_add_edittext);
            EditText flightFrom = (EditText) getView().findViewById(R.id.flight_add_edittext2);
            EditText flightTo = (EditText) getView().findViewById(R.id.flight_add_edittext3);
            EditText flightAirline = (EditText) getView().findViewById(R.id.flight_add_edittext4);
            EditText flightConfirmation = (EditText) getView().findViewById(R.id.flight_add_edittext5);

            try {
                tempJSON.put("flightNumber", flightNumber.getText());
                tempJSON.put("flightFrom", flightFrom.getText());
                tempJSON.put("flightTo", flightTo.getText());
                tempJSON.put("flightAirline", flightAirline.getText());
                tempJSON.put("flightConfirmation", flightConfirmation.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return tempJSON;
        }
    }

    public static class FoodFragment extends Fragment {
        public static FoodFragment newInstance() {
            return new FoodFragment();
        }

        public FoodFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_food_add, container, false);
            return rootView;
        }

        public JSONObject onGetData () {
            JSONObject tempJSON = new JSONObject();

            EditText restaurantName = (EditText) getView().findViewById(R.id.food_edittext);
            EditText restaurantConfirmation = (EditText) getView().findViewById(R.id.food_edittext2);
            EditText restaurantNotes = (EditText) getView().findViewById(R.id.food_edittext3);

            try {
                tempJSON.put("restaurantName", restaurantName.getText());
                tempJSON.put("restaurantConfirmation", restaurantConfirmation.getText());
                tempJSON.put("restaurantNotes", restaurantNotes.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return tempJSON;
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            return new TimePickerDialog(getActivity(), this, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            ((AddActivityActivity)getActivity()).setTimePickerDialog(hourOfDay, minute);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            return new DatePickerDialog(getActivity(), this, c.get(Calendar.YEAR), c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            ((AddActivityActivity)getActivity()).setDatePickerDialog(year, month, day);
        }
    }
}
