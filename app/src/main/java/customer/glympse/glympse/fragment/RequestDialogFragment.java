package customer.glympse.glympse.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import customer.glympse.glympse.R;

/**
 * Created by admin on 12/6/2016.
 */

public class RequestDialogFragment extends DialogFragment {
    int mNum;
    public static RequestDialogFragment newInstance(String content) {
        RequestDialogFragment f = new RequestDialogFragment();

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
        int style = DialogFragment.STYLE_NORMAL, theme = android.R.style.Theme_Black_NoTitleBar_Fullscreen;
        setStyle(style,theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.request_fragment, container, false);
        return v;
    }

    /*@OnClick(R.id.iv_closeDialog)
    public void closeDialog() {
        this.dismiss();
    }*/
}
