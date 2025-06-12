package vn.sttechnology.virtualassistant.service.stoptracking;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import vn.sttechnology.virtualassistant.VirtualAssistantApp;
import vn.sttechnology.virtualassistant.service.place.PlaceManager;

import javax.inject.Inject;

public class StopTrackingWorker extends Worker {
    private static boolean isProcessing;
    private final Context context;

    @Inject
    public PlaceManager placeManager;

    public StopTrackingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        ((VirtualAssistantApp) (getApplicationContext())).appComponent.inject(this);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        doSync();
        return Result.success();
    }

    private void doSync() {
        if (isProcessing) return;
        try {
            isProcessing = true;
            placeManager.sync(context);
        }
        catch (Exception exception) {
            FirebaseCrashlytics.getInstance().recordException(exception);
        }
        finally {
            isProcessing = false;
        }
    }
}
