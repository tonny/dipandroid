package com.puntoslash.dipandroid;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.puntoslash.dipandroid.data.JobPostDbContract;
import com.puntoslash.dipandroid.data.JobPostDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class JobPostService extends IntentService {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public JobPostService() {
        super("JobPostService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // The URL To connect:
        // http://dipandroid-ucb.herokuapp.com/work_posts.json
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        Uri buildUri = Uri.parse("http://dipandroid-ucb.herokuapp.com").buildUpon()
                .appendPath("work_posts.json").build();
        try {
            URL url = new URL(buildUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer buffer = new StringBuffer();

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            String clientInfoJSON = buffer.toString();
            Log.d(LOG_TAG, "JSON: " + clientInfoJSON);
            JSONArray jsonArray = new JSONArray(clientInfoJSON);
            int length = jsonArray.length();
            JobPostDbHelper dbHelper = new JobPostDbHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            for (int i = 0; i < length; i++) {
                    /*
                     {"id":1,"title":"Something","description":"Something",
                          "posted_date":"03/10/2015","contacts":["1-391-882-2074","846-971-4852 x9658"]}
                    * */
                JSONObject element = jsonArray.getJSONObject(i);
                int id = element.getInt("id");
                String title = element.getString("title");
                String date = element.getString("posted_date");
                String description = element.getString("description");

                // Para llenar la base de datos, usamos la clase ContentValue
                // Un objeto de esta clase tiene la referencia a los atributos
                // Que vamos a insertar en la base de datos
                ContentValues contentValues = new ContentValues();

                contentValues.put(JobPostDbContract.JobPost._ID, id);
                contentValues.put(JobPostDbContract.JobPost.TITLE_COLUMN, title);
                contentValues.put(JobPostDbContract.JobPost.POSTED_DATE_COLUMN, date);
                contentValues.put(JobPostDbContract.JobPost.DESCRIPTION_COLUMN, description);

                db.insert(JobPostDbContract.JobPost.TABLE_NAME, null, contentValues);

                JSONArray contacts = element.getJSONArray("contacts");

                for (int j = 0; j < contacts.length(); j++) {
                    String number = contacts.getString(j);

                    ContentValues contactContentValues = new ContentValues();

                    contactContentValues.put(JobPostDbContract.Contact.NUMBER_COLUMN, number);
                    contactContentValues.put(JobPostDbContract.Contact.JOB_POST_ID_COLUMN, id);

                    db.insert(JobPostDbContract.Contact.TABLE_NAME, null, contactContentValues);
                }
            }
        } catch (IOException e) {

        } catch (JSONException e) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }
        getContentResolver().notifyChange(JobPostDbHelper.URI_TABLE_JOB_POST, null);
    }


}
