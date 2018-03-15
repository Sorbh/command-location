package in.unicodelabs.location.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * Created by saurabh on 14/3/18.
 */

public class PermissionUtils {
    public static boolean checkPermission(final Activity context, final int requestCode, final String... permissions) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (!hasPermissions(context, permissions)) {
                ActivityCompat.requestPermissions(context, permissions, requestCode);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
