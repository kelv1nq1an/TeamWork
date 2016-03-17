package me.fattycat.kun.teamwork.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.roger.match.library.MatchButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.TWAccessToken;
import me.fattycat.kun.teamwork.TWApi;
import me.fattycat.kun.teamwork.event.AuthorizeEvent;
import me.fattycat.kun.teamwork.util.LogUtils;
import me.fattycat.kun.teamwork.util.ToastUtils;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "TW_LoginActivity";

    @Bind(R.id.login)
    MatchButton mBtnLogin;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loadAuthorization();
        checkAuthorization();

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void checkAuthorization() {
        if (TWAccessToken.sIsAuthorized) {
            mBtnLogin.setVisibility(View.GONE);

            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();

            LogUtils.i(TAG, "checkAuthorization | authorized");
        } else {
            mBtnLogin.setVisibility(View.VISIBLE);

            LogUtils.i(TAG, "checkAuthorization | not authorized");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        LogUtils.i(TAG, "onNewIntent | not authorized");

        getAccessToken(intent);
    }

    @OnClick(R.id.login)
    public void login() {
        Intent loginIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(TWApi.OAUTHURL));
        startActivity(loginIntent);
    }

    // Chrome Custom Tabs
    //@OnClick(R.id.login)
    public void requestAuth() {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        builder.build().launchUrl(this, Uri.parse(TWApi.OAUTHURL));
    }

    @Subscribe
    public void onAuthorize(AuthorizeEvent event) {

        LogUtils.i(TAG, "onAuthorize");

        if (event.isAuthorized) {
            loadAuthorization();
            checkAuthorization();
        } else {
            // FIXME: 16/3/16 test
            ToastUtils.showShort("授权失败");
        }
    }
}
