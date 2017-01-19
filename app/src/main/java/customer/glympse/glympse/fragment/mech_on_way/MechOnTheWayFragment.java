package customer.glympse.glympse.fragment.mech_on_way;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import customer.glympse.glympse.GlympseApplication;
import customer.glympse.glympse.R;
import customer.glympse.glympse.app.MainActivity;
import customer.glympse.glympse.data.model.MechanicDetail;
import customer.glympse.glympse.helper.DirectionsJSONParser;
import customer.glympse.glympse.model.Customer;
import customer.glympse.glympse.utils.ApplicationMetadata;
import customer.glympse.glympse.utils.LocationUtils;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by admin on 12/27/2016.
 */

public class MechOnTheWayFragment extends Fragment implements OnMapReadyCallback,MechOnWayMapView{
    private static final String TAG = MechOnTheWayFragment.class.getSimpleName();
    private static final String MECH_DETAIL = "mech_details";
    private MainActivity activity;
    @BindView(R.id.image_profile)
    CircleImageView image_profile;
    @BindView(R.id.tv_mechName) TextView tv_mechName;
    @BindView(R.id.rating_company) RatingBar rating_company;
    @BindView(R.id.btn_callMech) TextView btn_callMech;
    @BindView(R.id.btn_cancelReq) TextView btn_cancelReq;
    private GoogleMap map;
    private MapPresenter presenter;

    public static MechOnTheWayFragment newInstance(String mechId,boolean arrived) {
        MechOnTheWayFragment fragment = new MechOnTheWayFragment();
        Bundle data = new Bundle();
        data.putString("args",mechId);
        data.putBoolean("arrived",arrived);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) (getActivity());
        ((TextView)((MainActivity) getActivity()).findViewById(R.id.tv_toolbarHeader)).setText(getString(R.string.title_mechanic_on_the_way));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mechanic_on_the_way_fragment,container,false);
        ButterKnife.bind(this, view);
        GlympseApplication.getBus().register(this);
        presenter = new MapPresenterImp(this, getActivity(), getContext());
        presenter.setMechanicId(getArguments().getString("args"));

        if (getArguments().getBoolean("arrived")) {
            presenter.mechanicArrived("Mechanic arrived");
        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        presenter.onMapReady();
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


    @Override
    public void generateMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void updateLocationOnMap(Location location) {

    }

    @Override
    public void setMechanicDetails(MechanicDetail mechachicDetails) {
        Glide.with(this)
                .load(ApplicationMetadata.IMAGE_BASE_URL + mechachicDetails.getProfilePic())
                .thumbnail(0.2f)
                .centerCrop()
                .error(R.drawable.ic_profile_photo)
                .into(image_profile);

        tv_mechName.setText(mechachicDetails.getName());
        rating_company.setRating(Float.parseFloat((mechachicDetails.getRating() != null)?mechachicDetails.getRating():"0.0"));
    }

    @OnClick(R.id.btn_callMech)
    public void callMechanic() {
        presenter.callMechanic();
    }

    @Override
    public void drawPolylines(List<LatLng> latLngList) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LocationUtils.computeCentroid(latLngList), ApplicationMetadata.MAP_ZOOM_VALUE));
        map.addMarker(new MarkerOptions().position(latLngList.get(0)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_com_repair)).title("4MIN"));
        map.addMarker(new MarkerOptions().position(latLngList.get(1)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_pin)));

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(LocationUtils.getDirectionsUrl(latLngList.get(0),latLngList.get(1)));
    }

    @OnClick(R.id.btn_cancelReq)
    public void cancelRequest() {
        presenter.cancelRequest();
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            try{
                URL finalUrl = new URL(url[0]);

                urlConnection = (HttpURLConnection) finalUrl.openConnection();
                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb  = new StringBuffer();

                String line = "";
                while( ( line = br.readLine())  != null){
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            }catch(Exception e){
                Log.d("Exception while downloading url", e.toString());
            }finally{
                try {
                    iStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
            Log.i(TAG, url[0]+"*****" +data);
            return data;
            // For storing data from web service
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(getResources().getColor(R.color.mapLineColor));
            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }


    @Subscribe
    public void getNotification(Customer data) {
//        Toast.makeText(getActivity(), data.message, Toast.LENGTH_LONG).show();

        if (Integer.parseInt(data.status) == ApplicationMetadata.NOTIFICATION_MECH_ARRIVED) {
            //customer has arrived
            presenter.mechanicArrived(data.message);
        } else if (Integer.parseInt(data.status) == ApplicationMetadata.NOTIFICATION_TASK_FINISH) {
            //mechanic has finished the task
            presenter.taskFinishedByMech(data.message);
        }
    }

    @Override
    public void disableCancel() {
        btn_cancelReq.setVisibility(View.GONE);
    }


    @Override
    public void setMap(LatLng mechCurrentLoc, LatLng customerLatLng) {

    }
}
