package com.apps.vpaniagua.glowtooth;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Splash extends AppCompatActivity {
    private Boolean botonBackPressed=false;
    private static final int DURACION_SPLASH=2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                if(!botonBackPressed){
                    Intent intent = new Intent(Splash.this, SelectBT.class);
                    startActivity(intent);
                }
            }
        }, DURACION_SPLASH);

    }

    @Override
    public void onBackPressed(){
        botonBackPressed=true;
        super.onBackPressed();
    }

}
