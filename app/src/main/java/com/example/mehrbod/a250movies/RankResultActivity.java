package com.example.mehrbod.a250movies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RankResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_result);

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new RankResultActivitySecondThread(this));

        executorService.shutdown();
    }
}
