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
import me.fattycat.kun.teamwork.TWApi;
import me.fattycat.kun.teamwork.TWRetrofit;
import me.fattycat.kun.teamwork.TWSecret;
import me.fattycat.kun.teamwork.model.AccessToken;
import me.fattycat.kun.teamwork.model.AccessTokenBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private Context mContext;

    private String mOAuthUrl = TWApi.BASE_URL_OAUTH
            + "authorize?client_id=" + TWSecret.CLIENT_ID
            + "&redirect_uri=" + TWSecret.REDIRECT_URI;

    @Bind(R.id.TWAccessToken)
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

        getAccessToken(intent);
    }

    private void getAccessToken(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith(TWSecret.REDIRECT_URI)) {
            String code = uri.getQueryParameter("code");

            if (code != null) {
                accessToken.setText(String.format("Code = %s", code));

                TWApi.AccessTokenService accessTokenService = TWRetrofit.createService(TWApi.AccessTokenService.class);
                Call<AccessToken> accessTokenCall = accessTokenService.getAccessToken(new AccessTokenBody(TWSecret.CLIENT_ID, TWSecret.CLIENT_SECRET, code));
                accessTokenCall.enqueue(new Callback<AccessToken>() {
                    @Override
                    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                        if (response.body() != null) {
                            if (response.body().getAccess_token() != null) {
                                accessToken.setText(response.body().getAccess_token() + "\n" + response.body().getExpires_in() + "\n" + response.body().getRefresh_token());
                            } else {
                                showToast(mContext, "error");
                            }
                        } else {
                            showToast(mContext, "null");
                        }

                    }

                    @Override
                    public void onFailure(Call<AccessToken> call, Throwable t) {

                    }
                });

            } else if (uri.getQueryParameter("error") != null) {
                showToast(mContext, "授权失败");
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
