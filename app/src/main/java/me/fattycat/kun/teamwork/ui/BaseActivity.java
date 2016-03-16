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
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.TWAccessToken;
import me.fattycat.kun.teamwork.TWApi;
import me.fattycat.kun.teamwork.TWRetrofit;
import me.fattycat.kun.teamwork.TWSecret;
import me.fattycat.kun.teamwork.model.AccessToken;
import me.fattycat.kun.teamwork.model.AccessTokenBody;
import me.fattycat.kun.teamwork.util.ToastUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity {

    public void checkAuthorization() {
        SharedPreferences spAuthorize = getSharedPreferences(getString(R.string.text_sp_authorize), MODE_PRIVATE);
        boolean isAuthorized = spAuthorize.getBoolean(getString(R.string.text_sp_authorize_flag), false);

        if (isAuthorized) {
            String accessToken = spAuthorize.getString(getString(R.string.text_sp_authorize_access_token), null);
            String refreshToken = spAuthorize.getString(getString(R.string.text_sp_authorize_refresh_token), null);

            if (!TextUtils.isEmpty(accessToken) && !TextUtils.isEmpty(refreshToken)) {
                TWAccessToken.init(TWAccessToken.AUTHORIZED, accessToken, refreshToken);
            }
        } else {
            TWAccessToken.init(TWAccessToken.NOTAUTHORIZED);
        }
    }


    public void getAccessToken(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith(TWSecret.REDIRECT_URI)) {
            String code = uri.getQueryParameter("code");

            if (code != null) {
                //accessToken.setText(String.format("Code = %s", code));

                TWApi.AccessTokenService accessTokenService = TWRetrofit.createService(TWApi.AccessTokenService.class);
                Call<AccessToken> accessTokenCall = accessTokenService.getAccessToken(new AccessTokenBody(TWSecret.CLIENT_ID, TWSecret.CLIENT_SECRET, code));
                accessTokenCall.enqueue(new Callback<AccessToken>() {
                    @Override
                    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                        if (response.body() != null) {
                            if (response.body().getAccess_token() != null) {
                                //accessToken.setText(response.body().getAccess_token() + "\n" + response.body().getExpires_in() + "\n" + response.body().getRefresh_token());
                            } else {
                                ToastUtils.showShort("error");
                            }
                        } else {
                            ToastUtils.showShort("null");
                        }

                    }

                    @Override
                    public void onFailure(Call<AccessToken> call, Throwable t) {

                    }
                });

            } else if (uri.getQueryParameter("error") != null) {
                ToastUtils.showShort("授权失败");
            }
        }
    }
}
