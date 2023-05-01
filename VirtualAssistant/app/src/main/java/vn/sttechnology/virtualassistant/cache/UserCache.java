package vn.sttechnology.virtualassistant.cache;

import android.content.Context;

public class UserCache {
    private static final String USER_UID_KEY = "AppCache.CurrentUser.Uid";

    public static void saveUserUid(Context context, String userUid) {
        AppCache.save(context, USER_UID_KEY, userUid);
    }

    public static String getUserUid(Context context) {
        return AppCache.retrieve(context, USER_UID_KEY);
    }

    public static void removeUserUid(Context context) {
        AppCache.remove(context, USER_UID_KEY);
    }
}
