package com.puntoslash.dipandroid;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.puntoslash.dipandroid.data.JobPostDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int JOB_POST_LOADER_ID = 1;
    private JobPostAdapter jobPostAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,JobPostRegister.class);
                startActivity(i);
            }
        });

        jobPostAdapter = new JobPostAdapter(this, null, 0);
        listView = (ListView)findViewById(R.id.works_list_view);
        listView.setAdapter(jobPostAdapter);
        getSupportLoaderManager().initLoader(JOB_POST_LOADER_ID, null, this);
        //start services by default
        Intent intent = new Intent(this,JobPostService.class);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_settings:
                return true;
            case R.id.action_actualizar:
                Intent intent = new Intent(this,JobPostService.class);
                startService(intent);
                return true;
            case R.id.action_register_job:
                Intent i = new Intent(this,JobPostRegister.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void syncData(View view) {
        Intent intent = new Intent(this,JobPostService.class);
        startService(intent);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new JobPostLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        jobPostAdapter.swapCursor(data);

        JobPostContentObserver observer = new JobPostContentObserver(new Handler(), loader);
        data.registerContentObserver(observer);
        data.setNotificationUri(getContentResolver(), JobPostDbHelper.URI_TABLE_JOB_POST);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        jobPostAdapter.swapCursor(null);
    }

    public void addNewJob(View view) {
        Intent intent = new Intent(this,JobPostRegister.class);
        startService(intent);
    }
}
