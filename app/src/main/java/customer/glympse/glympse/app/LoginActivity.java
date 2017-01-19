package customer.glympse.glympse.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import customer.glympse.glympse.R;
import customer.glympse.glympse.data.DataManager;
import customer.glympse.glympse.data.local.PrefsHelper;
import customer.glympse.glympse.utils.ApplicationMetadata;
import customer.glympse.glympse.utils.CommonMethods;
import customer.glympse.glympse.utils.DialogFactory;

public class LoginActivity extends BaseActivity {
    private static final int LOGIN_PERMISSIONS_REQUEST = 10;
    @BindView(R.id.et_emailId) EditText et_emailId;
    @BindView(R.id.et_password) EditText et_password;
    private String[] permissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((TextView)findViewById(R.id.tv_toolbarHeader)).setText(getString(R.string.title_login));
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_login)
    public void login() {
        if (hasPermission(permissions[0])) {
            loginUser();
        } else {
            requestPermissions(permissions,LOGIN_PERMISSIONS_REQUEST);
        }
    }
    @OnClick(R.id.tv_forgotPassword)
    public void forgotPassward() {
        Intent loginIntent = new Intent(this, ForgetPasswordActivity.class);
        startActivity(loginIntent);
    }

    @OnClick(R.id.btn_register)
    public void registerNow() {
        Intent loginIntent = new Intent(this, RegisterActivity.class);
        startActivity(loginIntent);
    }
    private void loginUser() {
        if (validCredentdial()) {

            Map<String,String> loginDetails = new HashMap<>();
            loginDetails.put("ent_email",getString(et_emailId));
            loginDetails.put("ent_password",getString(et_password));
            loginDetails.put("ent_device_type", "1");
            loginDetails.put("ent_device_token",(new PrefsHelper(this).getPref(ApplicationMetadata.DEVICE_TOKEN) != null)?(String)new PrefsHelper(this).getPref(ApplicationMetadata.DEVICE_TOKEN):"");
            loginDetails.put("ent_device_id", CommonMethods.getDeviceId(this));

            DataManager dataManager = new DataManager(this);
            dataManager.login(loginDetails);
        }
    }

    private boolean validCredentdial() {
        if (!CommonMethods.isEmailValid(this,et_emailId.getText().toString().trim())) {
            return false;
        }else if (et_password.getText().toString().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(this, R.string.title_attention,R.string.valid_msg_empty_password).show();
            return false;
        } else if (et_password.getText().toString().length() < 6) {
            DialogFactory.createSimpleOkErrorDialog(this,R.string.title_attention,R.string.msg_password_lenght).show();
            return false;
        }
        return true;
    }

    private String getString(EditText editText) {
        if (editText != null) {
            return editText.getText().toString();
        }
        return "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOGIN_PERMISSIONS_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loginUser();
                } else {
                    DialogFactory.createSimpleOkErrorDialog(this,
                            R.string.title_permissions,
                            R.string.permission_not_accepted_read_phone_state).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
