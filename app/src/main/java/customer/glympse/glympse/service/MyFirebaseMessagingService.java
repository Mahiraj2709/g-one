package customer.glympse.glympse.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import customer.glympse.glympse.GlympseApplication;
import customer.glympse.glympse.R;
import customer.glympse.glympse.app.MainActivity;
import customer.glympse.glympse.model.Customer;
import customer.glympse.glympse.utils.ApplicationMetadata;
import customer.glympse.glympse.utils.NotificationUtils;


/**
 * Created by admin on 12/20/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private Customer customer = null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        GlympseApplication.getBus().register(this);
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().containsKey("status")) {

            String payLoad = "";
            int notificationType = Integer.parseInt(remoteMessage.getData().get("status"));
            payLoad = new NotificationUtils().getData(remoteMessage.getData());
            Intent intent = null;


            if (GlympseApplication.isVisible) {
                customer = new Gson().fromJson(payLoad, Customer.class);

                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        GlympseApplication.getBus().post(customer);
                    }
                });

            } else {
                String message = "";
                if (remoteMessage.getData().containsKey("message")) {
                    message = remoteMessage.getData().get("message");
                }
                intent = new Intent(this, MainActivity.class);

                switch (notificationType) {
                /*case ApplicationMetadata.NOTIFICATION_NEW_OFFER:
                    intent.putExtra(ApplicationMetadata.NOTIFICATION_DATA, payLoad);
                    intent.putExtra(ApplicationMetadata.NOTIFICATION_TYPE, notificationType);
                    break;*/
                    case ApplicationMetadata.NOTIFICATION_REQ_ACCEPTED:
                        intent.putExtra(ApplicationMetadata.NOTIFICATION_DATA, payLoad);
                        intent.putExtra(ApplicationMetadata.NOTIFICATION_TYPE, notificationType);
                        break;
                    case ApplicationMetadata.NOTIFICATION_REQ_COMPLETED:
                        break;
                /*case ApplicationMetadata.NOTIFICATION_SEND_REQ:
                    intent.putExtra(ApplicationMetadata.NOTIFICATION_DATA, payLoad);
                    intent.putExtra(ApplicationMetadata.NOTIFICATION_TYPE, notificationType);
                    break;*/
                case ApplicationMetadata.NOTIFICATION_MECH_ARRIVED:
                    intent.putExtra(ApplicationMetadata.NOTIFICATION_DATA, payLoad);
                    intent.putExtra(ApplicationMetadata.NOTIFICATION_TYPE, notificationType);
                    intent.putExtra(ApplicationMetadata.REQUEST_ID, remoteMessage.getData().containsKey("request_id"));
                    break;
                case ApplicationMetadata.NOTIFICATION_TASK_FINISH:
                        intent.putExtra(ApplicationMetadata.NOTIFICATION_DATA, payLoad);
                        intent.putExtra(ApplicationMetadata.NOTIFICATION_TYPE, notificationType);
                        intent.putExtra(ApplicationMetadata.REQUEST_ID, remoteMessage.getData().get("request_id"));
                        break;
                    default:

                }

                    // use System.currentTimeMillis() to have a unique ID for the pending intent
                    PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

                    // build notification
                    // the addAction re-use the same intent to keep the example short
                    Notification n = new Notification.Builder(this)
                            .setContentTitle("Glympse")
                            .setContentText(message)
                            .setSmallIcon(R.drawable.ic_logo_2)
                            //.setColor(getColor(R.color.colorPrimary))
                            .setContentIntent(pIntent)
                            .setAutoCancel(true)
                            .build();

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    notificationManager.notify(0, n);
                }
            }
        }
    }
