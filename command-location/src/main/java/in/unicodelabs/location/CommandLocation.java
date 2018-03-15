package in.unicodelabs.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import in.unicodelabs.location.permission.GetPermission;
import in.unicodelabs.location.permission.PermissionResponse;
import in.unicodelabs.location.permission.PermissionResultCallback;
import in.unicodelabs.location.permission.PermissionUtils;

/**
 * Created by saurabh on 13/3/18.
 */

public class CommandLocation {
    private final static String TAG = CommandLocation.class.getSimpleName();
    private final static String[] locationPermission = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    Context context;
    LocationRequest locationRequest;
    ResultReceiver resultReceiver;
    int resultCode;
    int requestMode;
    private long fallBackToLastLocationTime;

    public CommandLocation(Context context, int requestMode, LocationRequest locationRequest, long fallBackToLastLocationTime, int resultCode, ResultReceiver resultReceiver) {
        this.context = context;
        this.requestMode = requestMode;
        this.locationRequest = locationRequest;
        this.fallBackToLastLocationTime = fallBackToLastLocationTime;
        this.resultCode = resultCode;
        this.resultReceiver = resultReceiver;
    }

    public void start() {
        Log.d(TAG, "Checking location permission");
        if (PermissionUtils.hasPermissions(context, locationPermission)) {
            Log.d(TAG, "Creating location setting request and checking setting for it");
            // Create LocationSettingsRequest object using location request
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            final LocationSettingsRequest locationSettingsRequest = builder.build();

            // Check whether location settings are satisfied
            // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
            SettingsClient settingsClient = LocationServices.getSettingsClient(context);
            settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Log.i(TAG, "All location settings are satisfied.");
                    LocationBackGroundService.start(context, requestMode, locationRequest, fallBackToLastLocationTime, resultCode, resultReceiver);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade location settings ");
                            ResolvableApiException rae = (ResolvableApiException) e;
                            TransparentActivity.startResolutionForResult(context, rae, new ResultReceiver(new Handler(Looper.getMainLooper())) {
                                @Override
                                protected void onReceiveResult(int resultCode, Bundle resultData) {
                                    super.onReceiveResult(resultCode, resultData);
                                    switch (resultCode) {
                                        case Activity.RESULT_OK:
                                            Log.i(TAG, "User agreed to make required location settings changes.");
                                            start();
                                            break;
                                        case Activity.RESULT_CANCELED:
                                            Log.i(TAG, "User chose not to make required location settings changes.");
                                            break;
                                    }
                                }
                            });
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                            Log.e(TAG, errorMessage);
                            TransparentActivity.openSetting(context, new ResultReceiver(new Handler(Looper.getMainLooper())) {
                                @Override
                                protected void onReceiveResult(int resultCode, Bundle resultData) {
                                    super.onReceiveResult(resultCode, resultData);
                                    switch (resultCode) {
                                        case Activity.RESULT_OK:
                                            Log.i(TAG, "User agreed to make required location settings changes.");
                                            start();
                                            break;
                                        case Activity.RESULT_CANCELED:
                                            Log.i(TAG, "User chose not to make required location settings changes.");
                                            break;
                                    }
                                }
                            });
                    }
                }
            });
        } else {
            Log.d(TAG, "App dont have location permission, asking for it");
            new GetPermission.Builder(context).setRequestCode(100).setPermissions(locationPermission).enqueue(new PermissionResultCallback() {
                @Override
                public void onPermissionComplete(PermissionResponse permissionResponse) {
                    if (permissionResponse.isGranted()) {
                        start();
                    } else {
                        Log.e(TAG, "User decline location runtime permission");
                    }
                }
            });
        }


    }


    public static class Builder {
        Context context;
        LocationRequest locationRequest;
        LocationResultCallback locationResultCallback;

        int resultCode = 100;
        int requestMode = CommandMode.SINGLE;//init with default request mode
        private long fallBackToLastLocationTime = 0;

        LocationRequest defaultLocationRequest = new LocationRequest();

        {
            defaultLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            defaultLocationRequest.setInterval(2000);
            defaultLocationRequest.setFastestInterval(1000);
        }

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setLocationRequest(LocationRequest locationRequest) {
            this.locationRequest = locationRequest;
            return this;
        }

//        public Builder setResultReceiver(ResultReceiver resultReceiver) {
//            this.resultReceiver = resultReceiver;
//            return this;
//        }
//
//        public Builder setResultReceiver(int resultCode, ResultReceiver resultReceiver) {
//            this.resultCode = resultCode;
//            this.resultReceiver = resultReceiver;
//            return this;
//        }


        public Builder setLocationResultCallback(LocationResultCallback locationResultCallback) {
            this.locationResultCallback = locationResultCallback;
            return this;
        }

        public Builder setRequestMode(int requestMode) {
            this.requestMode = requestMode;
            return this;
        }

        public Builder setFallBackToLastLocationTime(long fallBackToLastLocationTime) {
            this.fallBackToLastLocationTime = fallBackToLastLocationTime;
            return this;
        }

        public CommandLocation build() {
            if (locationRequest == null)
                locationRequest = defaultLocationRequest;

            if (locationResultCallback == null)
                throw new IllegalStateException("Please set location result callback to builder");

            CommandLocation commandLocation = new CommandLocation(context, requestMode, locationRequest, fallBackToLastLocationTime, resultCode, new ResultReceiver(new Handler(Looper.getMainLooper())) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    super.onReceiveResult(resultCode, resultData);
                    String action = resultData.getString(GlobalConstant.Bundle.ACTION);

                    if (action.equalsIgnoreCase(GlobalConstant.Action.LOCATION_RECEIVED)) {
                        Location location = resultData.getParcelable(GlobalConstant.Bundle.LOCATION);
                        if (location != null)
                            if (locationResultCallback != null)
                                locationResultCallback.onLocationReceived(location);
                            else if (locationResultCallback != null)
                                locationResultCallback.noLocationReceived();
                    } else if (action.equalsIgnoreCase(GlobalConstant.Action.NO_LOCATION_RECEIVED)) {
                        if (locationResultCallback != null)
                            locationResultCallback.noLocationReceived();
                    }
                }
            });
            return commandLocation;
        }

        public void start() {
            CommandLocation commandLocation = build();
            commandLocation.start();
        }
    }

    public static class CommandMode {
        public final static int SINGLE = 1;
        public final static int REGULAR_UPDATE = 2;
    }
}
