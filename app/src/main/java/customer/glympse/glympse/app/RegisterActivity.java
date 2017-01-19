package customer.glympse.glympse.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import customer.glympse.glympse.R;
import customer.glympse.glympse.data.DataManager;
import customer.glympse.glympse.data.local.PrefsHelper;
import customer.glympse.glympse.helper.CaptureImage;
import customer.glympse.glympse.utils.ApplicationMetadata;
import customer.glympse.glympse.utils.CommonMethods;
import customer.glympse.glympse.utils.DialogFactory;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static customer.glympse.glympse.R.id.et_address;

public class RegisterActivity extends BaseActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 10;
    private static final int SELECT_PHOTO = 101;
    private static final int PIC_CROP = 12;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 2;
    private static final int LOGIN_PERMISSIONS_REQUEST = 10;
    @BindView(R.id.image_profile)
    CircleImageView image_profile;
    @BindView(R.id.et_name)
    EditText et_name;
    @BindView(R.id.et_emailId)
    EditText et_emailId;
    @BindView(R.id.et_address)
    EditText et_address;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.et_confirmPasswd)
    EditText et_confirmPasswd;
    @BindView(R.id.et_phoneNo)
    EditText et_phoneNo;
    @BindView(R.id.cb_term_n_condition)
    CheckBox cb_term_n_condition;
    @BindView(R.id.tv_terms_n_condition)
    TextView tv_terms_n_condition;
    private Uri imageUri = null;
    private Uri picUri = null;
    private String[] permissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((TextView) findViewById(R.id.tv_toolbarHeader)).setText(getString(R.string.title_register));
        ButterKnife.bind(this);

        tv_terms_n_condition.setPaintFlags(tv_terms_n_condition.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //this is to prepopulate all text fields
        //setTestData();
    }

    @OnClick(R.id.tv_login)
    public void goToLogin() {
        onBackPressed();
    }

    @OnClick(R.id.tv_terms_n_condition)
    public void showTermsNCondition() {
        PrefsHelper prefsHelper = new PrefsHelper(this);
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(ApplicationMetadata.PAGE_IDENTIFIER, ApplicationMetadata.TNC_CUSTOMER);
        requestParams.put(ApplicationMetadata.LANGUAGE, "en");

        DataManager dataManager = new DataManager(this);
        dataManager.getStaticPages(requestParams, ApplicationMetadata.TNC_CUSTOMER);
    }

    @OnClick(R.id.image_profile)
    public void getProfileImage() {
        if (CropImage.isExplicitCameraPermissionRequired(this)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            CropImage.startPickImageActivity(this);
        }
    }

    @OnClick(R.id.btn_register)
    public void registerUser() {
        if (hasPermission(permissions[0])) {
            registerCurrentUser();
        } else {
            requestPermissions(permissions, LOGIN_PERMISSIONS_REQUEST);
        }

    }

    private void registerCurrentUser() {
        if (validCredentials()) {

            Map<String, RequestBody> requestmap = new HashMap();
            requestmap.put("name", RequestBody.create(MediaType.parse("text/plain"), getString(et_name)));
            requestmap.put("email", RequestBody.create(MediaType.parse("text/plain"), getString(et_emailId)));
            requestmap.put("mobile", RequestBody.create(MediaType.parse("text/plain"), getString(et_phoneNo)));
            requestmap.put("password", RequestBody.create(MediaType.parse("text/plain"), getString(et_password)));
            requestmap.put("device_token", RequestBody.create(MediaType.parse("text/plain"), (new PrefsHelper(this).getPref(ApplicationMetadata.DEVICE_TOKEN) != null) ? (String) new PrefsHelper(this).getPref(ApplicationMetadata.DEVICE_TOKEN) : ""));
            requestmap.put("device_id", RequestBody.create(MediaType.parse("text/plain"), CommonMethods.getDeviceId(this)));
            requestmap.put("device_type", RequestBody.create(MediaType.parse("text/plain"), "1"));
            requestmap.put("address", RequestBody.create(MediaType.parse("text/plain"), getString(et_address)));

            if (imageUri != null) {
                File imageFile = new File(imageUri.getPath());
                requestmap.put("profile_pic\"; filename=\"pp.png\" ", RequestBody.create(MediaType.parse("image/*"), imageFile));
            }

            DataManager manager = new DataManager(this);
            manager.signUp(requestmap);
        }
    }

    private String getString(EditText editText) {
        if (editText != null) {
            return editText.getText().toString();
        }
        return "";
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                Bitmap thePic = null;
                try {
                    //imagePath = picUri.getPath();
                    thePic = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                image_profile.setImageBitmap(thePic);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .start(this);
    }

    private boolean validCredentials() {
        if (et_name.getText().toString().trim().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(this, R.string.title_attention, R.string.msg_invalid_name).show();
            return false;
        } else if (!CommonMethods.isEmailValid(this, et_emailId.getText().toString().trim())) {
            return false;
        } else if (!CommonMethods.isValidPhoneNo(this, et_phoneNo.getText().toString().trim())) {
            return false;
        } else if (et_phoneNo.getText().toString().trim().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(this, R.string.title_attention, R.string.msg_invalid_phone_no).show();
            return false;
        } else if (et_address.getText().toString().trim().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(this, R.string.title_attention, R.string.msg_invalid_address).show();
            return false;
        } else if (et_password.getText().toString().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(this, R.string.title_attention, R.string.valid_msg_empty_password).show();
            return false;
        } else if (et_password.getText().toString().length() < 6) {
            DialogFactory.createSimpleOkErrorDialog(this, R.string.title_attention, R.string.msg_password_lenght).show();
            return false;
        } else if (et_confirmPasswd.getText().toString().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(this, R.string.title_attention, R.string.msg_empty_confirm_password).show();
            return false;
        } else if (et_confirmPasswd.getText().toString().length() < 6) {
            DialogFactory.createSimpleOkErrorDialog(this, R.string.title_attention, R.string.msg_password_lenght).show();
            return false;
        } else if (!et_confirmPasswd.getText().toString().equals(et_password.getText().toString())) {
            DialogFactory.createSimpleOkErrorDialog(this, R.string.title_attention, R.string.password_not_match).show();
            return false;
        } else if (!cb_term_n_condition.isChecked()) {
            DialogFactory.createSimpleOkErrorDialog(this, R.string.title_attention, R.string.msg_accept_tnc).show();
            return false;
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_image_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        CaptureImage captureImage = new CaptureImage(this);
        switch (item.getItemId()) {
            case R.id.open_gallery:
//                captureImage.openGallery();
                picUri = null;
                performCrop();
                return true;
            case R.id.open_camera:

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                } else {
                    picUri = captureImage.openCamera();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void performCrop() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(picUri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, PIC_CROP);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CaptureImage captureImage = new CaptureImage(this);
                    picUri = captureImage.openCamera();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

            }
            break;
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    /*Intent sosIntent = new Intent(this, MapActivity.class);
                    startActivity(sosIntent);*/
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            break;

            case LOGIN_PERMISSIONS_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    registerCurrentUser();
                } else {
                    DialogFactory.createSimpleOkErrorDialog(this,
                            R.string.title_permissions,
                            R.string.permission_not_accepted_read_phone_state).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

                // other 'case' lines to check for other
                // permissions this app might request
        }
    }

    private void setTestData() {
        et_name.setText("Mahiraj");
        et_emailId.setText("mahiraj@onsinteractive.com");
        et_phoneNo.setText("7065257289");
        et_password.setText("mahiraj");
        et_confirmPasswd.setText("mahiraj");
        cb_term_n_condition.setChecked(true);
    }

}
