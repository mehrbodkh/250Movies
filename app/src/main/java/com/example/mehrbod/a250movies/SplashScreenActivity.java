package com.example.mehrbod.a250movies;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ActionBar splashScreenActionBar = getSupportActionBar();
        splashScreenActionBar.hide();

        final int DELAY_TIME = 1000;

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Intent finder = new Intent(SplashScreenActivity.this, FinderActivity.class);
                        SplashScreenActivity.this.startActivity(finder);
                        SplashScreenActivity.this.finish();
                    }
                }, DELAY_TIME
        );

    }
}
