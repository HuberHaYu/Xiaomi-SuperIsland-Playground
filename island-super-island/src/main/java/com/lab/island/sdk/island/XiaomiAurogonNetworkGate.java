package com.lab.island.sdk.island;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

import java.lang.reflect.Method;

/**
 * Short-lived HyperOS validation gate. It changes only XMSF's UID and always restores the rule.
 * Notification ownership remains scoped to this app's UID.
 */
final class XiaomiAurogonNetworkGate {
    private static final String TAG = "IslandAurogonGate";
    private static final String XMSF_PACKAGE = "com.xiaomi.xmsf";

    @NonNull private final Context appContext;
    @Nullable private final ConnectivityManager connectivityManager;
    @Nullable private Integer cachedXmsfUid;
    @Nullable private Boolean cachedSupported;
    private boolean blockedByThisInstance;

    XiaomiAurogonNetworkGate(@NonNull Context context) {
        appContext = context.getApplicationContext();
        connectivityManager = appContext.getSystemService(ConnectivityManager.class);
    }

    synchronized boolean isSupported() {
        if (cachedSupported != null) return cachedSupported;
        boolean supported = connectivityManager != null
                && resolveXmsfUid() != null
                && hasAurogonMethod();
        cachedSupported = supported;
        return supported;
    }

    synchronized boolean setXmsfBlocked(boolean blocked) {
        ConnectivityManager manager = connectivityManager;
        Integer uid = resolveXmsfUid();
        if (manager == null || uid == null || !isSupported()) return false;

        if (blocked) blockedByThisInstance = true;
        try {
            HiddenApiBypass.invoke(
                    ConnectivityManager.class,
                    manager,
                    "updateAurogonUidRule",
                    uid,
                    blocked
            );
            if (!blocked) blockedByThisInstance = false;
            return true;
        } catch (Throwable error) {
            Log.w(TAG, "Unable to " + (blocked ? "arm" : "restore")
                    + " XMSF validation", error);
            return false;
        }
    }

    synchronized boolean restoreIfNeeded() {
        return !blockedByThisInstance || setXmsfBlocked(false);
    }

    private boolean hasAurogonMethod() {
        try {
            Method method = HiddenApiBypass.getDeclaredMethod(
                    ConnectivityManager.class,
                    "updateAurogonUidRule",
                    int.class,
                    boolean.class
            );
            return method.getReturnType() == void.class;
        } catch (Throwable error) {
            Log.i(TAG, "HyperOS Aurogon API is unavailable", error);
            return false;
        }
    }

    @Nullable
    private Integer resolveXmsfUid() {
        if (cachedXmsfUid != null) return cachedXmsfUid;
        try {
            PackageManager packageManager = appContext.getPackageManager();
            ApplicationInfo info;
            if (Build.VERSION.SDK_INT >= 33) {
                info = packageManager.getApplicationInfo(
                        XMSF_PACKAGE,
                        PackageManager.ApplicationInfoFlags.of(0L)
                );
            } else {
                //noinspection deprecation
                info = packageManager.getApplicationInfo(XMSF_PACKAGE, 0);
            }
            if (info.uid < Process.FIRST_APPLICATION_UID) return null;
            cachedXmsfUid = info.uid;
            return info.uid;
        } catch (PackageManager.NameNotFoundException | RuntimeException error) {
            Log.i(TAG, "XMSF is not visible on this device", error);
            return null;
        }
    }
}
