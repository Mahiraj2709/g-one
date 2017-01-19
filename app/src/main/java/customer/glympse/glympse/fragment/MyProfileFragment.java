package customer.glympse.glympse.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import customer.glympse.glympse.R;
import customer.glympse.glympse.app.MainActivity;
import customer.glympse.glympse.data.DataManager;
import customer.glympse.glympse.data.local.PrefsHelper;
import customer.glympse.glympse.utils.ApplicationMetadata;
import customer.glympse.glympse.utils.CommonMethods;
import customer.glympse.glympse.utils.ViewUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;

/**
 * Created by admin on 11/22/2016.
 */

public class MyProfileFragment extends Fragment {
    private static final String TAG = MyProfileFragment.class.getSimpleName();
    private MainActivity activity;
    @BindView(R.id.image_profile)
    CircleImageView image_profile;
    @BindView(R.id.et_name) EditText et_name;
    @BindView(R.id.et_emailId) EditText et_emailId;
    @BindView(R.id.et_phoneNo) EditText et_phoneNo;
    @BindView(R.id.et_address) EditText et_address;
    private EditText tempEditText = null;
    private Uri imageUri = null;

    public static MyProfileFragment newInstance(int args) {
        MyProfileFragment fragment = new MyProfileFragment();
        Bundle data = new Bundle();
        data.putInt("args",args);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) (getActivity());
        ((TextView)((MainActivity) getActivity()).findViewById(R.id.tv_toolbarHeader)).setText(getString(R.string.title_my_profile));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_profile_fragment,container,false);
        ButterKnife.bind(this, view);
        loadSharePrefsData();
        return view;
    }

    @OnClick(R.id.image_profile)
    public void getProfileImage() {
        if (CropImage.isExplicitCameraPermissionRequired(getActivity())) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            CropImage.startPickImageActivity(getActivity());
        }
    }

    @OnClick(R.id.btn_save)
    public void saveDetail() {

        if (!validField()) {
            return;
        }

        Map<String, RequestBody> requestmap = new HashMap();

        PrefsHelper prefsHelper = new PrefsHelper(getContext());

        requestmap.put(ApplicationMetadata.SESSION_TOKEN, RequestBody.create(MediaType.parse("text/plain"), prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN,"")));
        requestmap.put("name", RequestBody.create(MediaType.parse("text/plain"), getString(et_name)));
        requestmap.put("mobile", RequestBody.create(MediaType.parse("text/plain"), getString(et_phoneNo)));
        requestmap.put("address", RequestBody.create(MediaType.parse("text/plain"),getString(et_address)));

        if (imageUri != null) {
            File imageFile = new File(imageUri.getPath());
            Log.i(TAG,imageUri.getPath().toString());
            requestmap.put("profile_pic\"; filename=\"pp.png\" ", RequestBody.create(MediaType.parse("image/*"), imageFile));
        }

        DataManager manager = new DataManager(getContext());
        manager.editProfile(requestmap);
    }

    private boolean validField() {
        if (!CommonMethods.isValidPhoneNo(getContext(),et_phoneNo.getText().toString())) {
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

    private void loadSharePrefsData() {
        PrefsHelper prefsHelper = new PrefsHelper(getContext());
        et_name.setText(prefsHelper.getPref(ApplicationMetadata.USER_NAME, ""));
        et_emailId.setText(prefsHelper.getPref(ApplicationMetadata.USER_EMAIL, ""));
        et_phoneNo.setText(prefsHelper.getPref(ApplicationMetadata.USER_MOBILE, ""));
        et_address.setText(prefsHelper.getPref(ApplicationMetadata.ADDRESS, ""));

        //load image with glide
        Glide.with(this)
                .load(ApplicationMetadata.IMAGE_BASE_URL + prefsHelper.getPref(ApplicationMetadata.USER_IMAGE))
                .thumbnail(0.2f)
                .centerCrop()
                .error(R.drawable.ic_profile_photo)
                .into(image_profile);
    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.i(TAG,"image picked");
            Uri imageUri = CropImage.getPickImageResultUri(getContext(), data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(getContext(), imageUri)) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
//                Log.i()
                imageUri = result.getUri();
                Bitmap thePic = null;
                try {
                    //imagePath = picUri.getPath();
                    thePic = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
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
                .start(getActivity());
    }

    @OnClick(R.id.iv_editName)
    public void editName() {
        disableLastEditText(et_name);
        et_name.setEnabled(true);
        ViewUtil.focusEditText(getActivity(), et_name);
    }

    @OnClick(R.id.iv_editNumber)
    public void editPhoneNo() {
        disableLastEditText(et_phoneNo);
        et_phoneNo.setEnabled(true);
        ViewUtil.focusEditText(getActivity(), et_phoneNo);
    }

    @OnClick(R.id.iv_editAddress)
    public void editAddress() {
        disableLastEditText(et_address);
        et_address.setEnabled(true);
        ViewUtil.focusEditText(getActivity(), et_address);
    }

    private void disableLastEditText(EditText currentEditText) {
        if (tempEditText != null) {
            tempEditText.setEnabled(false);
        }
        tempEditText = currentEditText;
    }
}
