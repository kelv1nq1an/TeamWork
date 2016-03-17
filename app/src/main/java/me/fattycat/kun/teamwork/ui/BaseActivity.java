/*
 * TeamWork
 * Copyright (C) 2015  FattycatR
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program;  if not, see <http://www.gnu.org/licenses/>.
 */
package me.fattycat.kun.teamwork.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.TWAccessToken;
import me.fattycat.kun.teamwork.TWApi;
import me.fattycat.kun.teamwork.TWRetrofit;
import me.fattycat.kun.teamwork.TWSecret;
import me.fattycat.kun.teamwork.event.AuthorizeEvent;
import me.fattycat.kun.teamwork.model.AccessTokenBody;
import me.fattycat.kun.teamwork.model.AccessTokenModel;
import me.fattycat.kun.teamwork.util.LogUtils;
import me.fattycat.kun.teamwork.util.ToastUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "TW_BaseActivity";

    private SharedPreferences mSPAuthorize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSPAuthorize = getSharedPreferences(getString(R.string.text_sp_authorize_key), MODE_PRIVATE);
    }

    public void loadAuthorization() {
        boolean isAuthorized = mSPAuthorize.getBoolean(getString(R.string.text_sp_authorize_flag), false);

        if (isAuthorized) {
            String accessToken = mSPAuthorize.getString(getString(R.string.text_sp_authorize_access_token), null);
            String refreshToken = mSPAuthorize.getString(getString(R.string.text_sp_authorize_refresh_token), null);

            if (!TextUtils.isEmpty(accessToken) && !TextUtils.isEmpty(refreshToken)) {
                TWAccessToken.init(TWAccessToken.AUTHORIZED, accessToken, refreshToken);

                LogUtils.i(TAG, "loadAuthorization | authorized");
            } else {
                TWAccessToken.init(TWAccessToken.NOTAUTHORIZED);

                LogUtils.i(TAG, "loadAuthorization | not authorized");
            }

        } else {
            TWAccessToken.init(TWAccessToken.NOTAUTHORIZED);

            LogUtils.i(TAG, "loadAuthorization | not authorized");
        }
    }

    public void getAccessToken(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith(TWSecret.REDIRECT_URI)) {
            String code = uri.getQueryParameter("code");

            LogUtils.i(TAG, "getAccessToken | code = " + code);

            if (code != null) {
                TWApi.AccessTokenService accessTokenService = TWRetrofit.createService(TWApi.AccessTokenService.class);
                Call<AccessTokenModel> accessTokenCall = accessTokenService.getAccessToken(new AccessTokenBody(TWSecret.CLIENT_ID, TWSecret.CLIENT_SECRET, code));

                accessTokenCall.enqueue(new Callback<AccessTokenModel>() {
                    @Override
                    public void onResponse(Call<AccessTokenModel> call, Response<AccessTokenModel> response) {
                        if (response.body() != null) {
                            if (response.body().getAccess_token() != null) {
                                String accessToken = response.body().getAccess_token();
                                String refreshToken = response.body().getRefresh_token();

                                SharedPreferences.Editor editor = mSPAuthorize.edit();
                                editor.putBoolean(getString(R.string.text_sp_authorize_flag), TWAccessToken.AUTHORIZED)
                                        .putString(getString(R.string.text_sp_authorize_access_token), accessToken)
                                        .putString(getString(R.string.text_sp_authorize_refresh_token), refreshToken)
                                        .apply();

                                EventBus.getDefault().post(new AuthorizeEvent(true));

                                LogUtils.i(TAG, "getAccessToken | onResponse | access_token = " + accessToken);

                            } else {
                                // FIXME: 16/3/17
                                ToastUtils.showShort("error");
                            }
                        } else {
                            // FIXME: 16/3/17
                            ToastUtils.showShort("null");
                        }

                    }

                    @Override
                    public void onFailure(Call<AccessTokenModel> call, Throwable t) {
                        EventBus.getDefault().post(new AuthorizeEvent(false));

                        LogUtils.i(TAG, "getAccessToken | onFailure");

                    }
                });

            } else if (uri.getQueryParameter("error") != null) {
                ToastUtils.showShort("授权失败");
                EventBus.getDefault().post(new AuthorizeEvent(false));
            }
        }
    }

}
