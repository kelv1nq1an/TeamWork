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
package me.fattycat.kun.teamwork.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.TWAccessToken;
import me.fattycat.kun.teamwork.TWApi;
import me.fattycat.kun.teamwork.TWRetrofit;
import me.fattycat.kun.teamwork.model.UserProfileModel;
import me.fattycat.kun.teamwork.util.LogUtils;
import me.fattycat.kun.teamwork.util.ToastUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends BaseFragment {
    private static final String TAG = "TW_UserFragment";

    @Bind(R.id.profile_image)
    CircleImageView mProfileAvatar;
    @Bind(R.id.profile_name)
    TextView mProfileName;
    @Bind(R.id.profile_description)
    TextView mProfileDesc;

    private SharedPreferences mSPUserProfile;

    public UserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSPUserProfile = mContext.getSharedPreferences(getString(R.string.text_sp_user_profile_key), Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        loadUserProfile();
        getUserProfile();
    }

    private void loadUserProfile() {
        // FIXME: 16/3/17 add default avatar
        Picasso.with(mContext).load(Uri.parse(mSPUserProfile.getString(getString(R.string.text_sp_user_profile_avatar), null))).into(mProfileAvatar);
        mProfileName.setText(mSPUserProfile.getString(getString(R.string.text_sp_user_profile_display_name), getString(R.string.text_profile_name)));
        mProfileDesc.setText(mSPUserProfile.getString(getString(R.string.text_sp_user_profile_desc), getString(R.string.text_profile_description)));

        LogUtils.i(TAG, "loadUserProfile");
    }

    private void getUserProfile() {
        TWApi.UserProfileService userProfileService = TWRetrofit.createService(TWApi.UserProfileService.class, TWAccessToken.getAccessToken());
        Call<UserProfileModel> userProfileModelCall = userProfileService.getUserProfile();

        userProfileModelCall.enqueue(new Callback<UserProfileModel>() {
            @Override
            public void onResponse(Call<UserProfileModel> call, Response<UserProfileModel> response) {
                if (response.body() != null) {
                    String uid = response.body().getUid();
                    String avatar = response.body().getAvatar();
                    String name = response.body().getName();
                    String displayName = response.body().getDisplay_name();
                    String desc = response.body().getDesc();
                    String email = response.body().getEmail();
                    int online = response.body().getOnline();

                    SharedPreferences.Editor editor = mSPUserProfile.edit();
                    editor.clear()
                            .putString(getString(R.string.text_sp_user_profile_uid), uid)
                            .putString(getString(R.string.text_sp_user_profile_avatar), avatar)
                            .putString(getString(R.string.text_sp_user_profile_name), name)
                            .putString(getString(R.string.text_sp_user_profile_display_name), displayName)
                            .putString(getString(R.string.text_sp_user_profile_desc), desc)
                            .putString(getString(R.string.text_sp_user_profile_email), email)
                            .putInt(getString(R.string.text_sp_user_profile_online), online)
                            .apply();


                    loadUserProfile();

                    LogUtils.i(TAG, "getUserProfile | onResponse | name = " + name);

                } else {
                    // FIXME: 16/3/17 null on get user profile
                }
            }

            @Override
            public void onFailure(Call<UserProfileModel> call, Throwable t) {
                // FIXME: 16/3/17 failed to get user profile
            }
        });
    }

    @OnClick(R.id.profile_image)
    public void refreshUserProfile() {
        getUserProfile();

        ToastUtils.showShort("refresh");
    }
}
