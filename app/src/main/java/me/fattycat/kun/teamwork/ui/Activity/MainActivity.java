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
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.TWAccessToken;
import me.fattycat.kun.teamwork.TWApi;
import me.fattycat.kun.teamwork.TWRetrofit;
import me.fattycat.kun.teamwork.TWSettings;
import me.fattycat.kun.teamwork.event.TaskListEvent;
import me.fattycat.kun.teamwork.model.EntryModel;
import me.fattycat.kun.teamwork.model.TaskModel;
import me.fattycat.kun.teamwork.model.TeamProjectModel;
import me.fattycat.kun.teamwork.model.UserProfileModel;
import me.fattycat.kun.teamwork.model.UserTeamListModel;
import me.fattycat.kun.teamwork.ui.adapter.MainTabPagerAdapter;
import me.fattycat.kun.teamwork.ui.fragment.EntryFragment;
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
    @Bind(R.id.main_tabs)
    TabLayout mTabLayout;
    @Bind(R.id.container)
    ViewPager mViewPager;

    private CircleImageView mProfileImage;
    private TextView mTvProfileName;
    private TextView mTvProfileDesc;

    private Context mContext;
    private SharedPreferences mSPUserProfile;
    private List<UserTeamListModel> mUserTeamList = new ArrayList<>();
    private List<TeamProjectModel> mTeamProjectList = new ArrayList<>();
    private List<TaskModel> mTaskModelAll = new ArrayList<>();
    private List<EntryModel> mEntryList = new ArrayList<>();
    private Map<String, String> mTitlesMap = new HashMap<>();
    private Map<String, List<TaskModel>> mTaskListMap = new HashMap<>();
    private ArrayAdapter<String> mUserTeamAdapter;
    private MainTabPagerAdapter mMainTabPagerAdapter = new MainTabPagerAdapter(getSupportFragmentManager());
    private String mPid;

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
        getUserTeamList();
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
        mTvProfileName = (TextView) navHeadView.findViewById(R.id.profile_name);
        mTvProfileDesc = (TextView) navHeadView.findViewById(R.id.profile_description);
        Spinner spinnerProfileTeam = (Spinner) navHeadView.findViewById(R.id.profile_team_spinner);

        mUserTeamAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item_team);
        mUserTeamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProfileTeam.setAdapter(mUserTeamAdapter);

        spinnerProfileTeam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TWSettings.sSelectedTeamPos = position;
                getTeamProjects(mUserTeamList.get(position).getTeam_id());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // FIXME: 16/3/24 temporary refresh
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserProfile();
            }
        });

        mTabLayout.setVisibility(View.GONE);
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void loadUserProfile() {
        // FIXME: 16/3/17 add default avatar
        if (mSPUserProfile.getString(getString(R.string.text_sp_user_profile_uid), null) != null) {
            Picasso.with(mContext).load(Uri.parse(mSPUserProfile.getString(getString(R.string.text_sp_user_profile_avatar), null))).into(mProfileImage);
            mTvProfileName.setText(mSPUserProfile.getString(getString(R.string.text_sp_user_profile_display_name), getString(R.string.text_profile_name)));
            mTvProfileDesc.setText(mSPUserProfile.getString(getString(R.string.text_sp_user_profile_desc), getString(R.string.text_profile_description)));
        }
        LogUtils.i(TAG, "loadUserProfile");
    }

    private void updateSpinnerData() {
        mUserTeamAdapter.clear();
        for (UserTeamListModel model : mUserTeamList) {
            mUserTeamAdapter.add(model.getName());
        }
        mUserTeamAdapter.notifyDataSetChanged();

        TWSettings.sTeamList = mUserTeamList;
        TWSettings.sSelectedTeamPos = 0;

        LogUtils.i(TAG, "updateSpinnerData");

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

    private void getUserTeamList() {
        TWApi.UserTeamListService userTeamListService = TWRetrofit.createService(TWApi.UserTeamListService.class, TWAccessToken.getAccessToken());
        Call<List<UserTeamListModel>> userTeamsListCall = userTeamListService.getUserTeams();

        userTeamsListCall.enqueue(new Callback<List<UserTeamListModel>>() {
            @Override
            public void onResponse(Call<List<UserTeamListModel>> call, Response<List<UserTeamListModel>> response) {
                if (response.body() != null) {
                    mUserTeamList = response.body();

                    updateSpinnerData();
                    getTeamProjects(mUserTeamList.get(0).getTeam_id());

                    LogUtils.i(TAG, "getUserTeamList | onResponse ");

                }
            }

            @Override
            public void onFailure(Call<List<UserTeamListModel>> call, Throwable t) {
                // FIXME: 16/3/22 user team list on failure
            }
        });
    }

    private void getTeamProjects(String teamId) {
        initTeamProjectMenu(true);

        TWApi.TeamProjectListService teamProjectListService = TWRetrofit.createService(TWApi.TeamProjectListService.class, TWAccessToken.getAccessToken());
        Call<List<TeamProjectModel>> teamProjectsCall = teamProjectListService.getTeamProjectList(teamId);
        teamProjectsCall.enqueue(new Callback<List<TeamProjectModel>>() {
            @Override
            public void onResponse(Call<List<TeamProjectModel>> call, Response<List<TeamProjectModel>> response) {
                if (response.body() != null) {
                    mTeamProjectList = response.body();

                    TWSettings.sProjectList = mTeamProjectList;
                    initTeamProjectMenu(false);

                    LogUtils.i(TAG, "getTeamProjects | onResponse");

                }
            }

            @Override
            public void onFailure(Call<List<TeamProjectModel>> call, Throwable t) {
                // FIXME: 16/4/6
            }
        });
    }

    private void getProjectEntries(int id) {
        TWApi.ProjectEntryListService projectEntryListService = TWRetrofit.createService(TWApi.ProjectEntryListService.class, TWAccessToken.getAccessToken());
        Call<List<EntryModel>> entryListCall = projectEntryListService.getProjectEntryList(mTeamProjectList.get(id).getPid());
        entryListCall.enqueue(new Callback<List<EntryModel>>() {
            @Override
            public void onResponse(Call<List<EntryModel>> call, Response<List<EntryModel>> response) {
                if (response.body() != null) {
                    mEntryList = response.body();

                    List<String> entryTitles = new ArrayList<>();
                    int entryNum = 0;
                    for (EntryModel entry : response.body()) {
                        mTitlesMap.put(entry.getEntry_id(), entry.getName());
                        entryTitles.add(entry.getName());
                        entryNum += 1;
                    }

                    initProjectEntryFragments(entryNum, entryTitles);
                }

            }

            @Override
            public void onFailure(Call<List<EntryModel>> call, Throwable t) {
                // FIXME: 16/4/6
            }
        });

    }

    private void getTaskList() {
        LogUtils.i(TAG, "getTaskList");

        TWApi.TaskListService taskListService = TWRetrofit.createService(TWApi.TaskListService.class, TWAccessToken.getAccessToken());
        Call<List<TaskModel>> taskListCall = taskListService.getTaskList(mPid);
        taskListCall.enqueue(new Callback<List<TaskModel>>() {
            @Override
            public void onResponse(Call<List<TaskModel>> call, Response<List<TaskModel>> response) {
                if (response.body() != null) {
                    mTaskModelAll = response.body();
                    for (TaskModel task : response.body()) {
                        List<TaskModel> taskList = mTaskListMap.get(task.getEntry_name());
                        if (taskList != null) {
                            taskList.add(task);
                        }
                    }

                    EventBus.getDefault().post(new TaskListEvent(mTaskListMap));
                    LogUtils.i(TAG, "getTaskList | onResponse");
                }
            }

            @Override
            public void onFailure(Call<List<TaskModel>> call, Throwable t) {
                // FIXME: 16/4/6
            }
        });
    }

    private void initTeamProjectMenu(boolean isClear) {
        LogUtils.i(TAG, "initTeamProjectMenu");

        Menu menu = mNavView.getMenu();
        menu.removeGroup(233);
        SubMenu projectMenu = menu.addSubMenu(233,
                Menu.NONE,
                Menu.NONE,
                String.format("%s %s", getString(R.string.text_menu_project), TWSettings.sTeamList.get(TWSettings.sSelectedTeamPos).getName()));

        if (isClear) {
            projectMenu.add(Menu.NONE, Menu.NONE, Menu.NONE, "项目加载中 ...")
                    .setCheckable(true)
                    .setIcon(android.R.color.transparent);
            return;
        }

        int index = 0;
        for (TeamProjectModel model : mTeamProjectList) {
            projectMenu.add(Menu.NONE, index++, Menu.NONE, model.getName())
                    .setCheckable(true)
                    .setIcon(android.R.color.transparent);
        }

        if (index == 0) {
            projectMenu.add(Menu.NONE,
                    Menu.NONE,
                    Menu.NONE,
                    "该团队暂时没有项目")
                    .setCheckable(true)
                    .setIcon(android.R.color.transparent);
        }

    }

    private void initProjectEntryFragments(int entryNum, List<String> titles) {
        mTabLayout.setVisibility(View.VISIBLE);

        mMainTabPagerAdapter.clear();
        mTaskListMap.clear();
        TeamProjectModel project = mTeamProjectList.get(TWSettings.sSelectedProjectPos);
        mPid = project.getPid();

        for (int i = 0; i < entryNum; i++) {
            // FIXME: 16/4/6 entry id
            mMainTabPagerAdapter.addFragment(EntryFragment.newInstance(mPid, titles.get(i)), titles.get(i));
            mTaskListMap.put(titles.get(i), new ArrayList<TaskModel>());
        }

        getTaskList();

        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mMainTabPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

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

        } else {
            TWSettings.sSelectedProjectPos = id;
            getProjectEntries(id);
        }

        // FIXME: 16/3/22
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}