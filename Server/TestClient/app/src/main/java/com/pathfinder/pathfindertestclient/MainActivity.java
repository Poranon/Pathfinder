package com.pathfinder.pathfindertestclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button connect = (Button) findViewById(R.id.buttonConnect);
        connect.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Connector.request(getApplicationContext(), ConnectionCodes.REGISTER);
            }
        });

        Log.d("DEBUG1", getApplicationContext().getFilesDir().toString());
        Button map_bremen = (Button) findViewById(R.id.buttonBremen);
        map_bremen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connector.request(getApplicationContext(), ConnectionCodes.MAP_HB, getApplicationContext().getFilesDir().toString() + "/video.mp4", false);
            }
        });

        Button map_part = (Button) findViewById(R.id.buttonmappart);
        map_part.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connector.request(getApplicationContext(), ConnectionCodes.MAPPART, getApplicationContext().getFilesDir().toString() + "/map_part.txt", false);
            }
        });

    }






}

