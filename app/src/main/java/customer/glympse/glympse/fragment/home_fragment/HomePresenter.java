package customer.glympse.glympse.fragment.home_fragment;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by admin on 1/3/2017.
 */

public interface HomePresenter {
    void onMapReady();

    void initialSetup();
    void searchLocation();

    void setSearchPlace(Place place);
    void enableCurrentLocation();

    void openSendRequest(String message);
    void onCameraMove();
    void onCameraIdle(LatLng centerLatLng);
    void moveToCurrentLocation();

    void connectToGoogleApiClient();

    void onResume();
    void onStop();
    void onPause();

    void infoWindowClicked(Marker marker);

    void getAllProviders();
}
