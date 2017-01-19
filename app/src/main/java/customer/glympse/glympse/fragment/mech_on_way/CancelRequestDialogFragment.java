package customer.glympse.glympse.fragment.mech_on_way;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import customer.glympse.glympse.R;

/**
 * Created by admin on 12/27/2016.
 */

public class CancelRequestDialogFragment extends DialogFragment {
    @BindView(R.id.rg_cancelReasons) RadioGroup rg_cancelReasons;
    @BindView(R.id.btn_submit) Button btn_submit;
    private String message = "";
    private SubmitReqCallBack mCallback = null;

    public interface SubmitReqCallBack {
        void cancelRequest(String message);
    }
    public void setSubmitReqCallback(SubmitReqCallBack mCallback) {
        this.mCallback = mCallback;
    }
    int mNum;
    public static CancelRequestDialogFragment newInstance(String content) {
        CancelRequestDialogFragment f = new CancelRequestDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("content", content);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("num");

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.cancel_request_fragment, container, false);
        ButterKnife.bind(this,v);

        rg_cancelReasons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_mechLate:
                        message = "1";
                        break;
                    case R.id.rb_noContact:
                        message = "2";
                        break;
                    case R.id.rb_mechReqCancel:
                        message = "3";
                        break;
                    case R.id.rb_mechChangeMinc :
                        message = "4";
                        break;
                    case R.id.rb_other:
                        message = "5";
                        break;
                }
            }
        });
        return v;
    }

    @OnClick(R.id.btn_submit)
    public void submitRequest() {
        if (message.isEmpty()) {
            Snackbar snackbar = Snackbar
                    .make(getView(), "Please select reason!", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            this.dismiss();
            if (mCallback != null) {
                mCallback.cancelRequest(message);
            }
        }
    }
}
