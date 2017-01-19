package customer.glympse.glympse.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import customer.glympse.glympse.GlympseApplication;
import customer.glympse.glympse.R;
import customer.glympse.glympse.app.MainActivity;
import customer.glympse.glympse.model.ServiceHisrotyResponse;

/**
 * Created by admin on 11/24/2016.
 */

public class ServiceHistoryDetailFragment extends Fragment {
    private MainActivity activity;
    private static final String TAG = ServiceHistoryDetailFragment.class.getSimpleName();
    @BindView(R.id.tv_serviceDate) TextView tv_serviceDate;
    @BindView(R.id.tv_locationName) TextView tv_locationName;
    @BindView(R.id.tv_requestId) TextView tv_requestId;
    @BindView(R.id.tv_need) TextView tv_need;
    private Bundle bundle;
    private ServiceHisrotyResponse.ServiceHistory mServiceHistory;
    public static ServiceHistoryDetailFragment newInstance(Bundle args) {
        ServiceHistoryDetailFragment fragment = new ServiceHistoryDetailFragment();
        Bundle data = new Bundle();
        data.putBundle("args", args);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) (getActivity());
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.title_service_history_detail));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service_history_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        bundle = getArguments().getBundle("args");
        tv_serviceDate.setText(bundle.getString("date"));
        tv_requestId.setText("Request ID "+bundle.getString("id"));
        tv_locationName.setText(bundle.getString("location"));
        tv_need.setText(bundle.getString("need"));
        return view;
    }

    @Subscribe
    public void getSelectedServiceHistory(ServiceHisrotyResponse.ServiceHistory serviceHistory) {
        /*mServiceHistory = serviceHistory;
        Log.i(TAG,serviceHistory.servicePrice + " name" +serviceHistory.serviceName);
        tv_serviceDate.setText(serviceHistory.serviceDate);
        tv_servicePrice.setText(serviceHistory.servicePrice);*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}