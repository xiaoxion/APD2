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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Esau on 9/15/2014.
 */
public class ActivityActivity extends Activity implements ActionBar.OnNavigationListener {
    Button setDateButton;
    Button setTimeButton;
    DataStorage dataStorage;
    JSONObject editJSON;
    String type = null;
    private Menu daOptionsMenu;
    boolean menuOptions;
    int position;
    int position2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);

        position = getIntent().getIntExtra("position", 0);
        position2 = getIntent().getIntExtra("listPosition", -1);
        setDateButton = (Button) findViewById(R.id.select_date);
        setTimeButton = (Button) findViewById(R.id.select_time);

        if (getIntent().getIntExtra("listPosition", -1) >= 0) {
            dataStorage = DataStorage.getInstance(this);
            JSONObject jsonObject = dataStorage.onReadTrip(position);
            try {
                JSONArray daJSONArray = jsonObject.getJSONArray("tripActivities");
                editJSON = daJSONArray.getJSONObject(position2);
                type = editJSON.getString("type");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (type.equals("meeting")) {
                getFragmentManager().beginTransaction().replace(R.id.container, MeetingFragment.newInstance(), "meeting").commit();
            } else if (type.equals("flight")) {
                getFragmentManager().beginTransaction().replace(R.id.container, FlightFragment.newInstance(), "flight").commit();
            } else if (type.equals("food")) {
                getFragmentManager().beginTransaction().replace(R.id.container, FoodFragment.newInstance(), "food").commit();
            }

            setTimeButton.setEnabled(false);
            setDateButton.setEnabled(false);

            menuOptions = true;
        } else {
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
            menuOptions = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        daOptionsMenu = menu;
        if (menuOptions) {
            menu.findItem(R.id.action_edit).setVisible(true);
            menu.findItem(R.id.action_save).setVisible(false);

            try {
                setTimeButton.setText(editJSON.getString("time"));
                setDateButton.setText(editJSON.getString("date"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (type.equals("meeting")) {
                MeetingFragment meetingFragment = (MeetingFragment) getFragmentManager().findFragmentByTag("meeting");
                meetingFragment.onSetData(editJSON);
            } else if (type.equals("flight")) {
                FlightFragment flightFragment = (FlightFragment) getFragmentManager().findFragmentByTag("flight");
                flightFragment.onSetData(editJSON);
            } else if (type.equals("food")) {
                FoodFragment foodFragment = (FoodFragment) getFragmentManager().findFragmentByTag("food");
                foodFragment.onSetData(editJSON);
            }
        }
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
            boolean error = false;

            if (menuOptions) {
                if (type.equals("meeting")) {
                    MeetingFragment meetingFragment = (MeetingFragment) getFragmentManager().findFragmentByTag("meeting");
                    try {
                        data = meetingFragment.onGetData();
                        data.put("type", "meeting");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (type.equals("flight")) {
                    FlightFragment flightFragment = (FlightFragment) getFragmentManager().findFragmentByTag("flight");
                    try {
                        data = flightFragment.onGetData();
                        data.put("type", "flight");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (type.equals("food")) {
                    FoodFragment foodFragment = (FoodFragment) getFragmentManager().findFragmentByTag("food");
                    try {
                        data = foodFragment.onGetData();
                        data.put("type", "food");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (setDateButton.getText().equals("Select Date") || setTimeButton.getText().equals("Select Time")) {
                    error = true;
                }

                switch (getActionBar().getSelectedNavigationIndex()) {
                    case 0:
                        MeetingFragment meetingFragment = (MeetingFragment) fragmentManager.findFragmentByTag("meeting");
                        try {
                            data = meetingFragment.onGetData();
                            data.put("type", "meeting");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                    case 1:
                        FlightFragment flightFragment = (FlightFragment) fragmentManager.findFragmentByTag("flight");
                        try {
                            data = flightFragment.onGetData();
                            data.put("type", "flight");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                    case 2:
                        FoodFragment foodFragment = (FoodFragment) fragmentManager.findFragmentByTag("food");
                        try {
                            data = foodFragment.onGetData();
                            data.put("type", "food");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                }
            }

            try {
                if (data.getBoolean("error")) error = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (error) {
                if (setDateButton.getText().equals("Select Date")) setDateButton.setTextColor(getResources().getColor(R.color.error));
                if (setTimeButton.getText().equals("Select Time")) setTimeButton.setTextColor(getResources().getColor(R.color.error));

                return false;
            }

            try {
                data.put("date", setDateButton.getText());
                data.put("time", setTimeButton.getText());
                data.put("epoch", getEpochTime());
                if (menuOptions) {
                    tempJSON.getJSONArray("tripActivities").put(position2, data);
                } else {
                    tempJSON.getJSONArray("tripActivities").put(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            dataStorage.onRewriteFile(tempJSON, position);
            finish();
            return true;
        }

        if (id == R.id.action_edit) {
            daOptionsMenu.findItem(R.id.action_edit).setVisible(false);
            daOptionsMenu.findItem(R.id.action_save).setVisible(true);

            setTimeButton.setEnabled(true);
            setDateButton.setEnabled(true);

            if (type.equals("meeting")) {
                MeetingFragment meetingFragment = (MeetingFragment) getFragmentManager().findFragmentByTag("meeting");
                meetingFragment.onEnable();
            } else if (type.equals("flight")) {
                FlightFragment flightFragment = (FlightFragment) getFragmentManager().findFragmentByTag("flight");
                flightFragment.onEnable();
            } else if (type.equals("food")) {
                FoodFragment foodFragment = (FoodFragment) getFragmentManager().findFragmentByTag("food");
                foodFragment.onEnable();
            }
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
        String timeCode = "AM";

        if (hourOfDay > 12) {
            timeCode = "PM";
            hourDay = String.valueOf(hourOfDay-12);
        }

        if (hourDay.length() == 1) hourDay = "0" + hourDay;
        if (min.length() == 1) min = "0" + min;
        if (min.length() == 0) min = "00";

        setTimeButton.setText(hourDay + ":" + min + " " + timeCode);
        setTimeButton.setTextColor(getResources().getColor(R.color.da_black));
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
        setDateButton.setTextColor(getResources().getColor(R.color.da_black));
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
        EditText meetingName;
        EditText meetingWith;
        EditText meetingNotes;
        public static MeetingFragment newInstance() {
            return new MeetingFragment();
        }

        public MeetingFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View fragView = inflater.inflate(R.layout.fragment_meeting_add, container, false);

            meetingName = (EditText) fragView.findViewById(R.id.activity_add_edittext);
            meetingWith = (EditText) fragView.findViewById(R.id.activity_add_edittext2);
            meetingNotes = (EditText) fragView.findViewById(R.id.activity_add_edittext3);

            return fragView;
        }

        public JSONObject onGetData () {
            JSONObject tempJSON = new JSONObject();

            if (meetingName.getText().toString().equals("")) {
                meetingName.setError("Enter Meeting Name");
                try {
                    tempJSON.put("error", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return tempJSON;
            }

            try {
                tempJSON.put("meetingName", meetingName.getText());
                tempJSON.put("meetingWith", meetingWith.getText());
                tempJSON.put("meetingNotes", meetingNotes.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return tempJSON;
        }

        public void onSetData (JSONObject tempData) {
            try {
                meetingName.setText(tempData.getString("meetingName"));
                meetingWith.setText(tempData.getString("meetingWith"));
                meetingNotes.setText(tempData.getString("meetingNotes"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            meetingNotes.setEnabled(false);
            meetingWith.setEnabled(false);
            meetingName.setEnabled(false);
        }

        public void onEnable () {
            meetingName.setEnabled(true);
            meetingWith.setEnabled(true);
            meetingNotes.setEnabled(true);
        }
    }

    public static class FlightFragment extends Fragment {
        EditText flightNumber;
        EditText flightFrom;
        EditText flightTo;
        EditText flightAirline;
        EditText flightConfirmation;

        public static FlightFragment newInstance() {
            return new FlightFragment();
        }

        public FlightFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View fragView = inflater.inflate(R.layout.fragment_flight_add, container, false);

            flightNumber = (EditText) fragView.findViewById(R.id.flight_add_edittext);
            flightFrom = (EditText) fragView.findViewById(R.id.flight_add_edittext2);
            flightTo = (EditText) fragView.findViewById(R.id.flight_add_edittext3);
            flightAirline = (EditText) fragView.findViewById(R.id.flight_add_edittext4);
            flightConfirmation = (EditText) fragView.findViewById(R.id.flight_add_edittext5);

            return fragView;
        }

        public JSONObject onGetData () {
            JSONObject tempJSON = new JSONObject();

            if (flightNumber.getText().toString().equals("") || flightAirline.getText().toString().equals("") || flightConfirmation.getText().toString().equals("")) {
                if (flightNumber.getText().toString().equals("")) flightNumber.setError("Enter Flight Number");
                if (flightAirline.getText().toString().equals("")) flightAirline.setError("Enter Airline");
                if (flightConfirmation.getText().toString().equals("")) flightConfirmation.setError("Enter Confirmation Number");

                try {
                    tempJSON.put("error", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return tempJSON;
            }

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

        public void onSetData (JSONObject tempData) {
            try {
                flightNumber.setText(tempData.getString("flightNumber"));
                flightFrom.setText(tempData.getString("flightFrom"));
                flightTo.setText(tempData.getString("flightTo"));
                flightAirline.setText(tempData.getString("flightAirline"));
                flightConfirmation.setText(tempData.getString("flightConfirmation"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            flightNumber.setEnabled(false);
            flightFrom.setEnabled(false);
            flightTo.setEnabled(false);
            flightAirline.setEnabled(false);
            flightConfirmation.setEnabled(false);
        }

        public void onEnable () {
            flightNumber.setEnabled(true);
            flightFrom.setEnabled(true);
            flightTo.setEnabled(true);
            flightAirline.setEnabled(true);
            flightConfirmation.setEnabled(true);
        }
    }

    public static class FoodFragment extends Fragment {
        EditText restaurantName;
        EditText restaurantConfirmation;
        EditText restaurantNotes;

        public static FoodFragment newInstance() {
            return new FoodFragment();
        }

        public FoodFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View fragView = inflater.inflate(R.layout.fragment_food_add, container, false);

            restaurantName = (EditText) fragView.findViewById(R.id.food_edittext);
            restaurantConfirmation = (EditText) fragView.findViewById(R.id.food_edittext2);
            restaurantNotes = (EditText) fragView.findViewById(R.id.food_edittext3);

            return fragView;
        }

        public JSONObject onGetData () {
            JSONObject tempJSON = new JSONObject();

            if (restaurantName.getText().toString().equals("")) {
                restaurantName.setError("Enter Restaurant Name");

                try {
                    tempJSON.put("error", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return tempJSON;
            }

            try {
                tempJSON.put("restaurantName", restaurantName.getText());
                tempJSON.put("restaurantConfirmation", restaurantConfirmation.getText());
                tempJSON.put("restaurantNotes", restaurantNotes.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return tempJSON;
        }

        public void onSetData (JSONObject tempData) {
            try {
                restaurantName.setText(tempData.getString("restaurantName"));
                restaurantConfirmation.setText(tempData.getString("restaurantConfirmation"));
                restaurantNotes.setText(tempData.getString("restaurantNotes"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            restaurantName.setEnabled(false);
            restaurantConfirmation.setEnabled(false);
            restaurantNotes.setEnabled(false);
        }

        public void onEnable () {
            restaurantName.setEnabled(true);
            restaurantConfirmation.setEnabled(true);
            restaurantNotes.setEnabled(true);
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            return new TimePickerDialog(getActivity(), this, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            ((ActivityActivity)getActivity()).setTimePickerDialog(hourOfDay, minute);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            return new DatePickerDialog(getActivity(), this, c.get(Calendar.YEAR), c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            ((ActivityActivity)getActivity()).setDatePickerDialog(year, month, day);
        }
    }
}
