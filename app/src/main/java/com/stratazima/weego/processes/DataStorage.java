package com.stratazima.weego.processes;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Esau on 9/10/2014.
 */

public class DataStorage implements Cloneable {
    private static DataStorage mInstance;
    private static Context mContext;
    JSONArray tripArray = null;

    private DataStorage() {}

    public static DataStorage getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new DataStorage();
        }
        mContext = context.getApplicationContext();
        return mInstance;
    }

    /**
     * Writes JSON data to application files directory.
     */
    public void onWriteFile(JSONObject jsonObject, String fileName) {
        try {
            FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(jsonObject.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onRewriteFile (JSONObject jsonObject, int position) {
        File tempFileDir = mContext.getFilesDir();
        File file[] = tempFileDir.listFiles();

        try {
            FileOutputStream fos = mContext.openFileOutput(file[position].getName(), Context.MODE_PRIVATE);
            fos.write(jsonObject.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads file and creates a JSONArray which is sent to the main activity
     */
    public JSONArray onReadTripFiles() {
        String content;
        File tempFileDir = mContext.getFilesDir();

        File file[] = tempFileDir.listFiles();
        tripArray = new JSONArray();
        for (int i = 0; i < file.length; i++) {
            try {
                InputStream inputStream = mContext.openFileInput(file[i].getName());
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder contentBuffer = new StringBuilder();

                while ((content = bufferedReader.readLine()) != null) {
                    contentBuffer.append(content);
                }

                content = contentBuffer.toString();
                tripArray.put(i, new JSONObject(content));
                inputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return tripArray;
    }

    public JSONObject onReadTrip(int position) {
        String content;
        File tempFileDir = mContext.getFilesDir();
        File file[] = tempFileDir.listFiles();
        JSONObject tempJSON = null;

        try {
            InputStream inputStream = mContext.openFileInput(file[position].getName());
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder contentBuffer = new StringBuilder();

            while ((content = bufferedReader.readLine()) != null) {
                contentBuffer.append(content);
            }

            content = contentBuffer.toString();
            tempJSON = new JSONObject(content);
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tempJSON;
    }
}
