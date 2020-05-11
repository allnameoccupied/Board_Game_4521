package com.app.boardgame4521.MaxUtil;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.boardgame4521.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
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

    public static void INIT(Context context) {
        if (isInited) {
            return;
        } else {
            setAppContext(context);

            init_Notification();
            init_Vibrator();
//            init_Camera();
//            init_WorkManager();
//            init_LocalBroadcastReceiver();
//            init_Realm();
//            init_Google();
//            init_Firebase();
//            init_Firestore();
//            init_Service();
            init_Network();

//            init_Sensor();

            utilGoogle.INIT();
            utilRealm.INIT();

            isInited = true;
        }
    }

    public static void PAUSE() {
        stopToast();
//        stopSnackbar();
        stopVibrator();
    }

    public static void DESTROY() {
        utilRealm.shutdownRealm();
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

//    public static void makeToast_later(FragmentActivity activityToRunOn, CharSequence theThing, long delay) {
//        makeSchedule_short(delay, () -> makeToast_diffActivity(activityToRunOn, theThing));
//    }
//
//    public static void makeToast_repeat(FragmentActivity activity, CharSequence theThing, long interval, int repeatForXTime) {
//        makeSchedule_long(0, () -> makeToast_diffActivity(activity, theThing), interval, repeatForXTime);
//    }

    public static void stopToast() {
        if (TOAST == null) {
            return;
        }
        TOAST.cancel();
        TOAST = null;
    }

    //LOG
    public static final String LOG_TAG = "gg<3";

    public enum SERIOUS_LEVEL {VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT}

    public static void makeLog(SERIOUS_LEVEL level, String message) {
        switch (level) {
            case VERBOSE:
                Log.v(LOG_TAG, message);
                break;
            case DEBUG:
                Log.d(LOG_TAG, message);
                break;
            case INFO:
                Log.i(LOG_TAG, message);
                break;
            case WARN:
                Log.w(LOG_TAG, message);
                break;
            case ERROR:
                Log.e(LOG_TAG, message);
                break;
            case ASSERT:
                Log.wtf(LOG_TAG, message);
                break;
        }
    }

    public static void log(String message) {
        makeLog(SERIOUS_LEVEL.DEBUG, message != null ? message : "Null");
    }

    public static void log(int message) {
        log(Integer.toString(message));
    }

    public static void log(long message) {
        log(Long.toString(message));
    }

    public static void log(boolean message) {
        log(message ? "TRUE" : "FALSE");
    }

    public static void log(Object message) {
        log(message.toString());
    }

    public static void log() {
        log("log");
    }

    //TOAST + LOG
    //no toast in other thread or fire later
    public static void makeToastLog(Context context, SERIOUS_LEVEL level, String message) {
        makeToast(context, message);
        makeLog(level, message);
    }

    public static void makeToastLog(SERIOUS_LEVEL level, String message) {
        makeToastLog(APP_CONTEXT, level, message);
    }

    public static void makeToastLog(Context context, String message) {
        makeToastLog(context, SERIOUS_LEVEL.DEBUG, message);
    }

    public static void makeToastLog(String message) {
        makeToastLog(APP_CONTEXT, SERIOUS_LEVEL.DEBUG, message);
    }

    //NOTIFICATION
    private static NotificationManagerCompat notiManager;
    public static final String NOTI_CHANNELID_DEFAULT = "4521_NOTICHANNEL_ID";
    private static final String NOTI_ID_KEY_IN_EXTRAS = "noti_id_key_in_extras";
    private static AtomicInteger notiCount = new AtomicInteger(1);

    private static void init_Notification() {
        //get noti manager
        notiManager = NotificationManagerCompat.from(APP_CONTEXT);

        //make default channel
        NotificationChannel defaultChannel = new NotificationChannel(NOTI_CHANNELID_DEFAULT, "Default Channel", NotificationManager.IMPORTANCE_HIGH);
        defaultChannel.setDescription("Default Notification Channel for MEMO3");
        defaultChannel.enableVibration(true);
        defaultChannel.enableLights(false);
        defaultChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        notiManager.createNotificationChannel(defaultChannel);
    }

    public static int makeNotification_getID(){return notiCount.incrementAndGet();}

    public static void makeNotification(int ID, Notification notification) {
        notiManager.notify(ID, notification);
    }

    //VIBRATOR  (max amplitude = 255)
    private static Vibrator vibrator;

    private static void init_Vibrator() {
        vibrator = (Vibrator) APP_CONTEXT.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static void makeVibrate_full(long ms, int amplitude) {
        stopVibrator();
        vibrator.vibrate(VibrationEffect.createOneShot(ms, amplitude));
    }

    public static void makeVibrate(long ms) {
        makeVibrate_full(ms, VibrationEffect.DEFAULT_AMPLITUDE);
    }

    public static void makeVibrate_pattern(long[] ms, int[] amplitude, int repeat) {
        if (ms.length != amplitude.length) {
            return;
        }
        stopVibrator();
        vibrator.vibrate(VibrationEffect.createWaveform(ms, amplitude, repeat));
    }

    public static void makeVibrate_pattern(long[] ms, int[] amplitude) {
        makeVibrate_pattern(ms, amplitude, -1);
    }

    public static void makeVibrate_pattern(long[] ms, int repeat) {
        stopVibrator();
        vibrator.vibrate(VibrationEffect.createWaveform(ms, repeat));
    }

    public static void makeVibrate_pattern(long[] ms) {
        makeVibrate_pattern(ms, -1);
    }

    public static void stopVibrator() {
        vibrator.cancel();
    }

    //NETWORK   //check only
    private static ConnectivityManager networkManager;

    private static void init_Network(){
        networkManager = ((ConnectivityManager) APP_CONTEXT.getSystemService(Context.CONNECTIVITY_SERVICE));
    }

    public static boolean network_isNetworkConnected(){
        return networkManager.getActiveNetworkInfo() != null;
    }

    public static ArrayList<Network> network_getAllNetworks(){
        return new ArrayList<>(Arrays.asList(networkManager.getAllNetworks()));
    }
}
