package com.gankmobile.android.sense;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.os.SystemClock;
import android.widget.Toast;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ProxSenseService extends IntentService implements SensorEventListener
{


    public static boolean isRunning = false;

    private SensorManager sensorManager;
    private Sensor proxSensor;

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;

    private long startTime;

    public ProxSenseService()
    {
        super("ProxSenseService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            while(isRunning)
            {
                
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
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
                if (!wakeLock.isHeld())
                {
                    wakeLock.acquire();
                }
                else
                {
                    wakeLock.release();
                }
            }
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID)
    {
        Toast.makeText(this, "Sense has been activated", Toast.LENGTH_SHORT).show();
        isRunning = true;

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proxSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sensorManager.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);

        try
        {
            field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        }
        catch (Throwable ignored)
        {}


        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field, "sense");

        return super.onStartCommand(intent, flags, startID);
    }

    @Override
    public void onDestroy()
    {
        Toast.makeText(this, "Sense has been deactivated", Toast.LENGTH_SHORT).show();
        isRunning = false;
        super.onDestroy();
    }
}
