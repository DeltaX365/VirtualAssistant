package vn.sttechnology.virtualassistant.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class PermissionUtils {
    public static boolean hasPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (hasPermission(context, permission) == false)
                return false;
        }
        return true;
    }

    public static boolean hasPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
}
