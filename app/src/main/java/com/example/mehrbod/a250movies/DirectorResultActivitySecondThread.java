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

public class DirectorResultActivitySecondThread implements Runnable {
    private Activity activity;
    private DataBaseHelper movieDB;
    public String result;
    public ArrayList<String> resultArray;

    public DirectorResultActivitySecondThread(Activity activity) {
        this.activity = activity;
        movieDB = new DataBaseHelper(activity);
        result = new String();
        resultArray = new ArrayList<>();
    }

    @Override
    public void run() {
        String directorName = activity.getIntent().getStringExtra("directorName");

        Cursor allData = movieDB.getAllData();

        while (allData.moveToNext()) {
            String databaseDirectorName = allData.getString(6);

            if (databaseDirectorName.toLowerCase().contains(directorName.toLowerCase())) {
                result = allData.getString(1);
                resultArray.add(result);
            }
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar progressBar =
                        (ProgressBar) activity.findViewById(R.id.progressBarDirectorResultActivity);
                ViewGroup viewGroup = (ViewGroup) progressBar.getParent();
                viewGroup.removeView(progressBar);

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(activity,
                                android.R.layout.simple_list_item_1,
                                DirectorResultActivitySecondThread.this.resultArray);

                ListView listView =
                        (ListView) activity.findViewById(R.id.listViewDirectorResultActivity);
                listView.setAdapter(adapter);

            }
        });
    }
}
