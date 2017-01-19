package customer.glympse.glympse.fragment.home_fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import customer.glympse.glympse.Globals;
import customer.glympse.glympse.R;
import customer.glympse.glympse.app.MainActivity;
import customer.glympse.glympse.data.model.Provider;
import customer.glympse.glympse.fragment.RateYourMechFragment;
import customer.glympse.glympse.fragment.mech_on_way.MechOnTheWayFragment;
import customer.glympse.glympse.model.Customer;
import customer.glympse.glympse.utils.ApplicationMetadata;
import customer.glympse.glympse.utils.DialogFactory;
import customer.glympse.glympse.utils.LocationUtils;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * Created by admin on 11/22/2016.
 */

public class HomeFragment extends Fragment implements OnMapReadyCallback, HomeView, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener,GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int REQUEST_CHECK_SETTINGS = 11;
    private static final int REQUEST_PERMISSION_ACCESS_LOCATION = 12;
    private static final String TAG = HomeFragment.class.getSimpleName();
    private static View view;
    private GoogleMap map;
    private HomePresenter presenter = null;
    @BindView(R.id.tv_locationName)
    TextView tv_locationName;
    @BindView(R.id.iv_currentLocation)
    ImageView iv_currentLocation;
    @BindView(R.id.tv_place_marker)
    ImageView tv_place_marker;
    @BindView(R.id.pb_addressLoading)
    ProgressBar pb_addressLoading;
    @BindView(R.id.rl_bottomBar) RelativeLayout rl_bottomBar;
    @BindView(R.id.et_describe_need) EditText et_describe_need;

    private MainActivity activity;

    private int mapType = ApplicationMetadata.SHOW_ALL_MECH;
    public static HomeFragment newInstance(int args) {
        HomeFragment fragment = new HomeFragment();
        Bundle data = new Bundle();
        data.putInt("args", args);
        fragment.setArguments(data);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) (getActivity());

        String notificationData = getActivity().getIntent().getStringExtra(ApplicationMetadata.NOTIFICATION_DATA);
        int notificationType = getActivity().getIntent().getIntExtra(ApplicationMetadata.NOTIFICATION_TYPE,-1);
        if (notificationData != null && notificationType == ApplicationMetadata.NOTIFICATION_MECH_ARRIVED) { // NEW OFFER FROM CUSTOMER

            Fragment mechOnWayFragment = MechOnTheWayFragment.newInstance(getActivity().getIntent().getStringExtra(ApplicationMetadata.REQUEST_ID),true);
            ((MainActivity)context).addFragmentToStack(mechOnWayFragment,"mech_on_way_fragment");
        }else if (notificationData != null && notificationType == ApplicationMetadata.NOTIFICATION_TASK_FINISH) { // NEW OFFER FROM CUSTOMER
            RateYourMechFragment fragment = RateYourMechFragment.newInstance(getActivity().getIntent().getStringExtra(ApplicationMetadata.REQUEST_ID));
            ((MainActivity)getActivity()).addFragmentToStack(fragment,"rate_fragment");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.content_main, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }
        ButterKnife.bind(this, view);

        presenter = new HomePresenterImp(this, this, getContext());
        presenter.initialSetup();

        return view;
    }

    @OnClick(R.id.ll_searchLocation)
    public void launchSearchLocation() {
        presenter.searchLocation();
    }

    @OnClick(R.id.iv_currentLocation)
    public void moveToMyLocation() {
        presenter.moveToCurrentLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);
        map.setOnMarkerClickListener(this);
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoContents(Marker arg0) {
                return null;

            }

            @Override
            public View getInfoWindow(Marker marker) {
                View v = getActivity().getLayoutInflater().inflate(R.layout.info_window_layout, null);
                LatLng latLng = marker.getPosition();
                if(marker.getTitle() == null)
                    return null;
                String[] markerValues = marker.getTitle().split(":");
                /*ImageView[] stars = {
                        (ImageView) v.findViewById(R.id.iv_star_one),
                        (ImageView) v.findViewById(R.id.iv_star_two),
                        (ImageView) v.findViewById(R.id.iv_star_three),
                        (ImageView) v.findViewById(R.id.iv_star_four),
                        (ImageView) v.findViewById(R.id.iv_star_five),
                };*/

                /*int starCount = Integer.parseInt(markerValues[0]);
                for(int i = 0;i<starCount ; i++) {
                    stars[i].setImageResource(R.drawable.ic_star_yellow);
                }*/
                // Getting reference to the TextView to set longitude
                TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
                tv_name.setText(markerValues[0]);
                TextView tvLng = (TextView) v.findViewById(R.id.tv_price);
                // Setting the longitude
                tvLng.setText("Distance " + new DecimalFormat("#.00").format(Double.parseDouble(markerValues[1]))+"km");
                // Returning the view containing InfoWindow contents
                return v;
            }
        });
        map.setOnInfoWindowClickListener(this);
        presenter.onMapReady();
        //set map type

        String getAccountsPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        if (activity.hasPermission(getAccountsPermission)) {
            //show myLocation Button here
            presenter.enableCurrentLocation();
        } else {
            activity.requestPermissionsSafely(new String[]{getAccountsPermission},
                    REQUEST_PERMISSION_ACCESS_LOCATION);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.i(TAG, "Place: " + place.getName());
                //set the text view with this location
                presenter.setSearchPlace(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }

            switch (requestCode) {
                // Check for the integer request code originally supplied to startResolutionForResult().
                case REQUEST_CHECK_SETTINGS:
                    switch (resultCode) {
                        case RESULT_OK:
                            Log.e("Settings", "Result OK");
                            Toast.makeText(getActivity(), "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
                            //startLocationUpdates();
                            break;
                        case RESULT_CANCELED:
                            Log.e("Settings", "Result Cancel");
                            Toast.makeText(getActivity(), "GPS is disabaled in your device", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.enableCurrentLocation();
                } else {
                    DialogFactory.createSimpleOkErrorDialog(getActivity(),
                            R.string.title_permissions,
                            R.string.permission_not_accepted_access_location).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @OnClick(R.id.btn_sendRequest)
    public void sendRequest() {
        //check if the service list is there

        presenter.openSendRequest(et_describe_need.getText().toString());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

   /* public void updateMapForOnlineMech(ArrayList<Mechanic> onlineMechs) {
        //mapFragment.showOnlineMechanicOnMap(onlineMechs);
    }*/

    @Override
    public void generateMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void setMapType(int mapType) {
        //map type
    }

    @Override
    public void setCurrentLocation(LatLng currentLocation) {
        if (map != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, ApplicationMetadata.MAP_ZOOM_VALUE));
        }
    }

    @Override
    public void changeAddress(final String address) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_locationName.setText(address);
            }
        });
    }

    //when map is moved hide all view
    @Override
    public void hideViews() {
        rl_bottomBar.animate().translationY(400);
    }

    //when map is idle show all view
    @Override
    public void showViews() {
        rl_bottomBar.animate().translationY(0);
    }

    @Override
    public void showAddressLoadingProgressBar() {
        pb_addressLoading.setVisibility(View.VISIBLE);
        tv_place_marker.setVisibility(View.GONE);
    }

    @Override
    public void hideAddressLoadingProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pb_addressLoading.setVisibility(View.GONE);
                tv_place_marker.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void initialSetup() {
        LocationUtils locationUtils = new LocationUtils(getActivity());
        locationUtils.showSettingDialog();

        String notificationData = getActivity().getIntent().getStringExtra(ApplicationMetadata.NOTIFICATION_DATA);
        int notificationType = getActivity().getIntent().getIntExtra(ApplicationMetadata.NOTIFICATION_TYPE, -1);
        if (notificationData != null && notificationType == ApplicationMetadata.NOTIFICATION_REQ_ACCEPTED) {
            //presenter.setRequestAcceptedMech(new Gson().fromJson(notificationData, AllMechanic.class));
            mapType = ApplicationMetadata.SHOW_MECH_REQUEST;
        } else {

        }
    }

    @Override
    public void searchLocation() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    public void enableMapCurrentLocation() {
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
    }

    @Override
    public void showNearByProviders(List<Provider> providerList) {
        //show mechanic on the map
        if (map != null) {
            map.clear();

            //add center marker
            MarkerOptions centerMarker = new MarkerOptions().position(map.getCameraPosition().target);
            // Changing marker icon
            centerMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_pin));
            //Log.i(TAG,provider.getObj().getName());
            map.addMarker(centerMarker);
            //add inner circle
            map.addCircle(new CircleOptions()
                    .center(Globals.getUserLatLng())
                    .radius(300)
                    .strokeWidth(0f)
                    .fillColor(getResources().getColor(R.color.colorMapCircle)));

            //add outer circle
            map.addCircle(new CircleOptions()
                    .center(Globals.getUserLatLng())
                    .radius(600)
                    .strokeWidth(0f)
                    .fillColor(getResources().getColor(R.color.colorMapCircle)));

            for (Provider provider : providerList) {
                try {
                    LatLng latLng = new LatLng(provider.getObj().getLocation().getLatitude(), provider.getObj().getLocation().getLongitude());
                    MarkerOptions marker = new MarkerOptions().position(latLng).title((provider.getObj().getName() + ":" + provider.getDis()));
                    // Changing marker icon
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_com_repair));
                    Log.i(TAG,provider.getObj().getName());
                    map.addMarker(marker);
                } catch (NumberFormatException e) {
                    //the parseDouble failed and you need to handle it here
                    Log.e(TAG, e.toString());
                }
            }

            //map.animateCamera(CameraUpdateFactory.newLatLngZoom(77.398605f, 28.5409608f), ApplicationMetadata.MAP_ZOOM_VALUE));
        }
    }

    @Override
    public void onCameraIdle() {
        presenter.onCameraIdle(map.getCameraPosition().target);
    }

    @Override
    public void onCameraMoveStarted(int i) {
        presenter.onCameraMove();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        presenter.infoWindowClicked(marker);
    }

}
