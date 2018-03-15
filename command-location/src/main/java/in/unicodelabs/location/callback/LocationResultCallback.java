package in.unicodelabs.location.callback;

import android.location.Location;

/**
 * Created by saurabh on 14/3/18.
 */

public interface LocationResultCallback {
    void onLocationReceived(Location location);
    void noLocationReceived();
}
