package com.puntoslash.dipandroid;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.puntoslash.dipandroid.data.JobPostDbHelper;
import com.puntoslash.dipandroid.data.JobPostDbContract.JobPost;

/**
 * Created by antonio on 10/29/15.
 */
public class JobPostLoader extends SimpleCursorLoader {
    private JobPostDbHelper dbHelper;

    public JobPostLoader(Context context) {
        super(context);
        dbHelper = new JobPostDbHelper(context);
    }

    @Override
    public Cursor loadInBackground() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        return database.query(JobPost.TABLE_NAME,
                new String[] {JobPost._ID, JobPost.TITLE_COLUMN, JobPost.POSTED_DATE_COLUMN},
                null, null, null, null, JobPost.POSTED_DATE_COLUMN + " DESC");
    }
}
