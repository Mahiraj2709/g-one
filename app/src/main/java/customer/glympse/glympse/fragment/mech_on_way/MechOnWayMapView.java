package customer.glympse.glympse.fragment.mech_on_way;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import customer.glympse.glympse.data.model.MechanicDetail;

/**
 * Created by admin on 12/27/2016.
 */

public interface MechOnWayMapView {
    void updateLocationOnMap(Location location);
    void generateMap();
    void setMechanicDetails(MechanicDetail mechachicDetails);
    void callMechanic();
    void drawPolylines(List<LatLng> centerLatLng);

    void disableCancel();

    void setMap(LatLng mechCurrentLoc, LatLng customerLatLng);
}
