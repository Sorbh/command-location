package in.unicodelabs.location.permission;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.text.TextUtils;

/**
 * Created by saurabh on 14/3/18.
 */

public class GetPermission {
    Context context;
    String[] permissions;
    int requestCode;
    String notificationTitle;
    String notificationText;

    public GetPermission(Context context, String[] permissions, int requestCode, String notificationTitle, String notificationText) {
        this.context = context;
        this.permissions = permissions;
        this.requestCode = requestCode;
        this.notificationTitle = notificationTitle;
        this.notificationText = notificationText;
    }


    public void enqueue(final PermissionResultCallback callback) {
        if (!PermissionUtils.hasPermissions(context, permissions)) {

            if (!TextUtils.isEmpty(notificationTitle) || !TextUtils.isEmpty(notificationText) ) {
                //Build alert dialog here and notify user
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(notificationTitle);
                builder.setMessage(notificationText);
                builder.setCancelable(false);
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PermissionRequestActivity.grantPermission(context, requestCode, permissions, new ResultReceiver(new Handler(Looper.getMainLooper())) {
                            @Override
                            protected void onReceiveResult(int resultCode, Bundle resultData) {
                                super.onReceiveResult(resultCode, resultData);

                                String[] permissions = resultData.getStringArray(PermissionConstant.Bundle.PERMISSIONS);
                                int[] grantResult = resultData.getIntArray(PermissionConstant.Bundle.PERMISSIONS_RESULT);
                                int requestCode = resultData.getInt(PermissionConstant.Bundle.REQUEST_CODE, 0);

                                callback.onPermissionComplete(new PermissionResponse(permissions, grantResult, requestCode));
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onPermissionComplete(new PermissionResponse(permissions, new int[]{PackageManager.PERMISSION_DENIED}, requestCode));
                    }
                });
                builder.show();
            }else {
                PermissionRequestActivity.grantPermission(context, requestCode, permissions, new ResultReceiver(new Handler(Looper.getMainLooper())) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        super.onReceiveResult(resultCode, resultData);

                        String[] permissions = resultData.getStringArray(PermissionConstant.Bundle.PERMISSIONS);
                        int[] grantResult = resultData.getIntArray(PermissionConstant.Bundle.PERMISSIONS_RESULT);
                        int requestCode = resultData.getInt(PermissionConstant.Bundle.REQUEST_CODE, 0);

                        callback.onPermissionComplete(new PermissionResponse(permissions, grantResult, requestCode));
                    }
                });
            }
        } else {
            callback.onPermissionComplete(new PermissionResponse(permissions, new int[]{PackageManager.PERMISSION_GRANTED}, requestCode));
        }
    }


    public static class Builder {
        Context context;
        String[] permissions;
        int requestCode;
        String notificationTitle;
        String notificationText;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setPermissions(String[] permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder setNotificationTitle(String notificationTitle) {
            this.notificationTitle = notificationTitle;
            return this;
        }

        public Builder setNotificationText(String notificationText) {
            this.notificationText = notificationText;
            return this;
        }

        public GetPermission build() {
            if (permissions == null)
                throw new IllegalStateException("Permission array can't be null");

            if (requestCode == -1)
                throw new IllegalStateException("request code can't be null");

            if (TextUtils.isEmpty(notificationTitle))
                throw new IllegalStateException("notificationTitle can't be null or empty");

            if (TextUtils.isEmpty(notificationText))
                throw new IllegalStateException("notificationText can't be null or empty");

            return new GetPermission(context, permissions, requestCode, notificationTitle, notificationText);
        }

        public void enqueue(PermissionResultCallback callback) {
            GetPermission getPermission = build();
            getPermission.enqueue(callback);
        }
    }
}
