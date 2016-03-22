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
package me.fattycat.kun.teamwork.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.TWAccessToken;
import me.fattycat.kun.teamwork.TWApi;
import me.fattycat.kun.teamwork.TWRetrofit;
import me.fattycat.kun.teamwork.model.ProjectModel;
import me.fattycat.kun.teamwork.model.UserProfileModel;
import me.fattycat.kun.teamwork.util.LogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "TW_MainActivity";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.nav_view)
    NavigationView mNavView;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private CircleImageView mProfileImage;
    private TextView mProfileName;
    private TextView mProfileDesc;

    private Context mContext;
    private SharedPreferences mSPUserProfile;
    private List<ProjectModel> mProjectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSPUserProfile = getSharedPreferences(getString(R.string.text_sp_user_profile_key), MODE_PRIVATE);
        setSupportActionBar(mToolbar);

        initView();
        loadUserProfile();
        getUserProfile();
        getAllProjects();
    }

    private void initView() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavView.setNavigationItemSelectedListener(this);

        View navHeadView = mNavView.getHeaderView(0);
        mProfileImage = (CircleImageView) navHeadView.findViewById(R.id.profile_image);
        mProfileName = (TextView) navHeadView.findViewById(R.id.profile_name);
        mProfileDesc = (TextView) navHeadView.findViewById(R.id.profile_description);

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserProfile();
            }
        });
    }

    private void loadUserProfile() {
        // FIXME: 16/3/17 add default avatar
        if (mSPUserProfile.getString(getString(R.string.text_sp_user_profile_uid), null) != null) {
            Picasso.with(mContext).load(Uri.parse(mSPUserProfile.getString(getString(R.string.text_sp_user_profile_avatar), null))).into(mProfileImage);
            mProfileName.setText(mSPUserProfile.getString(getString(R.string.text_sp_user_profile_display_name), getString(R.string.text_profile_name)));
            mProfileDesc.setText(mSPUserProfile.getString(getString(R.string.text_sp_user_profile_desc), getString(R.string.text_profile_description)));
        }
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
                    initProjectMenu();


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

    private void getAllProjects() {
        TWApi.AllProjectsService allProjectsService = TWRetrofit.createService(TWApi.AllProjectsService.class, TWAccessToken.getAccessToken());
        Call<List<ProjectModel>> allProjectsCall = allProjectsService.getAllProjects();
        allProjectsCall.enqueue(new Callback<List<ProjectModel>>() {
            @Override
            public void onResponse(Call<List<ProjectModel>> call, Response<List<ProjectModel>> response) {
                if (response.body() != null) {
                    mProjectList.clear();
                    mProjectList = response.body();
                    initProjectMenu();
                }
            }

            @Override
            public void onFailure(Call<List<ProjectModel>> call, Throwable t) {
                // FIXME: 16/3/22
            }
        });
    }

    private void initProjectMenu() {
        Menu menu = mNavView.getMenu();
        SubMenu projectMenu = menu.addSubMenu(getString(R.string.text_menu_project));
        int index = 0;
        for (ProjectModel model : mProjectList) {
            projectMenu.add(Menu.NONE, index++, Menu.NONE, model.getName()).setCheckable(true).setIcon(android.R.color.transparent);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        LogUtils.i(TAG, "navigation drawer item selected | id = " + id);

        if (id == R.id.nav_team) {

        } else if (id == R.id.nav_project) {

        } else if (id == R.id.nav_calendar) {

        } else if (id == 1) {

        } else if (id == 2) {

        } else if (id == 3) {

        } else if (id == 4) {

        } else if (id == 5) {

        }

        // FIXME: 16/3/22
        //mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}