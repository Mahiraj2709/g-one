package customer.glympse.glympse;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by admin on 12/12/2016.
 */

public class Globals {
    private static LatLng userLatLng;

    public static LatLng getUserLatLng() {
        return userLatLng;
    }

    public static void setUserLatLng(LatLng userLatLng) {
        Globals.userLatLng = userLatLng;
    }
}
