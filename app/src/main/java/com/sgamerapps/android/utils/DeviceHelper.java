package com.sgamerapps.android.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

public class DeviceHelper {

    @SuppressLint("MissingPermission")
    public static String getDeviceIMEI(Context context) {
         String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

}
