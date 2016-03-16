package me.fattycat.kun.teamwork.ui;

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
import me.fattycat.kun.teamwork.util.ToastUtils;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    @Bind(R.id.login)
    MatchButton mBtnLogin;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        checkAuthorization();

        if (TWAccessToken.sIsAuthorized) {
            mBtnLogin.setVisibility(View.GONE);
            ToastUtils.showShort("authorized");

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            mBtnLogin.setVisibility(View.VISIBLE);
        }

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

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
    public void onAuthorizeSuccess(AuthorizeEvent event) {
        if (event.isAuthorized) {
            checkAuthorization();
        } else {
            // FIXME: 16/3/16 test
            ToastUtils.showShort("授权失败");
        }
    }
}
