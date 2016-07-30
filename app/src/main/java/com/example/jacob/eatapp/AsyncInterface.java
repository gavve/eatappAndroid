package com.example.jacob.eatapp;

import android.app.usage.UsageEvents;
import android.graphics.Bitmap;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jacob on 2016-05-29.
 */
public interface AsyncInterface {
    void processFinish(String output) throws JSONException, IOException;

    void bitMapFinished(Bitmap output) throws IOException;
}
