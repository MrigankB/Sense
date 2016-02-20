package com.gankmobile.android.sense;

/**
 * Created by Mrigank on 1/4/16.
 */
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Receiver class which shows notifications when the Device Administrator status
 * of the application changes.
 */
public class ScreenOffAdminReceiver extends DeviceAdminReceiver {
    private void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, "Sense was added as Device Administrator");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        showToast(context, "Sense off was removed from Device Administrators List");
    }

}
