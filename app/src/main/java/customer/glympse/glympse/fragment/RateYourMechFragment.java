package customer.glympse.glympse.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import customer.glympse.glympse.R;
import customer.glympse.glympse.app.MainActivity;
import customer.glympse.glympse.data.DataManager;
import customer.glympse.glympse.data.local.PrefsHelper;
import customer.glympse.glympse.data.model.MechanicDetail;
import customer.glympse.glympse.fragment.home_fragment.HomeFragment;
import customer.glympse.glympse.utils.ApplicationMetadata;
import customer.glympse.glympse.utils.DialogFactory;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by admin on 12/27/2016.
 */

public class RateYourMechFragment extends Fragment {
    private static final String TAG = RateYourMechFragment.class.getSimpleName();
    private PrefsHelper prefsHelper = null;
    private DataManager dataManager = null;
    private String appRequestId = null;
    @BindView(R.id.image_profile) CircleImageView image_profile;
    @BindView(R.id.rating_company) RatingBar rating_company;
    @BindView(R.id.et_comment) EditText et_comment;
    private MechanicDetail mechanicDetail = null;

    public static RateYourMechFragment newInstance(String args) {
        RateYourMechFragment fragment = new RateYourMechFragment();
        Bundle data = new Bundle();
        data.putString("content", args);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((TextView) (getActivity()).findViewById(R.id.tv_toolbarHeader)).setText(getString(R.string.title_rate_your_mech));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rate_mech_fragment, container, false);
        ButterKnife.bind(this, view);
        this.appRequestId = getArguments().getString("content");
        prefsHelper = new PrefsHelper(getContext());
        dataManager = new DataManager(getContext());

        getMechanic();
        return view;
    }

    private void getMechanic() {
        //request mechaniic by id
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        requestParams.put(ApplicationMetadata.APP_REQUEST_ID, getArguments().getString("content")); // CUSTOMER ID
        this.appRequestId = appRequestId;
        dataManager.setCallback(new DataManager.RequestCallback() {
            @Override
            public void Data(Object data) {
                mechanicDetail = (MechanicDetail)data;
                setProfile();
            }
        });
        dataManager.getMechanicDetail(requestParams);
    }

    @OnClick(R.id.btn_submit)
    public void rateCustomer() {
        if (validateField()) {
            Map<String, String> requestParams = new HashMap<>();
            requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
            requestParams.put(ApplicationMetadata.RATING, rating_company.getNumStars()+"");
            requestParams.put(ApplicationMetadata.APP_REQUEST_ID, appRequestId);
            requestParams.put(ApplicationMetadata.REVIEW, et_comment.getText().toString());

            dataManager.setCallback(new DataManager.RequestCallback() {
                @Override
                public void Data(Object data) {
                    HomeFragment fragment = HomeFragment.newInstance(0);
                    ((MainActivity)getActivity()).addFragmentToStack(fragment,"home_fragment");
                }
            });

            dataManager.rateMechanic(requestParams);
        }
    }

    public boolean validateField() {
        if (et_comment.getText().toString().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(getContext(),"Please share your experience!").show();
            return false;
        }
        return true;
    }

    void setProfile() {
        Glide.with(this)
                .load(ApplicationMetadata.IMAGE_BASE_URL + mechanicDetail.getProfilePic())
                .thumbnail(0.2f)
                .centerCrop()
                .error(R.drawable.ic_profile_photo)
                .into(image_profile);
    }
}
