package com.iantmeyer.spyfall.util;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class PermissionUtil {

    public static boolean hasPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasPermissionWithRequest(Fragment fragment, String permission, int requestCode) {
        Context context = fragment.getActivity().getApplicationContext();
        if (!hasPermission(context, permission)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragment.requestPermissions(new String[]{permission}, requestCode);
            }
            return false;
        }
        return true;
    }
}
