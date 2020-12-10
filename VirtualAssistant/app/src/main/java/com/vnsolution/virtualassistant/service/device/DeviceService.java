package com.vnsolution.virtualassistant.service.device;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.vnsolution.virtualassistant.R;
import com.vnsolution.virtualassistant.common.DeviceOptions;
import com.vnsolution.virtualassistant.util.PermissionUtils;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class DeviceService extends Service {
    @Inject
    public DeviceManager deviceManager;

    public class SyncPhoneCallTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                deviceManager.syncPhoneCalls(DeviceService.this);
            } catch (Exception exception) {
                FirebaseCrashlytics.getInstance().recordException(exception);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            String message = (result) ? "Phone call sync process is succeeded" : "Phone call sync process is failed";
            Toast.makeText(DeviceService.this, message, Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }

    public class SyncPhoneSmsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                deviceManager.syncPhoneSms(DeviceService.this);
            } catch (Exception exception) {
                FirebaseCrashlytics.getInstance().recordException(exception);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            String message = (result) ? "Phone sms sync process is succeeded" : "Phone sms sync process is failed";
            Toast.makeText(DeviceService.this, message, Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }

    public DeviceService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AndroidInjection.inject(this);

        //validate
        if (!PermissionUtils.hasPermissions(this, DeviceOptions.PERMISSIONS_PHONE_CALL)) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            stopSelf();
            return START_NOT_STICKY;
        }

        //notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(DeviceOptions.NOTIFY_FOREGROUND_SERVICE_CHANNEL_ID, DeviceOptions.NOTIFY_FOREGROUND_SERVICE_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(this, DeviceOptions.NOTIFY_FOREGROUND_SERVICE_CHANNEL_ID)
                .setContentTitle(DeviceOptions.NOTIFY_FOREGROUND_SERVICE_TITLE)
                .setContentText(DeviceOptions.NOTIFY_FOREGROUND_SERVICE_TEXT)
                .setSmallIcon(R.drawable.ic_unknown)
                .build();
        startForeground((int) (Math.random() * 999999), notification);

        //do heavy work on a background thread
        if (intent != null) {
            try {
                switch (intent.getAction()) {
                    case DeviceOptions.INTENT_ACTION_SYNC_PHONE_CALL:
                        new SyncPhoneCallTask().execute();
                        break;
                    case DeviceOptions.INTENT_ACTION_SYNC_PHONE_SMS:
                        new SyncPhoneSmsTask().execute();
                        break;
                    case DeviceOptions.INTENT_ACTION_SYNC_PHONE_CONTACT:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
