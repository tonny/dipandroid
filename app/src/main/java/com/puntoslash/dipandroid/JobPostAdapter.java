package com.puntoslash.dipandroid;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.puntoslash.dipandroid.data.JobPostDbContract;

/**
 * Created by antonio on 10/29/15.
 */
public class JobPostAdapter extends CursorAdapter {
    public JobPostAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.job_post_element, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView titleTextView = (TextView)view.findViewById(R.id.title_text_view);
        TextView dateTextView = (TextView)view.findViewById(R.id.date_text_view);

        String title = cursor.getString(cursor.getColumnIndex(JobPostDbContract.JobPost.TITLE_COLUMN));
        String date = cursor.getString(cursor.getColumnIndex(JobPostDbContract.JobPost.POSTED_DATE_COLUMN));

        titleTextView.setText(title);
        dateTextView.setText(date);
    }
}
