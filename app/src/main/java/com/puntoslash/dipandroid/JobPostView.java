package com.puntoslash.dipandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class JobPostView extends AppCompatActivity {

    TextView titleView, descriptionView, contactView;
    private static final String TITLE_JOB = "title";
    private static final String DESCRIPTION_JOB = "description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_post_view);

        //get data from Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra(TITLE_JOB);
        String description = intent.getStringExtra(DESCRIPTION_JOB);

        titleView = (TextView)findViewById(R.id.title_view);
        descriptionView = (TextView)findViewById(R.id.description_view);

        titleView.setText(title);
        descriptionView.setText(description);
    }
}
