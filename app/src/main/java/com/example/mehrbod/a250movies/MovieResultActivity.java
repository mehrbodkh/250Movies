package com.example.mehrbod.a250movies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MovieResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_result);

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new MovieResultActivitySecondThread(this));

        executorService.shutdown();
    }
}
