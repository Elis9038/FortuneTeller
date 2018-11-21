package com.helennagel.magic8ball;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

// implementing a SensorEventistener interface
public class ShakeActivity extends AppCompatActivity implements SensorEventListener {

    // constant for duration of animation
    public static final int FADE_DURATION = 1500;
    // constant for changing widget's position
    public static final int START_OFFSET = 1000;
    public static final int VIBRATE_TIME = 250;
    public static final int THRESHOLD = 240;
    public static final int SHAKE_COUNT = 2;
    private static Random RANDOM = new Random();
    private Vibrator vibrator;
    private SensorManager sensorManager;
    private Sensor sensor;
    private float lastX,lastY,lastZ;
    int counter = 0;
    private TextView txtAnswer;
    private ImageView imgBall;
    private Animation ballAnime;
    private ArrayList<String> answers;
    private Button btnShake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        btnShake = findViewById(R.id.raputa);
        imgBall = findViewById(R.id.imgBall);
        txtAnswer = findViewById(R.id.txtAnsw);

        // Instances of Vibrator class are obtained using
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Accessing sensor via this method below,
        // which takes the sensor type and the delay defined as constants on sensor manager as parameters
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Accessing sensor manager via:
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Making the ball shake
        ballAnime = AnimationUtils.loadAnimation(this, R.anim.shake);

        answers = loadAnswers();

        btnShake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswers(getAnswer(),true);
            }
        });
    }

    // Creating options menu on the top left display corner
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
            case R.id.fstpage:
                // Start another activity
                startActivity(new Intent(this, MainActivity.class));
                return true;
        }
        return false;
    }

    // To enable sensors after disabling them
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        showAnswers(getString(R.string.shk), false);
    }

    // To disable sensors. Because sensors are nor always necessary
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    // This listener will get informed, if the sensor data changes
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Checking sensor event type
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            if (isShakeEnough(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2])){
                showAnswers(getAnswer(),false);
            }
        }
    }

    // Class ShakeActivity must have onAccuracyChanged() method,
    // otherwise it must be declared as abstract
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private boolean isShakeEnough(float x, float y, float z){
        double force = 0d;

        // GRAVITY_EARTH constant value: 3.5303614E-7
        // Calculating the force to check if a device is shaken enough
        // Squaring the recieved number
        force += Math.pow((x-lastX) / SensorManager.GRAVITY_EARTH, 2.0);
        force += Math.pow((y-lastY) / SensorManager.GRAVITY_EARTH, 2.0);
        force += Math.pow((z-lastZ) / SensorManager.GRAVITY_EARTH, 2.0);

        // Getting the root number
        force = Math.sqrt(force);

        lastX = x;
        lastY = y;
        lastZ = z;

        if (force > ((float) THRESHOLD / 100f)){
            imgBall.startAnimation(ballAnime);
            counter++;

            if (counter > SHAKE_COUNT){
                counter = 0;
                lastX = 0;
                lastY = 0;
                lastZ = 0;
                return true;
            }
        }
        return false;
    }

    private void showAnswers(String vastus, boolean withAnim){
        if (withAnim){
            imgBall.startAnimation(ballAnime);
        }

        // Starting animation and setting the widget's visability
        txtAnswer.setVisibility(View.VISIBLE);
        txtAnswer.setText(vastus);
        AlphaAnimation animation = new AlphaAnimation(0,1);
        animation.setStartOffset(START_OFFSET);
        txtAnswer.setVisibility(View.VISIBLE);
        animation.setDuration(FADE_DURATION);

        txtAnswer.startAnimation(animation);
        vibrator.vibrate(VIBRATE_TIME);
    }

    // Function to get random answer
    private String getAnswer(){
        int randomInt = RANDOM.nextInt(answers.size());
        return answers.get(randomInt);
    }

    public ArrayList<String> loadAnswers(){
        ArrayList<String> list = new ArrayList<>();

        /* Accessing your application's resources.
           Acquiring the Resources instance associated with an application with getResources()
           At first, we're saving the answers to the String array
           because we can't add them to ArrayList
        */
        String[] tab = getResources().getStringArray(R.array.answers);
        // Checking if array has any values
        if (tab != null && tab.length > 0){
            // Adding every tab value to list
            for (String str : tab){
                list.add(str);
            }
        }
        return list;
    }
}

