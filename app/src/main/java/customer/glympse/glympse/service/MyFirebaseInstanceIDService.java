package customer.glympse.glympse.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import customer.glympse.glympse.data.local.PrefsHelper;
import customer.glympse.glympse.utils.ApplicationMetadata;

/**
 * Created by admin on 12/20/2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        //save this deivice token
        new PrefsHelper(getBaseContext()).savePref(ApplicationMetadata.DEVICE_TOKEN, refreshedToken);
    }
}
