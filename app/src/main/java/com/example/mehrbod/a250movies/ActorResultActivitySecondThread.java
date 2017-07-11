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

public class ActorResultActivitySecondThread implements Runnable {
    private Activity activity;
    private DataBaseHelper movieDB;
    public StringBuilder result;
    protected ArrayList<String> resultArray;

    public ActorResultActivitySecondThread(Activity activity) {
        this.activity = activity;
        movieDB = new DataBaseHelper(activity);
        result = new StringBuilder();
        resultArray = new ArrayList<>();
    }

    @Override
    public void run() {
        final String actorName = activity.getIntent().getStringExtra("actorName");

        Cursor allData = movieDB.getAllData();

        while (allData.moveToNext()) {
            String databaseActorName = allData.getString(2);

            if (databaseActorName.toLowerCase().contains(actorName.toLowerCase())) {
                resultArray.add(allData.getString(1));
            }
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar progressBar =
                        (ProgressBar) activity.findViewById(R.id.progressBarActorResultActivity);
                ViewGroup viewGroup = (ViewGroup) progressBar.getParent();
                viewGroup.removeView(progressBar);

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(
                                activity,
                                android.R.layout.simple_list_item_1,
                                ActorResultActivitySecondThread.this.resultArray
                        );

                ListView listView =
                        (ListView) activity.findViewById(R.id.listViewActorResultActivity);
                listView.setAdapter(adapter);
            }
        });
    }
}
