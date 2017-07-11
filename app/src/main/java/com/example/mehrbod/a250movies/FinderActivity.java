package com.example.mehrbod.a250movies;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FinderActivity extends AppCompatActivity {
    private DataBaseHelper movieDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finder);

        movieDB = new DataBaseHelper(this);

        Fragment fragment = new RankFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.rankDirectorActorMovieFragment, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_finder, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showMessage(String title, String message) {
        AlertDialog.Builder theMessage = new AlertDialog.Builder(this);
        theMessage.setCancelable(true);
        theMessage.setTitle(title);
        theMessage.setMessage(message);
        theMessage.show();
    }

    public void changeFragment(View view) {
        Fragment fragment = null;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        if (view == findViewById(R.id.rankButtonFinderActivity)) {
            fragment = new RankFragment();
        }

        if (view == findViewById(R.id.directorButtonFinderActivity)) {
            fragment = new DirectorFragment();
        }

        if (view == findViewById(R.id.actorButtonFinderAcitivty)) {
            fragment = new ActorFragment();
        }

        if (view == findViewById(R.id.movieButtonFinderActivity)) {
            fragment = new MovieFragment();
        }

        fragmentTransaction.replace(R.id.rankDirectorActorMovieFragment, fragment);
        fragmentTransaction.commit();
    }

    public void onActionUpdateClickListener(MenuItem item) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new ImdbHtmlFileCreator(this));

        executorService.shutdown();

        boolean isFileCreated = false;

        try {
            isFileCreated = executorService.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (isFileCreated) {
            if (insertMovieName()) {
                Toast.makeText(this, "Updated.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Error. Try again.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Couldn't load the file. Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean insertMovieName() {
        InputStream inputStream = null;
        boolean isDatabaseEmpty = false;
        try {
            inputStream = openFileInput("imdbFile.txt");
            Scanner inputFile = new Scanner(inputStream);

            String temp;
            String tempTiltle = "";
            String tempRankInImdb = "";

            // reads file and extract movies and attributes
            while (inputFile.hasNext()) {
                temp = inputFile.nextLine();

                if (temp.contains("<td class=\"titleColumn\">")) {

                    // reformatting the id of the movies to match to int type for id in the Movie class
                    String t1 = inputFile.next();
                    t1 = t1.replace(".", "");
                    tempRankInImdb = t1;

                    // ignoring non usable lines
                    inputFile.nextLine();

                    // finding the url
                    t1 = inputFile.nextLine();

                    // finding title
                    t1 = inputFile.nextLine();
                    String t2 = "";
                    boolean start = false;

                    for (int i = 0; i < t1.length(); i++) {
                        if (t1.charAt(i) == '<') {
                            start = false;
                        }

                        if (start) {
                            t2 += t1.charAt(i);
                        }

                        if (t1.charAt(i) == '>') {
                            start = true;
                        }
                    }
                    tempTiltle = t2;

                    // making the list
                    if (movieDB.isEmpty()) {
                        isDatabaseEmpty = true;
                    }

                    if (isDatabaseEmpty) {
                        movieDB.insertData(tempTiltle, "", "", "", "", "", "");
                    }
                    else {
                        Cursor res = movieDB.getData(tempRankInImdb);
                        boolean doIt = true;

                        res.moveToNext();
                        if (res.getString(1).equals(tempTiltle)) {
                            doIt = false;
                        }

                        if (doIt) {
                            movieDB.updateData(tempRankInImdb, tempTiltle, "", "", "", "", "", "");
                        }
                    }

                }
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void onActionViewAllClickListener(MenuItem item) {
        StringBuilder message = new StringBuilder();

        Cursor result = movieDB.getAllData();

        while (result.moveToNext()) {
            message.append("RANK: " + result.getString(0) + "\n");
            message.append("TITLE: " + result.getString(1) + "\n");
            message.append("STARS: " + result.getString(2) + "\n");
            message.append("WRITER: " + result.getString(3) + "\n");
            message.append("MUSIC: " + result.getString(4) + "\n");
            message.append("PRODUCER: " + result.getString(5) + "\n");
            message.append("DIRECTOR: " + result.getString(6) + "\n");
            message.append("YEAR: " + result.getString(7) + "\n\n");
        }

        showMessage("Movie List", message.toString());
    }

    public void onRankFragmentFindButtonClickListener(View view) {

        EditText rankEditText = (EditText) findViewById(R.id.rankEditTextRankFragment);

        if (!rankEditText.getText().toString().equals("")) {
            int rankEditTextValue = Integer.parseInt(rankEditText.getText().toString());
            if ((rankEditTextValue >= 1) && (rankEditTextValue <= 250)) {
                Intent intent = new Intent(this, RankResultActivity.class);
                intent.putExtra("movieRank", rankEditText.getText().toString());
                Cursor cursor = movieDB.getData(rankEditText.getText().toString());
                cursor.moveToNext();
                intent.putExtra("movieName", cursor.getString(1));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Enter a rank from 1 to 250.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onDirectorFragmentFindButtonClickListener(View view) {
        EditText directorNameEditText =
                (EditText) findViewById(R.id.directorEditTextDirectorActivity);

        Intent intent = new Intent(this, DirectorResultActivity.class);
        intent.putExtra("directorName", directorNameEditText.getText().toString());
        startActivity(intent);
    }

    public void onActorFragmentFindButtonClickListener(View view) {
        EditText actorNameEditText =
                (EditText) findViewById(R.id.actorEditTextActorFragment);

        Intent intent = new Intent(this, ActorResultActivity.class);
        intent.putExtra("actorName", actorNameEditText.getText().toString());
        startActivity(intent);
    }

    public void onMovieFragmentFindButtonClickListener(View view) {
        EditText movieNameEditText =
                (EditText) findViewById(R.id.movieNameEditTextMovieFragment);

        Intent intent = new Intent(this, MovieResultActivity.class);
        intent.putExtra("movieName", movieNameEditText.getText().toString());
        startActivity(intent);
    }
}