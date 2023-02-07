package com.example.crop_diease;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

      Button btnlocation = findViewById(R.id.btnlocation);
        btnlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),location.class);
                startActivity(i);

            }
        });


      Button btnweather = findViewById(R.id.btnweather);
        btnweather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i1 = new Intent(getApplicationContext(),weather.class);
                startActivity(i1);

            }
        });


        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Button cropdetection = findViewById(R.id.cropdetection);
        cropdetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i2 = new Intent(getApplicationContext(),detection.class);
                startActivity(i2);

            }
        });


        @SuppressLint({"MissingInflatedId", "LocalSuppress"})

        Button cropprediction = findViewById(R.id.cropprediction);
        cropprediction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i2 = new Intent(getApplicationContext(),CropPrediction.class);
                startActivity(i2);

            }
        });

    }
}