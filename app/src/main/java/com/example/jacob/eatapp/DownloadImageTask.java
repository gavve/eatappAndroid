package com.example.jacob.eatapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jacob on 2016-05-29.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    /* Class som hämtar profilbild för user i bakgrunden och sen sätter
        profilbilden för ImageView profile_picture
     */
    public AsyncInterface delegate = null;
    Bitmap result;
    public DownloadImageTask(AsyncInterface asyncInterface) {
        this.delegate = asyncInterface;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", "Kunde inte hämta profilbild");
            e.printStackTrace();
        }
        return mIcon11;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        try {
            delegate.bitMapFinished(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
