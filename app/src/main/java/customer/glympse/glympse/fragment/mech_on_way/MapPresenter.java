package customer.glympse.glympse.fragment.mech_on_way;


/**
 * Created by admin on 12/27/2016.
 */

public interface MapPresenter {
    void setMechanicId(String mechanicId);
    void connectToLocationService();
    void disconnectFromLocationService();
    void getCompanyProfile();
    void connectToGoogleApiClient();
    void onResume();
    void onStop();
    void onPause();

    void onMapReady();
    void callMechanic();



    void cancelRequest();

    void mechanicArrived(String message);

    void taskFinishedByMech(String message);
}
