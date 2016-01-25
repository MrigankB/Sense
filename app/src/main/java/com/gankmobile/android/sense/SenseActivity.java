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
    //private boolean screenOn = true;

    private long startTime;

    static final String LOG_TAG = "ScreenOffActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sense);

        mSensorValue = (TextView) findViewById(R.id.sensor_value_text);
        //mScreenCondition = (TextView) findViewById(R.id.screen_condition_text);
        mSensorValue.setText("Place your hand over the Sensor");
        //mScreenCondition.setText("Screen is on");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proxSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sensorManager.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //Button mStartServiceButton = (Button)findViewById(R.id.start_service_button);

//        if(ProxSenseService.isRunning)
//        {
//            mStartServiceButton.setText("Deactivate Sense");
//        }
//        else
//        {
//            mStartServiceButton.setText("Activate Sense");
//        }


//        mStartServiceButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if (!ProxSenseService.isRunning)
////                {
////                    startService(new Intent(SenseActivity.this, ProxSenseService.class));
////                }
////                else
////                {
////                    stopService(new Intent(SenseActivity.this, ProxSenseService.class));
////                }
//            }
//        });
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

        // Prox Sensor sensed something
        if(event.values[0] == 0.0)
        {
            startTime = SystemClock.elapsedRealtime();
        }
        else
        {
            long duration = SystemClock.elapsedRealtime() - startTime;

            //if the hand was over the sensor for less than half a second...
            if (duration < 500)
            {
//                if (screenOn)
//                {
//                    mScreenCondition.setText("Screen is turned off");
//                    screenOn = false;
//                }
//                else
//                {
//                    mScreenCondition.setText("Screen is turned on");
//                    screenOn = true;
//                }
                turnScreenOff(getApplicationContext());
            }
        }

    }
}
