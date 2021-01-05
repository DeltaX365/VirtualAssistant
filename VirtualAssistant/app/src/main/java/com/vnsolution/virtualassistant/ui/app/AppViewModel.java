package com.vnsolution.virtualassistant.ui.app;

import android.app.usage.UsageStats;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.util.ArrayMap;

import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vnsolution.virtualassistant.BuildConfig;
import com.vnsolution.virtualassistant.db.entity.PhoneAppEntity;
import com.vnsolution.virtualassistant.model.PhoneApp;
import com.vnsolution.virtualassistant.service.device.DeviceManager;
import com.vnsolution.virtualassistant.util.MappingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.inject.Inject;

import static android.content.Context.MODE_PRIVATE;

public class AppViewModel extends ViewModel {
    private Integer index = 1;
    private MutableLiveData<List<PhoneApp>> phoneAppsLiveData;

    @Inject
    public DeviceManager deviceManager;

    @Inject
    public AppViewModel() {}

    public void setIndex(int index) {
        this.index = index;
    }

    public LiveData<List<PhoneApp>> getPhoneAppsAsync(Context context, boolean shouldGetSystemPackages, boolean forceUpdate) {
        phoneAppsLiveData = new MutableLiveData<>();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getPhoneApps(context, shouldGetSystemPackages, forceUpdate,true);
            }
        }, 1000);
        return phoneAppsLiveData;
    }

    public List<PhoneApp> getPhoneApps(Context context, boolean shouldGetSystemPackages, boolean forceUpdate, boolean includeStat) {
        List<PhoneAppEntity> phoneAppEntities = deviceManager.getPhoneApps(context, shouldGetSystemPackages, forceUpdate);
        List<PhoneApp> phoneApps = MappingUtils.convertPhoneAppEntitiesToPhoneApps(phoneAppEntities);
        ArrayMap<String, UsageStats> phoneAppUsageStat = deviceManager.getPhoneAppUsageStats(context);
        for (PhoneApp phoneApp : phoneApps) {
            try {
                phoneApp.appIcon = context.getPackageManager().getApplicationIcon(phoneApp.packageName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (includeStat && phoneAppUsageStat != null) {
                phoneApp.usageStats = phoneAppUsageStat.get(phoneApp.packageName);
            }
        }
        if (phoneAppsLiveData != null) {
            phoneAppsLiveData.setValue(phoneApps);
        }
        return phoneApps;
    }

    public boolean hasPhoneLockPasscode(Context context) {
        return deviceManager.hasPhoneLockPasscode(context);
    }

    public void savePhoneLockPasscode(Context context, String passwordNumber) {
        deviceManager.savePhoneLockPasscode(context, passwordNumber);
    }

    public void lockPhoneApp(Context context, PhoneApp phoneApp) {
        deviceManager.lockPhoneApp(context, phoneApp);
    }

    public void unlockPhoneApp(Context context, PhoneApp phoneApp) {
        deviceManager.unlockPhoneApp(context, phoneApp);
    }

    public boolean sharePhoneApp(Context context, PhoneApp phoneApp) {
        try {
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(phoneApp.packageName, PackageManager.GET_META_DATA);
            String appInstalledPath = app.sourceDir;

            // Make new directory in new location
            File originalFile = new File(appInstalledPath);
            File tempFile = new File(context.getExternalCacheDir() + File.separator + "ExtractedApk");
            if (!tempFile.isDirectory() && !tempFile.mkdir()) return false;

            tempFile = new File(tempFile.getPath() + File.separator + phoneApp.appName + ".apk");
            if (!tempFile.exists() && !tempFile.createNewFile()) return false;

            // Copy file to new location
            InputStream in = new FileInputStream(originalFile);
            OutputStream out = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();

            // Open share dialog
            Intent intent = new Intent(Intent.ACTION_SEND);

            // MIME of .apk is "application/vnd.android.package-archive".
            // but Bluetooth does not accept this. Let's use "*/*" instead.
            intent.setType("*/*");
//            intent.setType("application/vnd.android.package-archive");

            // Create a content provider to expose your private data to other apps
            // https://developer.android.com/guide/topics/providers/content-provider-creating
            Uri fileUri = FileProvider.getUriForFile(context,BuildConfig.APPLICATION_ID + ".provider", tempFile);
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);

            context.startActivity(Intent.createChooser(intent, "Share app via"));
            return true;

        } catch (PackageManager.NameNotFoundException | IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}