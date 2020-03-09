package com.app.boardgame4521.MaxUtil;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

public final class util {
    //BASE
    public static Context APP_CONTEXT;
    private static boolean AppContextseted = false;

    public static void setAppContext(Context context) {
        if (!AppContextseted) {
            APP_CONTEXT = context;
            AppContextseted = true;
        }
    }

    //LIFECYCLE
    private static boolean isInited = false;

    public static void INIT() {
        if (isInited) {
            return;
        } else {
            init_Notification();
            init_Vibrator();
            init_Camera();
            init_WorkManager();
            init_LocalBroadcastReceiver();
            init_Realm();
            init_Google();
            init_Firebase();
            init_Firestore();
            init_Service();
            init_Network();

//            init_Sensor();
            isInited = true;
        }
    }

    //TOAST
    private static Toast TOAST;

    public static void makeToast_full(Context context, CharSequence theThing, int duration) {
        TOAST = Toast.makeText(context, theThing != null ? theThing : "Null", duration);
        TOAST.show();
    }

    public static void makeToast(Context context, CharSequence theThing) {
        stopToast();
        makeToast_full(context, theThing, Toast.LENGTH_SHORT);
    }

    public static void makeToast(CharSequence theThing) {
        makeToast(APP_CONTEXT, theThing);
    }

    public static void makeToast_diffActivity(FragmentActivity activity, CharSequence theThing) {
        activity.runOnUiThread(() -> makeToast(theThing));
    }

    public static void makeToast_later(FragmentActivity activityToRunOn, CharSequence theThing, long delay) {
        makeSchedule_short(delay, () -> makeToast_diffActivity(activityToRunOn, theThing));
    }

    public static void makeToast_repeat(FragmentActivity activity, CharSequence theThing, long interval, int repeatForXTime) {
        makeSchedule_long(0, () -> makeToast_diffActivity(activity, theThing), interval, repeatForXTime);
    }

    public static void stopToast() {
        if (TOAST == null) {
            return;
        }
        TOAST.cancel();
        TOAST = null;
    }
}
