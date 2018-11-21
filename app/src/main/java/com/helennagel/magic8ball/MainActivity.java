package com.helennagel.magic8ball;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView txtAnswer;
    private SensorManager sensorManager;
   private float acelValue;
   private float acelLast;
   private float shake;
   private String[] answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtAnswer = findViewById(R.id.txtAnwser);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        acelValue = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;
        answer = getResources().getStringArray(R.array.answers);
    }

    // Creating options menu on the top left
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Function which executes when one of the options in the menu is chosen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.magic:
                startActivity(new Intent(this, ShakeActivity.class));
                return true;
        }
        return false;
    }


    // Used for receiving notifications from the sensor manager when there is new sensor data
    private final SensorEventListener sensorListener = new SensorEventListener() {

        // Getting the answer when a device is being shaken well
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            acelLast = acelValue;
            acelValue = (float)Math.sqrt((double)(x*x + y*y + z*z));
            float delta = acelValue - acelLast;
            shake = shake * 0.9f + delta;
            if (shake > 12){
                int randomInt = new Random().nextInt(answer.length);
                String randomAnswer = answer[randomInt];
                txtAnswer.setText(randomAnswer);
            }
        }

        /* Class MainActivity must have onAccuracyChanged() method,
           otherwise it must be declared as abstract */
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
}
