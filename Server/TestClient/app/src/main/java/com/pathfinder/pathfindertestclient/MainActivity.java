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
                Log.d("DEBUG1", "[RECV]: registering UID");
                Connector.registerUID();
            }
        });

    }






}

