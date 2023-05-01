package vn.sttechnology.virtualassistant.service.logging;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class LoggingService {
    private static LoggingService instance;

    private LoggingService() {
    }

    public static synchronized LoggingService getInstance() {
        if (instance == null) {
            instance = new LoggingService();
        }
        return instance;
    }

    public void log(String msg) {
        FirebaseCrashlytics.getInstance().log(msg);
    }

    public void recordException(Exception exception) {
        FirebaseCrashlytics.getInstance().recordException(exception);
    }
}
