package in.unicodelabs.commandlocationdemo;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;

import in.unicodelabs.location.CommandLocation;
import in.unicodelabs.location.callback.LocationResultCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);

        CommandLocation.Builder builder =  new CommandLocation.Builder(this);
        builder.setLocationRequest(locationRequest);
        builder.setRequestMode(CommandLocation.CommandMode.REGULAR_UPDATE);
        builder.setFallBackToLastLocationTime(2000);
        builder.setLocationResultCallback(new LocationResultCallback() {
            @Override
            public void onLocationReceived(Location location) {
                Toast.makeText(MainActivity.this,location.getLatitude()+","+location.getLongitude(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void noLocationReceived() {
                Toast.makeText(MainActivity.this,"no location received",Toast.LENGTH_SHORT).show();
            }
        });
        builder.start();
    }
}
