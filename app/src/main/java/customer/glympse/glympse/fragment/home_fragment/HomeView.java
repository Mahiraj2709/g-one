package customer.glympse.glympse.fragment.home_fragment;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import customer.glympse.glympse.data.model.Provider;


/**
 * Created by admin on 1/3/2017.
 */

public interface HomeView {
    void generateMap();

    //Map type are two, 1)Show All online mechs 2)show all mech accepted request
    void setMapType(int mapType);
    void changeAddress(String address);

    void hideViews();
    void showViews();
    void showAddressLoadingProgressBar();
    void hideAddressLoadingProgressBar();
    void setCurrentLocation(LatLng currentLocation);

    void initialSetup();
    void searchLocation();
    void enableMapCurrentLocation();

    void showNearByProviders(List<Provider> nearbyProviderList);
}
