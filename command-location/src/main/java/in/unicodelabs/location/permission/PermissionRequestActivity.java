package in.unicodelabs.location.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

/**
 * Created by saurabh on 14/3/18.
 */

public class PermissionRequestActivity extends Activity {

    public static void grantPermission(Context context, int requestCode, String[] permissions, ResultReceiver resultReceiver) {
        Intent intent = new Intent(context, PermissionRequestActivity.class);
        intent.putExtra(PermissionConstant.Bundle.PERMISSIONS, permissions);
        intent.putExtra(PermissionConstant.Bundle.REQUEST_CODE, requestCode);
        intent.putExtra(PermissionConstant.Bundle.RESULT_RECEIVER, resultReceiver);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        context.startActivity(intent);
    }

    private ResultReceiver resultReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            resultReceiver = getIntent().getParcelableExtra(PermissionConstant.Bundle.RESULT_RECEIVER);
            String[] permissionsArray = getIntent().getStringArrayExtra(PermissionConstant.Bundle.PERMISSIONS);
            int requestCode = getIntent().getIntExtra(PermissionConstant.Bundle.REQUEST_CODE, 0);

            if (!PermissionUtils.hasPermissions(this, permissionsArray)) {
                ActivityCompat.requestPermissions(this, permissionsArray, requestCode);
            } else {
                onComplete(requestCode, permissionsArray, new int[]{PackageManager.PERMISSION_GRANTED});
            }
        } else {
            finish();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onComplete(requestCode, permissions, grantResults);
    }

    private void onComplete(int requestCode, String[] permissions, int[] grantResults) {
        Bundle bundle = new Bundle();
        bundle.putStringArray(PermissionConstant.Bundle.PERMISSIONS, permissions);
        bundle.putIntArray(PermissionConstant.Bundle.PERMISSIONS_RESULT, grantResults);
        bundle.putInt(PermissionConstant.Bundle.REQUEST_CODE, requestCode);
        resultReceiver.send(requestCode, bundle);
        finish();
    }

}
