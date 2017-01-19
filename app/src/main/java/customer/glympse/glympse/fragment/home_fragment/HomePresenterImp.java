package customer.glympse.glympse.fragment.home_fragment;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import customer.glympse.glympse.Globals;
import customer.glympse.glympse.app.MainActivity;
import customer.glympse.glympse.data.DataManager;
import customer.glympse.glympse.data.local.PrefsHelper;
import customer.glympse.glympse.data.model.MechanicDetail;
import customer.glympse.glympse.data.model.Provider;
import customer.glympse.glympse.fragment.RequestDialogFragment;
import customer.glympse.glympse.fragment.mech_on_way.MechOnTheWayFragment;
import customer.glympse.glympse.model.AllMechanic;
import customer.glympse.glympse.utils.ApplicationMetadata;
import customer.glympse.glympse.utils.DateUtil;
import customer.glympse.glympse.utils.DialogFactory;


/**
 * Created by admin on 1/3/2017.
 */

public class HomePresenterImp implements HomePresenter,LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = HomePresenterImp.class.getSimpleName();
    private static final long INTERVAL = 1000 * 60 * 1; //1 minute
    private static final long FASTEST_INTERVAL = 1000 * 60 * 1; // 1 minute
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLocation;
    private String mLastUpdateTime;
    private HomeView view = null;
    private Fragment homeFragment = null;
    private Context context = null;
    private Place currentPlace = null;
    private PrefsHelper prefsHelper = null;
    private DataManager dataManager = null;
    private LatLng cameraLatLng = new LatLng(0.0f,0.0f);
    private boolean setMapForFirstTime = false;
    private boolean cameraMovedManually = true;
    private String selectedServiceId = "0";
    private String currentLocationName = null;
    private List<Provider> providerList= null;
    public HomePresenterImp(HomeView view, Fragment homeFragment, Context context) {
        this.view = view;

        this.homeFragment = homeFragment;
        this.context = context;
        this.view.generateMap();

        prefsHelper = new PrefsHelper(context);
        dataManager = new DataManager(context);
        buildGoogleApiClient();
    }

    @Override
    public void onMapReady() {

    }


    @Override
    public void initialSetup() {
        view.initialSetup();
    }

    @Override
    public void searchLocation() {
        view.searchLocation();
    }

    @Override
    public void setSearchPlace(Place place) {
        this.cameraMovedManually = false;
        currentPlace = place;
        view.changeAddress(place.getAddress().toString());
        currentLocationName = place.getAddress().toString();
        view.setCurrentLocation(place.getLatLng());
    }

    @Override
    public void enableCurrentLocation() {
        view.enableMapCurrentLocation();
    }
    @Override
    public void moveToCurrentLocation() {
        if (mLocation != null) {
            view.setCurrentLocation(new LatLng(mLocation.getLatitude(),mLocation.getLongitude()));
        }
    }
    @Override
    public void openSendRequest(String message) {

        if (currentLocationName == null) {
            DialogFactory.createSimpleOkErrorDialog(context,"Loading address!").show();
            return;
        } else if (message.isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(context,"Please describe your need!").show();
            return;
        }
        if (providerList != null && providerList.size() > 0) {
            final DialogFragment fragment = RequestDialogFragment.newInstance("no content");
            fragment.show(homeFragment.getFragmentManager(),"request_dialog");

            Map<String, String> requestParams = new HashMap<>();
            requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
            if (Globals.getUserLatLng() == null) {
                return;
            }
            requestParams.put(ApplicationMetadata.ADDRESS_LATITUDE, Globals.getUserLatLng().latitude +"");
            requestParams.put(ApplicationMetadata.ADDRESS_LONGITUDE, Globals.getUserLatLng().longitude +"");
            requestParams.put(ApplicationMetadata.ADDRESS, currentLocationName);
            requestParams.put(ApplicationMetadata.MESSAGE, message);
            Log.i(TAG,DateUtil.getCurrentDate());
            DataManager dataManager = new DataManager(context);
            dataManager.setCallback(new DataManager.RequestCallback() {
                @Override
                public void Data(Object data) {

                    fragment.dismiss();

                    if (((String) data).contains("request_complete")) {
                        String mechId = ((String)data).split(":")[1];
                        Fragment mechOnWayFragment = MechOnTheWayFragment.newInstance(mechId,false);
                        ((MainActivity)context).addFragmentToStack(mechOnWayFragment,"mech_on_way_fragment");
                    }
                }
            });
            dataManager.sendRequest(requestParams);

        } else {
            DialogFactory.createSimpleOkErrorDialog(context, "No providers!").show();
        }
    }

    @Override
    public void onCameraMove() {
            view.hideViews();
    }

    @Override
    public void onCameraIdle(LatLng centerLatLng) {
        cameraLatLng = centerLatLng;
        Log.i(TAG, centerLatLng.toString());
        Globals.setUserLatLng(centerLatLng);


            //get all the mech again
            getAllProviders();
            view.showViews();

        //get address only when camera is moved manually, on move by serarch place don't load address
        if (cameraMovedManually) {
            getAddressFromLatLong(centerLatLng);
        } else {
            cameraMovedManually = true;
        }
    }

    @Override
    public void getAllProviders() {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        if (Globals.getUserLatLng() == null) {
            return;
        }
        requestParams.put(ApplicationMetadata.LATITUDE, Globals.getUserLatLng().latitude +"");
        requestParams.put(ApplicationMetadata.LONGITUDE, Globals.getUserLatLng().longitude +"");

        dataManager.setNearByProvidersCallback(new DataManager.NearByProvidersCallback() {

            @Override
            public void allProviders(List<Provider> providerList) {
                HomePresenterImp.this.providerList = providerList;
                view.showNearByProviders(providerList);
            }
        });

        dataManager.getNearbyProviders(requestParams);
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, location.getLatitude() + " longitude " + location.getLongitude());
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Log.i(TAG, "last update time for location is" + mLastUpdateTime);

        //move map to the current location
        //moveToLatLng();
        if (!setMapForFirstTime) {
            view.setCurrentLocation(new LatLng(location.getLatitude(),location.getLongitude()));
            setMapForFirstTime = true;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }
    private void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
        //got the the current location for the first time
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }
    @Override
    public void connectToGoogleApiClient() {
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPause() {
        stopLocationUpdates();
    }

    @Override
    public void infoWindowClicked(Marker marker) {

            String[] markerValues = marker.getTitle().split(":");
            /*Fragment fragment = CompanyInformationFragment.newInstance(markerValues[2],getMechById(markerValues[2]));
            ((MainActivity) context).addFragmentToStack(fragment, "company_information");*/

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private String getAddressFromLatLong(final LatLng latLng) {
        String addressFinal = "";
        //show address loading progress bar
        view.showAddressLoadingProgressBar();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context);
                String address = "";
                String state = "", city = "", pincode = "";
                try {
                    address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0);
                    state = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getLocality();
                    city = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAdminArea();
                    pincode = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getPostalCode();
                    Log.i("ADDRESS", address + "---" + state + "-----" + city + "=====" + pincode);
                    //addressFinal = address + ","+state;
//                    mapOperationListener.changeAddress(address + ","+ state);
                    view.changeAddress(address + ", "+ state);
                    currentLocationName = address +", "+state;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException es) {
                    Log.e(TAG,es.toString());
                }finally {
                 view.hideAddressLoadingProgressBar();
                }
            }
        }).start();
        return addressFinal;
    }

    //test data for the notification
    private AllMechanic testData() {
        AllMechanic allMechanic = new AllMechanic();
        allMechanic.total_offer = 10+"";
        allMechanic.request_id= 7+"";
        allMechanic.type = 2+"";
        allMechanic.message = "We found 10 offers for your request";
        allMechanic.mechanicList = new ArrayList<>();
        for(int i = 0; i< 10; i++) {
            AllMechanic.Mechanic singleMech = new AllMechanic().new Mechanic();
            singleMech.app_provider_id = i+"";
            singleMech.avg_rate = (i/2)+"";
            singleMech.offer_price = (i)+""+i;
            singleMech.offer_id = (i)+"";
            singleMech.latitude = "28.54"+i;
            singleMech.longitude = "77.39"+i;
            allMechanic.mechanicList.add(singleMech);
        }
        return allMechanic;
    }

    /*private AllMechanic.Mechanic getMechById(String mechId) {
        AllMechanic.Mechanic mechanic = new AllMechanic().new Mechanic();
        for (AllMechanic.Mechanic mech: allMechanic.mechanicList) {
            if (mech.app_provider_id.equals(mechId)) {
                mechanic = mech;
                return mechanic;
            }
        }
        return mechanic;
    }*/
}
