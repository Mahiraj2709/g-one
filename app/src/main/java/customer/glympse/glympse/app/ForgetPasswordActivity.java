package customer.glympse.glympse.app;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import customer.glympse.glympse.utils.ApplicationMetadata;
import customer.glympse.glympse.utils.CommonMethods;

public class ForgetPasswordActivity extends AppCompatActivity {
    @BindView(R.id.et_emailId)EditText et_emailId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((TextView)findViewById(R.id.tv_toolbarHeader)).setText(getString(R.string.title_forgot_password));
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_submit)
    public void submitEmail() {
        if (validEmail()) {
            DataManager manager = new DataManager(this);

            Map<String,String> forgotParams = new HashMap<>();
            forgotParams.put("email",et_emailId.getText().toString());
            forgotParams.put("language", ApplicationMetadata.LANG_ENGLISH);
            manager.forgotPassword(forgotParams);
        }
    }

    private boolean validEmail() {
        if (!CommonMethods.isEmailValid(this,et_emailId.getText().toString())) {
            return false;
        }
        return true;
    }

    @OnClick(R.id.tv_login)
    public void goToLogin() {
        onBackPressed();
    }
}
