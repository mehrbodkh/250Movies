package com.example.mehrbod.a250movies;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Mehrbod on 5/29/2017.
 */

public class ImdbHtmlFileCreator implements Runnable {
    private Activity activity;
    private final int bufferSize = 40960;

    public ImdbHtmlFileCreator(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        synchronized (this) {
            try {
                Log.d("ImdbHtmlFileCreator", "Downloading...");
                URL url = new URL("http://www.imdb.com/chart/top");
                InputStream inputStream = url.openStream();
                OutputStream outputStream = activity.openFileOutput("imdbFile.txt",
                        Context.MODE_PRIVATE);

                byte[] buf = new byte[bufferSize];
                int len;

                while ((len = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }

                inputStream.close();
                outputStream.close();
                Log.d("ImdbHtmlFileCreator", "Downloaded");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
