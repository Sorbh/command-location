package in.unicodelabs.location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;

import in.unicodelabs.location.permission.PermissionConstant;

/**
 * Created by saurabh on 14/3/18.
 */

public class TransparentActivity extends Activity {
    private final static String TAG = TransparentActivity.class.getSimpleName();

    public static void startResolutionForResult(Context context, ResolvableApiException e, ResultReceiver resultReceiver) {
        Intent intent = new Intent(context, TransparentActivity.class);
        intent.putExtra(GlobalConstant.Bundle.ACTION, GlobalConstant.Action.CHECK_SETTINGS);
        intent.putExtra(GlobalConstant.Bundle.EXCEPTION, e);
        intent.putExtra(GlobalConstant.Bundle.RESULT_RECEIVER, resultReceiver);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        context.startActivity(intent);
    }
    public static void openSetting(Context context,ResultReceiver resultReceiver) {
        Intent intent = new Intent(context, TransparentActivity.class);
        intent.putExtra(GlobalConstant.Bundle.ACTION, GlobalConstant.Action.OPEN_SETTINGS);
        intent.putExtra(GlobalConstant.Bundle.RESULT_RECEIVER, resultReceiver);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        context.startActivity(intent);
    }

    private ResultReceiver resultReceiver;
    private String action;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            resultReceiver = getIntent().getParcelableExtra(PermissionConstant.Bundle.RESULT_RECEIVER);
            action = getIntent().getStringExtra(GlobalConstant.Bundle.ACTION);

            if (action.equalsIgnoreCase(GlobalConstant.Action.CHECK_SETTINGS)) {
                try {
                    ResolvableApiException rae = (ResolvableApiException) getIntent().getSerializableExtra(GlobalConstant.Bundle.EXCEPTION);
                    rae.startResolutionForResult(TransparentActivity.this, 100);
                } catch (IntentSender.SendIntentException sie) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                    sie.printStackTrace();
                }
            } else if (action.equalsIgnoreCase(GlobalConstant.Action.OPEN_SETTINGS)) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 101);
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bundle bundle = new Bundle();
        bundle.putInt(GlobalConstant.Bundle.REQUEST_CODE, resultCode);
        resultReceiver.send(resultCode, bundle);
        finish();
//        switch (requestCode) {
//            case 100:
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        break;
//                }
//                break;
//            case 101:
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        break;
//                }
//                break;
//        }
    }
}
