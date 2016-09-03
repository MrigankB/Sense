package com.gankmobile.android.sense;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class SenseActivity extends ActionBarActivity implements SensorEventListener
{
    private TextView mSensorValue;
    private SensorManager sensorManager;
    private Sensor proxSensor;
    //private Sensor accelerometer;

    private Button disableButton;
    private boolean disabled = true;

    private long startTime;
    private long waveTime;
    private int waves;

    static final String LOG_TAG = "ScreenOffActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sense);

        waves = 0;
        waveTime = 0;

        mSensorValue = (TextView) findViewById(R.id.sensor_value_text);
        mSensorValue.setText("Place your hand over the Sensor");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proxSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        //accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);

        disableButton = (Button) findViewById(R.id.disable_button);

        updateButtonText();

        disableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disabled = !disabled;
                updateButtonText();
            }
        });
    }

    private void updateButtonText()
    {
        if(disabled)
        {
            disableButton.setText("Activate Wave Lock");
        }
        else
        {
            disableButton.setText("Disable Wave Lock");
        }
    }
    static void turnScreenOff(final Context context)
    {
        DevicePolicyManager policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(context, ScreenOffAdminReceiver.class);
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (admin) {
            Log.i(LOG_TAG, "Going to sleep now.");
            policyManager.lockNow();
        } else {
            Log.i(LOG_TAG, "Not an admin");
            Toast.makeText(context, "Admin not enabled", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // idk panic
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        mSensorValue.setText(String.valueOf(event.values[0]));

        if(!disabled)
        {
            // Prox Sensor sensed something
            if (event.values[0] == 0.0)
            {
                startTime = SystemClock.elapsedRealtime();
                if(waves > 0)
                {
                    long waveDuration = SystemClock.elapsedRealtime() - waveTime;
                    if(waveDuration > 500)
                        waves = 0;
                }
            }
            else
            {
                long duration = SystemClock.elapsedRealtime() - startTime;
                //if the hand was over the sensor for less than half a second...
                if (duration < 500)
                {
                    waves++;
                    if(waves >= 2)
                    {
                        waves = 0;
                        turnScreenOff(getApplicationContext());
                    }
                    else
                    {
                        waveTime = SystemClock.elapsedRealtime();
                    }
                }
            }
        }

    }
}
