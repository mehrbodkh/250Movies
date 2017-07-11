package com.example.mehrbod.a250movies;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mehrbod on 5/31/2017.
 */

public class RankResultActivitySecondThread implements Runnable {
    private Activity activity;
    private DataBaseHelper movieDB;
    public String finalRes;

    public RankResultActivitySecondThread(Activity activity) {
        this.activity = activity;
        movieDB = new DataBaseHelper(activity);
    }

    @Override
    public void run() {
        String movieRank = activity.getIntent().getStringExtra("movieRank");
        String movieName = activity.getIntent().getStringExtra("movieName");

        Cursor checkCursor = movieDB.getData(movieRank);
        checkCursor.moveToNext();
        String checkStarts = checkCursor.getString(2);

        if (checkStarts.equals("")) {
            Log.d("Entered", "checkStarts");
            ExecutorService executorService = Executors.newCachedThreadPool();
            executorService.execute(new WikiHtmlFileCreator(activity, movieName, movieRank));

            executorService.shutdown();
            boolean isTerminated = false;
            try {
                isTerminated = executorService.awaitTermination(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (isTerminated) {
                Log.d("Entered", "isTerminated");
                boolean readIsNeeded = false;

                Cursor cursor = movieDB.getData(movieRank);
                cursor.moveToNext();

                if (cursor.getString(2).equals("")) {
                    readIsNeeded = true;
                }

                String res[] = null;
                finalRes = null;
                if (readIsNeeded) {
                    res = makeList();
                    movieDB.updateData(movieRank, movieName, res[0], res[1], res[2], res[3], res[4], res[5]);


                    finalRes =
                            "Rank: " + movieRank + "\n" + "Title: " + movieName + "\n" + "Stars: " + res[0] + "\n" +
                                    "Writers: " + res[1] + "\n" + "Music: " + res[2] + "\n" + "Producers: " +
                                    res[3] + "\n" + "Director: " + res[4] + "\n";
                } else {
                    finalRes =
                            "Rank: " + movieRank + "\n" + "Title: " + movieName + "\n" + "Stars: " + cursor.getString(2) + "\n" +
                                    "Writers: " + cursor.getString(3) + "\n" + "Music: " + cursor.getString(4) + "\n" + "Producers: " +
                                    cursor.getString(5) + "\n" + "Director: " + cursor.getString(6) + "\n";

                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.progressBarRankResultActivity);
                        ViewGroup viewGroup = (ViewGroup) progressBar.getParent();
                        viewGroup.removeView(progressBar);

                        TextView textView = (TextView) activity.findViewById(R.id.textViewRankResultActivity);
                        textView.setText(RankResultActivitySecondThread.this.finalRes);
                    }
                });
            }
        }
        else {
            finalRes =
                    "Rank: " + movieRank + "\n" + "Title: " + movieName + "\n" + "Stars: " + checkCursor.getString(2) + "\n" +
                            "Writers: " + checkCursor.getString(3) + "\n" + "Music: " + checkCursor.getString(4) + "\n" + "Producers: " +
                            checkCursor.getString(5) + "\n" + "Director: " + checkCursor.getString(6) + "\n";
            activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.progressBarRankResultActivity);
                        ViewGroup viewGroup = (ViewGroup) progressBar.getParent();
                        viewGroup.removeView(progressBar);

