package customer.glympse.glympse.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import customer.glympse.glympse.GlympseApplication;
import customer.glympse.glympse.R;
import customer.glympse.glympse.adapter.ServiceHistoryAdapter;
import customer.glympse.glympse.app.MainActivity;
import customer.glympse.glympse.data.DataManager;
import customer.glympse.glympse.data.local.PrefsHelper;
import customer.glympse.glympse.data.model.ServiceHistory;
import customer.glympse.glympse.model.ServiceHisrotyResponse;
import customer.glympse.glympse.utils.ApplicationMetadata;
import customer.glympse.glympse.utils.DateUtil;

/**
 * Created by admin on 11/22/2016.
 */

public class ServiceHistoryFragment extends Fragment {
    private MainActivity activity;
    @BindView(R.id.tv_fromDate)
    TextView tv_fromDate;
    @BindView(R.id.tv_toDate)
    TextView tv_toDate;
    @BindView(R.id.rv_servicesView)
    RecyclerView rv_servicesView;

    private ServiceHistoryAdapter mServiceAdapter;
    private DataManager dataManager = null;
    private PrefsHelper prefsHelper = null;
    private List<ServiceHistory> mServiceList = new ArrayList<>();
    private String fromDate = DateUtil.getBackDate(), toDate = DateUtil.getCurrentDate();
    public static ServiceHistoryFragment newInstance(int args) {
        ServiceHistoryFragment fragment = new ServiceHistoryFragment();
        Bundle data = new Bundle();
        data.putInt("args", args);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) (getActivity());
        ((TextView) ((MainActivity) getActivity()).findViewById(R.id.tv_toolbarHeader)).setText(getString(R.string.title_service_history));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service_history_fragment, container, false);
        ButterKnife.bind(this, view);
        GlympseApplication.getBus().register(this);
        prefsHelper = new PrefsHelper(getContext());
        dataManager = new DataManager(getContext());
        loadServiceHistory(DateUtil.getBackDate(), DateUtil.getCurrentDate());
        tv_fromDate.setText(fromDate);
        tv_toDate.setText(toDate);
        setRecyclerView();
        return view;
    }

    private void setRecyclerView() {
        mServiceAdapter = new ServiceHistoryAdapter(mServiceList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rv_servicesView.setLayoutManager(mLayoutManager);
        rv_servicesView.setItemAnimator(new DefaultItemAnimator());
        rv_servicesView.setAdapter(mServiceAdapter);
        mServiceAdapter.setItemClickListener(new ServiceHistoryAdapter.MyClickListerer() {
            @Override
            public void onItemClick(int position, View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id",mServiceList.get(position).getAppRequestId());
                bundle.putString("date",mServiceList.get(position).getRequestDate());
                bundle.putString("location",mServiceList.get(position).getAddress());
                bundle.putString("need",mServiceList.get(position).getMsg());
                Fragment fragment = ServiceHistoryDetailFragment.newInstance(bundle);
                activity.addFragmentToStack(fragment, "service_detail_fragment");
            }
        });
    }

    @OnClick(R.id.rl_fromDate)
    void selectFromDate() {
        DialogFragment datePicker = DatePickerFragment.getInstance(0);
        datePicker.show(getActivity().getSupportFragmentManager(), "from_date");
    }

    @OnClick(R.id.rl_toDate)
    void selectToDate() {
        DialogFragment datePicker = DatePickerFragment.getInstance(1);
        datePicker.show(getActivity().getSupportFragmentManager(), "to_date");
    }

    @Subscribe
    public void getSelectedDate(DatePickerFragment.DateData dateData) {
        String [] dates = dateData.date.split(":");
        if (dateData.type == 0) {
            tv_fromDate.setText(dates[0]);
            fromDate = dates[1];
        }
        if (dateData.type == 1) {
            tv_toDate.setText(dates[0]);
            toDate = dates[1];
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //GlympseApplication.getBus().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void loadServiceHistory(String fromDate, String toDate) {

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        requestParams.put(ApplicationMetadata.FROM_DATE, fromDate);
        requestParams.put(ApplicationMetadata.TO_DATE, toDate);
        dataManager.setCallback(new DataManager.RequestCallback() {
            @Override
            public void Data(Object data) {
                mServiceList = (List<ServiceHistory>) data;
                setRecyclerView();
            }
        });
        dataManager.getHistory(requestParams);
    }
    @OnClick(R.id.ll_loadHistory)
    public void loadHistory() {
        loadServiceHistory(fromDate,toDate);
    }


}
