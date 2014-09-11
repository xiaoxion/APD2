package com.stratazima.weego;

import android.content.Context;
import android.widget.Toast;

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
    JSONArray daObject = null;

    private DataStorage() {}

    public static DataStorage getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new DataStorage();
        }
        mContext = context.getApplicationContext();
        return mInstance;
    }

    /**
     * Check file so that the application can check if there
     * is an existing file.
     */
    public boolean onCheckFile(String fileName) {
        File file = new File(mContext.getFilesDir().getPath() + "/" + fileName);
        return file.exists();
    }

    /**
     * Writes JSON data to application files directory.
     */
    public void onWriteFile(JSONObject jsonObject, String fileName) {
        // JSONArray tempJSON;
        //if (onCheckFile(fileName)) {} else {}

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

    /**
     * Reads file and creates a JSONArray which is sent to the main activity
     */
    public JSONArray onReadFile() {
        String content;
        try {
            InputStream inputStream = mContext.openFileInput("data.JSON");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder contentBuffer = new StringBuilder();

            while ((content = bufferedReader.readLine()) != null) {
                contentBuffer.append(content);
            }

            content = contentBuffer.toString();
            daObject = new JSONArray(content);
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return daObject;
    }
}
