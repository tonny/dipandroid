package com.puntoslash.dipandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class JobPostRegister extends AppCompatActivity {

    private AutoCompleteTextView titleTextView;
    private EditText descriptionTextView;
    private AutoCompleteTextView contactTextView;

    private View mProgressView;
    private View mJobFormView;

    private JobPostTask mPostTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_post_register);

        Button mEmailSignInButton = (Button) findViewById(R.id.job_post_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        titleTextView = (AutoCompleteTextView)findViewById(R.id.title_text);
        descriptionTextView = (EditText)findViewById(R.id.description);
        contactTextView = (AutoCompleteTextView)findViewById(R.id.contact);

        mJobFormView = findViewById(R.id.job_form);
        mProgressView = findViewById(R.id.job_progress);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mJobFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mJobFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mJobFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mJobFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        if (mPostTask != null) {
            return;
        }

        // Reset errors.

        titleTextView.setError(null);
        descriptionTextView.setError(null);
        contactTextView.setError(null);

        // Store values at the time of the register attempt.
        String title = titleTextView.getText().toString();
        String description = descriptionTextView.getText().toString();
        String contact = contactTextView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(title)) {
            titleTextView.setError(getString(R.string.error_field_required));
            focusView = titleTextView;
            cancel = true;
        }

        // Check for a valid description address.
        if (TextUtils.isEmpty(description)) {
            descriptionTextView.setError(getString(R.string.error_field_required));
            focusView = descriptionTextView;
            cancel = true;
        }

        if(TextUtils.isEmpty(contact)){
            contactTextView.setError(getString(R.string.error_field_required));
            focusView = contactTextView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mPostTask = new JobPostTask(title, description,contact);
            mPostTask.execute((Void) null);
        }
    }

    private boolean isFieldValid(String fieldtext) {
        return fieldtext.length() > 4;
    }

    public class JobPostTask extends AsyncTask<Void, Void, Boolean> {

        private final String mTitle;
        private final String mDescription;
        private final String mContact;

        JobPostTask(String title, String description, String contact) {
            mTitle = title;
            mDescription = description;
            mContact = contact;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            int response = 0;
            HttpURLConnection urlConnection = null;
            DataOutputStream printout = null;
            Uri buildUri = Uri.parse("http://dipandroid-ucb.herokuapp.com").buildUpon()
                    .appendPath("work_posts.json").build();
            try {
                URL url = new URL(buildUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.addRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();
                // {"work_post":{"title":"Title of the Job Post","description":"Text with the description","contacts":["Phone1", "Phone2", ..., "PhoneN"]}}

                //Create json to send to server
                JSONObject jobPostData = new JSONObject();
                JSONObject contentPostData = new JSONObject();
                JSONArray contactsArray = new JSONArray();

                String [] contacts = mContact.split(",");
                for(int i=0; i<contacts.length; i++){
                    contactsArray.put(contacts[i]);
                }
                contentPostData.put("title",mTitle);
                contentPostData.put("description",mDescription);
                contentPostData.put("contacts",contactsArray);
                jobPostData.put("work_post",contentPostData);

                // Send POST output.
                printout = new DataOutputStream(urlConnection.getOutputStream());

                String strData = jobPostData.toString();
                byte[] data=strData.getBytes("UTF-8");

                printout.write(data);
                printout.flush();
                response = urlConnection.getResponseCode();

                // Simulate network access.
                //Thread.sleep(2000);
            } catch (IOException e) {
                Log.i("[ERROR CONNECTION]", "errror de coneccion");
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(printout != null){
                    try {
                        printout.close ();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mPostTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                /*
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
                */
            }
        }

        @Override
        protected void onCancelled() {
            mPostTask = null;
            showProgress(false);
        }
    }
}
