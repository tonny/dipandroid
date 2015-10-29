package com.puntoslash.dipandroid;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.Loader;

/**
 * Created by antonio on 10/29/15.
 */
public class JobPostContentObserver extends ContentObserver {
    private Loader<Cursor> cursorLoader;
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public JobPostContentObserver(Handler handler, Loader<Cursor> cursorLoader) {
        super(handler);
        this.cursorLoader = cursorLoader;
    }
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        cursorLoader.onContentChanged();
    }
}
