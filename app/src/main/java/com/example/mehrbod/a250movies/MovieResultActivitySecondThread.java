package com.example.mehrbod.a250movies;

import android.app.Activity;
import android.database.Cursor;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * Created by Mehrbod on 6/1/2017.
 */

public class MovieResultActivitySecondThread implements Runnable {
    private Activity activity;
    private DataBaseHelper movieDB;
    public StringBuilder result;
    protected ArrayList<String> resultArray;

    public MovieResultActivitySecondThread(Activity activity) {
        this.activity = activity;
        movieDB = new DataBaseHelper(activity);
        result = new StringBuilder();
        resultArray = new ArrayList<>();
    }

    @Override
    public void run() {
        String movieName = activity.getIntent().getStringExtra("movieName");

        Cursor allData = movieDB.getAllData();

        while (allData.moveToNext()) {
            String databaseMovieName = allData.getString(1);

            if (databaseMovieName.toLowerCase().contains(movieName.toLowerCase())) {
                result.append("Rank: " + allData.getString(0) + "\n");
                result.append("Title: " + allData.getString(1) + "\n");
                result.append("Stars: " + allData.getString(2) + "\n");
                result.append("Writer: " + allData.getString(3) + "\n");
                result.append("Music: " + allData.getString(4) + "\n");
                result.append("Producer: " + allData.getString(5) + "\n");
                result.append("Director: " + allData.getString(6));

                resultArray.add(result.toString());

                result = new StringBuilder();
            }
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar progressBar =
                        (ProgressBar) activity.findViewById(R.id.progressBarMovieResultActivity);
                ViewGroup viewGroup = (ViewGroup) progressBar.getParent();
                viewGroup.removeView(progressBar);

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(activity,
                                android.R.layout.simple_list_item_1,
                                MovieResultActivitySecondThread.this.resultArray);

                ListView listView =
                        (ListView) activity.findViewById(R.id.listViewMovieResultActivity);
                listView.setAdapter(adapter);
            }
        });
    }
}
