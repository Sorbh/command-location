package in.unicodelabs.location.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import in.unicodelabs.location.CommandLocation;
import in.unicodelabs.location.GlobalConstant;

/**
 * Created by saurabh on 13/3/18.
 */

public class LocationBackGroundService extends Service {
    private static final long NO_FALLBACK = 0;
    private Context context;
    private final String TAG = LocationBackGroundService.class.getSimpleName();
    private int mLocationMode;
    private LocationRequest mLocationRequest;
    private ResultReceiver resultReceiver;
    private int resultCode;
    private Handler handler;
    private long fallBackToLastLocationTime;

    public static void start(Context context, int mLocationMode, LocationRequest mLocationRequest, long fallBackToLastLocationTime, int resultCode, ResultReceiver resultReceiver) {
        Intent intent = new Intent(context, LocationBackGroundService.class);
        intent.setAction(GlobalConstant.Action.LOCATION_COMMAND_START);
        intent.putExtra(GlobalConstant.Bundle.COMMAND_MODE, mLocationMode);
        intent.putExtra(GlobalConstant.Bundle.LOCATION_REQUEST, mLocationRequest);
        intent.putExtra(GlobalConstant.Bundle.FALLBACK_TIME, fallBackToLastLocationTime);
        intent.putExtra(GlobalConstant.Bundle.RESULT_CODE, resultCode);
        intent.putExtra(GlobalConstant.Bundle.RESULT_RECEIVER, resultReceiver);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, LocationBackGroundService.class);
        intent.setAction(GlobalConstant.Action.LOCATION_COMMAND_STOP);
        context.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        handler = new Handler();
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d(TAG, "Location background service start command " + intent.getAction());

        if (intent.getAction().equals(GlobalConstant.Action.LOCATION_COMMAND_START)) {

            mLocationMode = intent.getIntExtra(GlobalConstant.Bundle.COMMAND_MODE, CommandLocation.CommandMode.SINGLE);
            mLocationRequest = intent.getParcelableExtra(GlobalConstant.Bundle.LOCATION_REQUEST);
            fallBackToLastLocationTime = intent.getLongExtra(GlobalConstant.Bundle.FALLBACK_TIME, NO_FALLBACK);
            resultReceiver = intent.getParcelableExtra(GlobalConstant.Bundle.RESULT_RECEIVER);
            resultCode = intent.getIntExtra(GlobalConstant.Bundle.RESULT_CODE, 0);

            if (mLocationRequest == null)
                throw new IllegalStateException("Location request can't be null");

            if (resultReceiver == null)
                throw new IllegalStateException("Result Receiver can't be null,unless you wont receive any result");

            requestLocationUpdates();

        } else if (intent.getAction().equals(GlobalConstant.Action.LOCATION_COMMAND_STOP)) {
            stopLocationService();
        }
        return START_NOT_STICKY;
    }

    @SuppressWarnings("MissingPermission")
    private void requestLocationUpdates() {
        if (mLocationRequest != null) {
            startFallbackToLastLocationTimer();
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, locationCallback, null).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Location update applied successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Error in appling location update");
                }
            });


        } else {
            throw new IllegalStateException("Location request can't be null");
        }
    }

    @SuppressWarnings("MissingPermission")
    private void startFallbackToLastLocationTimer() {
        if (fallBackToLastLocationTime != NO_FALLBACK) {
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LocationServices.getFusedLocationProviderClient(context).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Log.d(TAG, "Last known location found");
                                onLocationChanged(location);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error in getting last known location");
                            e.printStackTrace();
                        }
                    });
                }
            }, fallBackToLastLocationTime);
        }
    }

    private void stopLocationService() {
        if (handler != null)
            handler.removeCallbacksAndMessages(null);

        LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(locationCallback);
        stopSelf();
    }

    public void onLocationChanged(Location location) {
        Log.d(TAG, " location received");

        Bundle bundle = new Bundle();

        if (location != null) {
            bundle.putString(GlobalConstant.Bundle.ACTION, GlobalConstant.Action.LOCATION_RECEIVED);
            bundle.putParcelable(GlobalConstant.Bundle.LOCATION, location);
        } else {
            bundle.putString(GlobalConstant.Bundle.ACTION, GlobalConstant.Action.NO_LOCATION_RECEIVED);
        }

        resultReceiver.send(resultCode, bundle);

        if (mLocationMode == CommandLocation.CommandMode.SINGLE)
            stopLocationService();
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            onLocationChanged(locationResult.getLastLocation());
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);

        }
    };

}
