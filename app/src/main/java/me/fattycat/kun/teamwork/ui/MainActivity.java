package me.fattycat.kun.teamwork.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.TWSecret;
import me.fattycat.kun.teamwork.TWApi;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private Context mContext;

    private String mOAuthUrl = TWApi.BASE_URL_OAUTH
            + "authorize?client_id=" + TWSecret.CLIENT_ID
            + "&redirect_uri=" + TWSecret.REDIRECT_URI;

    @Bind(R.id.accessToken)
    TextView accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith(TWSecret.REDIRECT_URI)) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                accessToken.setText(String.format("Code = %s", code));
            } else if (uri.getQueryParameter("error") != null) {
                accessToken.setText("Code Fetch Failure!");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(TWSecret.REDIRECT_URI)) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                accessToken.setText(String.format("Code = %s", code));
            } else if (uri.getQueryParameter("error") != null) {
                accessToken.setText("Code Fetch Failure!");
            }
        }
    }

    @OnClick(R.id.login)
    public void login() {
        Intent loginIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mOAuthUrl));
        startActivity(loginIntent);
    }

    //@OnClick(R.id.login) // Chrome Custom Tabs
    public void requestAuth() {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        builder.build().launchUrl(this, Uri.parse(mOAuthUrl));
    }
}
