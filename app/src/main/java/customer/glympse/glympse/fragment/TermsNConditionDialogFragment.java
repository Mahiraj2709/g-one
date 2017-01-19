package customer.glympse.glympse.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import customer.glympse.glympse.R;

/**
 * Created by admin on 12/6/2016.
 */

public class TermsNConditionDialogFragment extends DialogFragment {
    @BindView(R.id.tv_supportText)
    TextView tv_supportText;
    @BindView(R.id.iv_closeDialog)
    ImageView iv_closeDialog;
    int mNum;
    public static TermsNConditionDialogFragment newInstance(String content) {
        TermsNConditionDialogFragment f = new TermsNConditionDialogFragment();

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
        View v = inflater.inflate(R.layout.term_condition_fragment, container, false);
        ButterKnife.bind(this,v);
        String content = getArguments().getString("content");
        tv_supportText.setText(Html.fromHtml(content));
        return v;
    }

    @OnClick(R.id.iv_closeDialog)
    public void closeDialog() {
        this.dismiss();
    }
}
