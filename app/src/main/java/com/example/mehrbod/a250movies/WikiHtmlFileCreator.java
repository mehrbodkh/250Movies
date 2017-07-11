package com.example.mehrbod.a250movies;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Mehrbod on 5/26/2017.
 */

public class WikiHtmlFileCreator implements Runnable {
    private String movieName;
    private int bufferSize = 40960;
    private String movieRank;
    private Activity activity;

    public WikiHtmlFileCreator(Activity activity, String movieName, String movieRank) {
        this.movieName = movieName;
        this.activity = activity;
        this.movieRank = movieRank;
    }


    @Override
    public void run() {
        boolean needChange = false;
        URL url = null;
//        try {
//
            String tempWikiURL = "https://en.wikipedia.org/wiki/";
            String temp = movieName;
            temp = temp.replace(" ", "_");
            temp += "_(film)";
            tempWikiURL += temp;
//
//            URL url = new URL(tempWikiURL);
//            InputStream inputStream = url.openStream();
//            OutputStream outputStream = activity.openFileOutput("movieFile.txt",
//                    Context.MODE_PRIVATE);
//
//            byte[] buf = new byte[bufferSize];
//            int len;
//
//            while ((len = inputStream.read(buf)) > 0) {
//                outputStream.write(buf, 0, len);
//            }
//
//            Log.d("WikiFileCreator", "File Created");
//            inputStream.close();
//            outputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        for (int i = 0; i < 2; i++) {
            try {
                String secondURL = "";
                if (needChange) {
                    secondURL = tempWikiURL;
                    secondURL = secondURL.replace("_(film)", "");
                    url = new URL(secondURL);
                } else {
                    url = new URL(tempWikiURL);
                }

                InputStream inputStream = url.openStream();
                OutputStream outputStream = activity.openFileOutput("movieFile.txt",
                        Context.MODE_PRIVATE);

                byte[] buffer = new byte[bufferSize];
                int len;

                while ((len = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }

                inputStream.close();
                outputStream.close();

                InputStream tempFileInputStream = activity.openFileInput("movieFile.txt");
                Scanner inputFile = new Scanner(tempFileInputStream);

                while (inputFile.hasNextLine()) {
                    String tempString = inputFile.nextLine();

                    if (tempString.contains("<b>Wikipedia does not have an article " +
                            "with this exact name.</b> Please <span class=")) {
                        needChange = true;
                        break;
                    }
                }
                Log.d("File", "Wiki Created");

            } catch (IOException e) {
                Log.d("WikiHtmlFile", "Not Found");
            }

            if (!needChange) {
                Log.d("Created", "WikiHtmlFile");
                break;
            }
        }
    }
}