                        TextView textView = (TextView) activity.findViewById(R.id.textViewRankResultActivity);
                        textView.setText(RankResultActivitySecondThread.this.finalRes);
                    }
                });
            }

    }

    private String[] makeList() {
        ArrayList<String> director = new ArrayList<>();
        ArrayList<String> producer = new ArrayList<>();
        ArrayList<String> writer = new ArrayList<>();
        ArrayList<String> stars = new ArrayList<>();
        ArrayList<String> music = new ArrayList<>();
        String year = null;

        String[] result = new String[6];

        try {
            InputStream inputStream = activity.openFileInput("movieFile.txt");

            Scanner inputFile = new Scanner(inputStream);

            // to-do: read the file and extract the data from it

            String directorTemp = "";
            String producerTemp = "";
            String writerTemp = "";
            String starsTemp = "";
            String musicTemp = "";

            while (inputFile.hasNextLine()) {
                String temp = inputFile.nextLine();

                if (temp.contains("<th scope=\"row\" style=\"white-" +
                        "space:nowrap;padding-right:0.65em;\">Directed by</th>")) {
                    String secondTemp = inputFile.nextLine();

                    if (secondTemp.equals("<td style=\"line-height:1.3em;\">")) {
                        inputFile.nextLine();
                        inputFile.nextLine();

                        while (true) {
                            secondTemp = inputFile.nextLine();

                            if (secondTemp.contains("</ul>")) {
                                break;
                            }

                            boolean start = false;
                            for (int i = 1; i < secondTemp.length(); i++) {
                                if (secondTemp.charAt(i) == '<') {
                                    start = false;
                                }

                                if (start) {
                                    directorTemp += secondTemp.charAt(i);
                                }

                                if (secondTemp.charAt(i) == '>' && secondTemp.charAt(i - 1) == '"') {
                                    start = true;
                                }
                            }
                            director.add(directorTemp);
                            directorTemp = "";
                        }
                    } else if (secondTemp.contains("<td style=\"line-height:1.3em;\">")) {
                        boolean start = false;
                        for (int i = 33; i < secondTemp.length(); i++) {
                            if (secondTemp.charAt(i) == '<') {
                                start = false;
                            }

                            if (start) {
                                directorTemp += secondTemp.charAt(i);
                            }

                            if (secondTemp.charAt(i) == '>' && secondTemp.charAt(i - 1) == '"') {
                                start = true;
                            }
                        }
                        director.add(directorTemp);
                        directorTemp = "";
                    }
                }

                if (temp.contains("<th scope=\"row\" " +
                        "style=\"white-space:nowrap;padding-right:0.65em;\">Produced by</th>")) {
                    String secondTemp = inputFile.nextLine();

                    if (secondTemp.equals("<td style=\"line-height:1.3em;\">")) {
                        inputFile.nextLine();
                        inputFile.nextLine();

                        while (true) {
                            secondTemp = inputFile.nextLine();

                            if (secondTemp.contains("</ul>")) {
                                break;
                            }

                            boolean start = false;
                            for (int i = 1; i < secondTemp.length(); i++) {
                                if (secondTemp.charAt(i) == '<') {
                                    start = false;
                                }

                                if (start) {
                                    producerTemp += secondTemp.charAt(i);
                                }

                                if (secondTemp.charAt(i) == '>' && secondTemp.charAt(i - 1) == '"') {
                                    start = true;
                                }
                            }
                            producer.add(producerTemp);
                            producerTemp = "";
                        }
                    } else if (secondTemp.contains("<td style=\"line-height:1.3em;\">")) {
                        boolean start = false;
                        for (int i = 33; i < secondTemp.length(); i++) {
                            if (secondTemp.charAt(i) == '<') {
                                start = false;
                            }

                            if (start) {
                                producerTemp += secondTemp.charAt(i);
                            }

                            if (secondTemp.charAt(i) == '>' && secondTemp.charAt(i - 1) == '"') {
                                start = true;
                            }
                        }
                        producer.add(producerTemp);
                        producerTemp = "";
                    }
                }

                if (temp.contains("<th scope=\"row\" style=\"white-space:" +
                        "nowrap;padding-right:0.65em;\">Screenplay by</th>")) {
                    String secondTemp = inputFile.nextLine();

                    if (secondTemp.equals("<td style=\"line-height:1.3em;\">")) {
                        inputFile.nextLine();

                        while (true) {
                            secondTemp = inputFile.nextLine();

                            if (secondTemp.contains("</ul>")) {
                                break;
                            }

                            boolean start = false;
                            for (int i = 1; i < secondTemp.length(); i++) {
                                if (secondTemp.charAt(i) == '<') {
                                    start = false;
                                }

                                if (start) {
                                    writerTemp += secondTemp.charAt(i);
                                }

                                if (secondTemp.charAt(i) == '>' && secondTemp.charAt(i - 1) == '"') {
                                    start = true;
                                }
                            }
                            writer.add(writerTemp);
                            writerTemp = "";
                        }
                    } else if (secondTemp.contains("<td style=\"line-height:1.3em;\">")) {
                        boolean start = false;
                        for (int i = 33; i < secondTemp.length(); i++) {
                            if (secondTemp.charAt(i) == '<') {
                                start = false;
                            }

                            if (start) {
                                writerTemp += secondTemp.charAt(i);
                            }

                            if (secondTemp.charAt(i) == '>' && secondTemp.charAt(i - 1) == '"') {
                                start = true;
                            }
                        }
                        writer.add(writerTemp);
                        writerTemp = "";
                    }
                }

                if (temp.contains("<th scope=\"row\" style=\"white-space:" +
                        "nowrap;padding-right:0.65em;\">Starring</th>")) {
                    String secondTemp = inputFile.nextLine();

                    if (secondTemp.equals("<td style=\"line-height:1.3em;\">")) {
                        inputFile.nextLine();

                        while (true) {
                            secondTemp = inputFile.nextLine();

                            if (secondTemp.contains("</ul>")) {
                                break;
                            }

                            boolean start = false;
                            for (int i = 1; i < secondTemp.length(); i++) {
                                if (secondTemp.charAt(i) == '<') {
                                    start = false;
                                }

                                if (start) {
                                    starsTemp += secondTemp.charAt(i);
                                }

                                if (secondTemp.charAt(i) == '>' && secondTemp.charAt(i - 1) == '"') {
                                    start = true;
                                }
                            }
                            stars.add(starsTemp);
                            starsTemp = "";
                        }
                    } else if (secondTemp.contains("<td style=\"line-height:1.3em;\">")) {
                        boolean start = false;
                        for (int i = 33; i < secondTemp.length(); i++) {
                            if (secondTemp.charAt(i) == '<') {
                                start = false;
                            }

                            if (start) {
                                starsTemp += secondTemp.charAt(i);
                            }

                            if (secondTemp.charAt(i) == '>' && secondTemp.charAt(i - 1) == '"') {
                                start = true;
                            }
                        }
                        stars.add(starsTemp);
                        starsTemp = "";
                    }
                }


                if (temp.contains("<th scope=\"row\" style=\"white-space:" +
                        "nowrap;padding-right:0.65em;\">Music by</th>")) {
                    String secondTemp = inputFile.nextLine();

                    if (secondTemp.equals("<td style=\"line-height:1.3em;\">")) {
                        inputFile.nextLine();
                        inputFile.nextLine();

                        while (true) {
                            secondTemp = inputFile.nextLine();

                            if (secondTemp.contains("</ul>")) {
                                break;
                            }

                            boolean start = false;
                            for (int i = 1; i < secondTemp.length(); i++) {
                                if (secondTemp.charAt(i) == '<') {
                                    start = false;
                                }

                                if (start) {
                                    musicTemp += secondTemp.charAt(i);
                                }

                                if (secondTemp.charAt(i) == '>' && secondTemp.charAt(i - 1) == '"') {
                                    start = true;
                                }
                            }
                            music.add(musicTemp);
                            musicTemp = "";
                        }
                    } else if (secondTemp.contains("<td style=\"line-height:1.3em;\">")) {
                        boolean start = false;
                        for (int i = 33; i < secondTemp.length(); i++) {
                            if (secondTemp.charAt(i) == '<') {
                                start = false;
                            }

                            if (start) {
                                musicTemp += secondTemp.charAt(i);
                            }

                            if (secondTemp.charAt(i) == '>' && secondTemp.charAt(i - 1) == '"') {
                                start = true;
                            }
                        }
                        music.add(musicTemp);
                        musicTemp = "";
                    }
                }

            }


            result[0] = rowToString(stars);
            result[1] = rowToString(writer);
            result[2] = rowToString(music);
            result[3] = rowToString(producer);
            result[4] = rowToString(director);
            result[5] = year;


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    private String rowToString(ArrayList<String> list) {
        String resultString = "";

        if (list.size() > 1) {
            for (int i = 0; i < list.size() - 1; i++) {
                resultString += list.get(i) + ", ";
            }
            resultString += list.get(list.size() - 1);
        }else if (list.size() == 1){
            resultString += list.get(0);
        }
        return resultString;
    }
}
