package customer.glympse.glympse.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import customer.glympse.glympse.utils.ApplicationMetadata;


/**
 * Created by admin on 12/29/2016.
 */

public class NotificationDialogActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String notificationData = getIntent().getStringExtra(ApplicationMetadata.NOTIFICATION_DATA);
        int notificationType = getIntent().getIntExtra(ApplicationMetadata.NOTIFICATION_TYPE,-1);
        String requestId = getIntent().getStringExtra(ApplicationMetadata.REQUEST_ID);

        if (notificationData != null && notificationType == ApplicationMetadata.NOTIFICATION_TASK_FINISH) { // NEW OFFER FROM CUSTOMER
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(ApplicationMetadata.NOTIFICATION_DATA, notificationData);
            intent.putExtra(ApplicationMetadata.NOTIFICATION_TYPE, notificationType);
            intent.putExtra(ApplicationMetadata.REQUEST_ID, requestId);
            startActivity(intent);
            finish();

        } else if (notificationData != null && notificationType == ApplicationMetadata.NOTIFICATION_MECH_ARRIVED) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(ApplicationMetadata.NOTIFICATION_DATA, notificationData);
            intent.putExtra(ApplicationMetadata.NOTIFICATION_TYPE, notificationType);
            startActivity(intent);
            finish();

        } else {
            /*final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setCanceledOnTouchOutside(false);
            View view = getLayoutInflater().inflate(R.layout.notification_dialog, null);
            Button button = (Button) view.findViewById(R.id.btn_dialog_ok);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    Intent intent = new Intent(NotificationDialogActivity.this, MainActivity.class);
                    intent.putExtra(ApplicationMetadata.NOTIFICATION_TYPE, notificationType);
                    intent.putExtra(ApplicationMetadata.NOTIFICATION_DATA, getIntent().getStringExtra(ApplicationMetadata.NOTIFICATION_DATA));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    NotificationDialogActivity.this.finish();
                }
            });

            alertDialog.setView(view);
            alertDialog.show();*/
        }
    }
}
