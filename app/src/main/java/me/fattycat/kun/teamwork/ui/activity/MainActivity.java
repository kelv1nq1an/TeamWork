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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.TWAccessToken;
import me.fattycat.kun.teamwork.TWApi;
import me.fattycat.kun.teamwork.TWRetrofit;
import me.fattycat.kun.teamwork.TWSettings;
import me.fattycat.kun.teamwork.event.TaskCompleteEvent;
import me.fattycat.kun.teamwork.event.TaskDataChangeEvent;
import me.fattycat.kun.teamwork.event.TaskDetailEvent;
import me.fattycat.kun.teamwork.event.TaskListEvent;
import me.fattycat.kun.teamwork.event.TodoCompleteEvent;
import me.fattycat.kun.teamwork.model.CompleteModel;
import me.fattycat.kun.teamwork.model.EntryModel;
import me.fattycat.kun.teamwork.model.NewTaskBody;
import me.fattycat.kun.teamwork.model.TaskModel;
import me.fattycat.kun.teamwork.model.TeamModel;
import me.fattycat.kun.teamwork.model.TeamProjectModel;
import me.fattycat.kun.teamwork.model.TodoWrapper;
import me.fattycat.kun.teamwork.model.TodosEntity;
import me.fattycat.kun.teamwork.model.UserProfileModel;
import me.fattycat.kun.teamwork.ui.adapter.MainTabPagerAdapter;
import me.fattycat.kun.teamwork.ui.fragment.EntryFragment;
import me.fattycat.kun.teamwork.util.LogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "TW_MainActivity";
    private static final int EVENT_TYPE_TASK = 0;
    private static final int EVENT_TYPE_TODO = 1;
    private static final int UNCOMPLETE = 0;
    private static final int COMPLETE = 1;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.nav_view)
    NavigationView mNavView;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.main_tabs)
    TabLayout mTabLayout;
    @Bind(R.id.container)
    ViewPager mViewPager;
    @Bind(R.id.fab_button_refresh)
    FloatingActionButton mFabButtonRefresh;
    @Bind(R.id.fab_button_add_task)
    FloatingActionButton mFabButtonAddTask;
    @Bind(R.id.fab_menu)
    FloatingActionMenu mFabMenu;

    private CircleImageView mProfileImage;
    private TextView mTvProfileName;
    private TextView mTvProfileDesc;
    private AlertDialog mAdNewTask;
    private ProgressDialog mProgressDialog;

    private Context mContext;
    private List<TeamModel> mUserTeamList = new ArrayList<>();
    private List<TeamProjectModel> mTeamProjectList = new ArrayList<>();
    private Map<String, List<TaskModel>> mTaskListMap = new HashMap<>();
    private ArrayAdapter<String> mUserTeamAdapter;
    private ArrayAdapter<String> mNewTaskEntryAdapter;
    private MainTabPagerAdapter mMainTabPagerAdapter;
    private String mPid;
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        setSupportActionBar(mToolbar);
        mRealm = Realm.getDefaultInstance();
        mMainTabPagerAdapter = new MainTabPagerAdapter(getSupportFragmentManager());
        initView();
        loadUserProfile();
        getUserProfile();

        mUserTeamList = mRealm.where(TeamModel.class).findAll();
        if (mUserTeamList.size() > 0) {
            updateSpinnerData();
            LogUtils.i(TAG, "Realm load user team list success.");
        }
        getUserTeamList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mRealm.close();
    }

    private void initView() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("数据同步中......");
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

        mNewTaskEntryAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item);
        mNewTaskEntryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // FIXME: 16/3/24 temporary refresh
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserProfile();
            }
        });

        mTabLayout.setVisibility(View.GONE);
        mDrawerLayout.openDrawer(GravityCompat.START);
        mFabMenu.setClosedOnTouchOutside(true);
        mFabButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProjectEntries(TWSettings.sSelectedProjectPos);
                mFabMenu.close(true);
            }
        });
        mFabButtonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout dialogContainer = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_new_task, null);
                final Spinner entrySelector = (Spinner) dialogContainer.findViewById(R.id.dialog_new_task_entry_selector);
                entrySelector.setAdapter(mNewTaskEntryAdapter);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                mAdNewTask = builder.setTitle("创建任务")
                        .setView(dialogContainer)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TextInputEditText etName = (TextInputEditText) dialogContainer.findViewById(R.id.dialog_new_task_name);
                                TextInputEditText etDesc = (TextInputEditText) dialogContainer.findViewById(R.id.dialog_new_task_desc);

                                String name = etName.getText().toString();
                                String desc = etDesc.getText().toString();
                                String entryName = mNewTaskEntryAdapter.getItem(entrySelector.getSelectedItemPosition());
                                String entryId = mRealm.where(EntryModel.class).equalTo("name", entryName).findFirst().getEntry_id();
                                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(entryId)) {
                                    mProgressDialog.show();
                                    TWApi.AddNewTaskService newTaskService = TWRetrofit.createServiceWithToken(TWApi.AddNewTaskService.class, TWAccessToken.getAccessToken());
                                    Call<TaskModel> newTaskCall = newTaskService.postNewTask(mPid, new NewTaskBody(name, entryId, desc));
                                    newTaskCall.enqueue(new Callback<TaskModel>() {
                                        @Override
                                        public void onResponse(Call<TaskModel> call, Response<TaskModel> response) {
                                            if (response.body() == null) {
                                                mProgressDialog.dismiss();
                                                Snackbar.make(mFabMenu, "任务创建失败", Snackbar.LENGTH_LONG).show();
                                                return;
                                            }

                                            mProgressDialog.dismiss();
                                            getProjectEntries(TWSettings.sSelectedProjectPos);
                                            Snackbar.make(mFabMenu, "任务创建成功", Snackbar.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onFailure(Call<TaskModel> call, Throwable t) {
                                            mProgressDialog.dismiss();
                                            Snackbar.make(mFabMenu, "任务创建失败", Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        })
                        .create();
                mAdNewTask.show();
            }
        });
    }

    private void loadUserProfile() {
        RealmResults<UserProfileModel> userProfileResult = mRealm.where(UserProfileModel.class).findAll();
        if (userProfileResult != null && userProfileResult.size() > 0) {
            Picasso.with(mContext).load(Uri.parse(userProfileResult.last().getAvatar())).into(mProfileImage);
            mTvProfileName.setText(userProfileResult.last().getDisplay_name());
            mTvProfileDesc.setText(userProfileResult.last().getDesc());
        }
        LogUtils.i(TAG, "loadUserProfile");
    }

    private void updateSpinnerData() {
        mUserTeamAdapter.clear();
        for (TeamModel model : mUserTeamList) {
            mUserTeamAdapter.add(model.getName());
        }
        mUserTeamAdapter.notifyDataSetChanged();

        TWSettings.sTeamList = mUserTeamList;
        TWSettings.sSelectedTeamPos = 0;

        LogUtils.i(TAG, "updateSpinnerData");

    }

    private void getUserProfile() {
        TWApi.UserProfileService userProfileService = TWRetrofit.createServiceWithToken(TWApi.UserProfileService.class, TWAccessToken.getAccessToken());
        Call<UserProfileModel> userProfileModelCall = userProfileService.getUserProfile();

        userProfileModelCall.enqueue(new Callback<UserProfileModel>() {
            @Override
            public void onResponse(Call<UserProfileModel> call, Response<UserProfileModel> response) {
                if (response.body() != null) {

                    final UserProfileModel userProfile = response.body();
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mRealm.clear(UserProfileModel.class);
                            mRealm.copyToRealmOrUpdate(userProfile);
                        }
                    });

                    loadUserProfile();

                    LogUtils.i(TAG, "getUserProfile | onResponse");

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
        TWApi.UserTeamListService userTeamListService = TWRetrofit.createServiceWithToken(TWApi.UserTeamListService.class, TWAccessToken.getAccessToken());
        Call<List<TeamModel>> userTeamsListCall = userTeamListService.getUserTeams();

        userTeamsListCall.enqueue(new Callback<List<TeamModel>>() {
            @Override
            public void onResponse(Call<List<TeamModel>> call, Response<List<TeamModel>> response) {
                if (response.body() != null) {

                    final List<TeamModel> list = response.body();
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mRealm.clear(TeamModel.class);
                            mRealm.copyToRealmOrUpdate(list);
                        }
                    });

                    mUserTeamList = response.body();

                    updateSpinnerData();
                    getTeamProjects(mUserTeamList.get(0).getTeam_id());
                    Snackbar.make(mFabMenu, R.string.text_team_refresh, Snackbar.LENGTH_SHORT).show();

                    LogUtils.i(TAG, "getUserTeamList | onResponse ");

                }
            }

            @Override
            public void onFailure(Call<List<TeamModel>> call, Throwable t) {
                // FIXME: 16/3/22 user team list on failure
            }
        });
    }

    private void getTeamProjects(String teamId) {
        initTeamProjectMenu(true);

        TWApi.TeamProjectListService teamProjectListService = TWRetrofit.createServiceWithToken(TWApi.TeamProjectListService.class, TWAccessToken.getAccessToken());
        Call<List<TeamProjectModel>> teamProjectsCall = teamProjectListService.getTeamProjectList(teamId);
        teamProjectsCall.enqueue(new Callback<List<TeamProjectModel>>() {
            @Override
            public void onResponse(Call<List<TeamProjectModel>> call, Response<List<TeamProjectModel>> response) {
                if (response.body() != null) {
                    mTeamProjectList = response.body();

                    final List<TeamProjectModel> teamProject = response.body();
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mRealm.clear(TeamProjectModel.class);
                            mRealm.copyToRealmOrUpdate(teamProject);
                        }
                    });

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
        TWApi.ProjectEntryListService projectEntryListService = TWRetrofit.createServiceWithToken(TWApi.ProjectEntryListService.class, TWAccessToken.getAccessToken());
        Call<List<EntryModel>> entryListCall = projectEntryListService.getProjectEntryList(mTeamProjectList.get(id).getPid());
        entryListCall.enqueue(new Callback<List<EntryModel>>() {
            @Override
            public void onResponse(Call<List<EntryModel>> call, Response<List<EntryModel>> response) {
                if (response.body() != null) {

                    final List<EntryModel> entryModel = response.body();
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // FIXME: 16/4/7 group
                            mRealm.clear(EntryModel.class);
                            mRealm.copyToRealm(entryModel);
                        }
                    });

                    initProjectEntryFragments();
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

        TWApi.TaskListService taskListService = TWRetrofit.createServiceWithToken(TWApi.TaskListService.class, TWAccessToken.getAccessToken());
        Call<List<TaskModel>> taskListCall = taskListService.getTaskList(mPid);
        taskListCall.enqueue(new Callback<List<TaskModel>>() {
            @Override
            public void onResponse(Call<List<TaskModel>> call, Response<List<TaskModel>> response) {
                if (response.body() != null) {

                    final List<TaskModel> taskModels = response.body();
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mRealm.clear(TaskModel.class);
                            mRealm.copyToRealmOrUpdate(taskModels);
                        }
                    });

                    for (TaskModel task : response.body()) {
                        List<TaskModel> taskList = mTaskListMap.get(task.getEntry_id());
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
            projectMenu.add(Menu.NONE, Menu.NONE, Menu.NONE, R.string.text_project_refreshing)
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
                    R.string.text_team_no_project)
                    .setCheckable(true)
                    .setIcon(android.R.color.transparent);
        }

    }

    private void initProjectEntryFragments() {
        mTabLayout.setVisibility(View.VISIBLE);

        mMainTabPagerAdapter.clear();
        mTaskListMap.clear();
        mNewTaskEntryAdapter.clear();
        TeamProjectModel project = mTeamProjectList.get(TWSettings.sSelectedProjectPos);
        mPid = project.getPid();

        List<EntryModel> entryModelRealmResults = mRealm.where(EntryModel.class).findAll();
        for (EntryModel entryModel : entryModelRealmResults) {
            String entryId = entryModel.getEntry_id();
            String entryName = entryModel.getName();
            EntryFragment fragment = EntryFragment.newInstance(entryId);
            mMainTabPagerAdapter.addFragment(fragment, entryName);
            mTaskListMap.put(entryId, new ArrayList<TaskModel>());

            mNewTaskEntryAdapter.add(entryName);
        }

        mViewPager.setOffscreenPageLimit(entryModelRealmResults.size());
        mViewPager.setAdapter(mMainTabPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        getTaskList();


    }

    private void showCompleteChangeSnackBar(String msg, final int eventType, final String tid, final String pid, final TodoWrapper todoWrapper, final boolean isComplete) {
        Snackbar.make(mFabMenu, msg, Snackbar.LENGTH_LONG)
                .setAction("撤销", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (eventType) {
                            case EVENT_TYPE_TASK:
                                onTaskComplete(new TaskCompleteEvent(tid, pid, !isComplete));
                                break;
                            case EVENT_TYPE_TODO:
                                onTodoComplete(new TodoCompleteEvent(todoWrapper, !isComplete));
                                break;
                        }
                    }
                })
                .show();
    }

    @Subscribe
    public void onTaskComplete(TaskCompleteEvent event) {
        final String tid = event.tid;
        final String pid = event.pid;
        final boolean isComplete = event.isComplete;

        if (isComplete) {
            TWApi.TaskCompleteService taskCompleteService = TWRetrofit.createServiceWithToken(TWApi.TaskCompleteService.class, TWAccessToken.getAccessToken());
            Call<CompleteModel> taskCompleteCall = taskCompleteService.putTaskComplete(tid, tid, pid);
            taskCompleteCall.enqueue(new Callback<CompleteModel>() {
                @Override
                public void onResponse(Call<CompleteModel> call, Response<CompleteModel> response) {
                    if (response.body() != null) {
                        if (TextUtils.equals("true", response.body().getSuccess())) {
                            showCompleteChangeSnackBar("任务标记完成", EVENT_TYPE_TASK, tid, pid, null, true);
                            taskCompleteInfoToRealm(tid, COMPLETE);
                            EventBus.getDefault().post(new TaskDataChangeEvent(true));
                        }
                    }
                }

                @Override
                public void onFailure(Call<CompleteModel> call, Throwable t) {
                    EventBus.getDefault().post(new TaskDataChangeEvent(false));
                }
            });
        } else {
            TWApi.TaskUnCompleteService taskUnCompleteService = TWRetrofit.createServiceWithToken(TWApi.TaskUnCompleteService.class, TWAccessToken.getAccessToken());
            Call<CompleteModel> taskUnCompleteCall = taskUnCompleteService.putTaskUnComplete(tid, tid, pid);
            taskUnCompleteCall.enqueue(new Callback<CompleteModel>() {
                @Override
                public void onResponse(Call<CompleteModel> call, Response<CompleteModel> response) {
                    if (response.body() != null) {
                        if (TextUtils.equals("true", response.body().getSuccess())) {
                            showCompleteChangeSnackBar("任务标记未完成", EVENT_TYPE_TASK, tid, pid, null, false);
                            taskCompleteInfoToRealm(tid, UNCOMPLETE);
                            EventBus.getDefault().post(new TaskDataChangeEvent(true));
                        }
                    }
                }

                @Override
                public void onFailure(Call<CompleteModel> call, Throwable t) {
                    EventBus.getDefault().post(new TaskDataChangeEvent(false));
                }
            });
        }
    }

    @Subscribe
    public void onTodoComplete(final TodoCompleteEvent event) {
        final String todoId = event.todoWrapper.todoId;
        final String taskId = event.todoWrapper.taskId;
        final String projectId = event.todoWrapper.projectId;
        if (event.isChecked) {
            TWApi.TodoCompleteService todoCompleteService = TWRetrofit.createServiceWithToken(TWApi.TodoCompleteService.class, TWAccessToken.getAccessToken());
            Call<CompleteModel> todoCompleteCall = todoCompleteService.putTodoComplete(taskId, todoId, projectId);

            todoCompleteCall.enqueue(new Callback<CompleteModel>() {
                @Override
                public void onResponse(Call<CompleteModel> call, Response<CompleteModel> response) {
                    if (response.body() != null) {
                        if (TextUtils.equals("true", response.body().getSuccess())) {
                            showCompleteChangeSnackBar("检查项标记完成", EVENT_TYPE_TODO, null, null, event.todoWrapper, true);
                            todoCompleteInfoToRealm(taskId, projectId, todoId, COMPLETE);
                            EventBus.getDefault().post(new TaskDataChangeEvent(true));
                        }
                    }
                }

                @Override
                public void onFailure(Call<CompleteModel> call, Throwable t) {
                    EventBus.getDefault().post(new TaskDataChangeEvent(false));
                }
            });
        } else {
            TWApi.TodoUnCompleteService todoUnCompleteService = TWRetrofit.createServiceWithToken(TWApi.TodoUnCompleteService.class, TWAccessToken.getAccessToken());
            Call<CompleteModel> todoUnCompleteCall = todoUnCompleteService.putTodoUnComplete(taskId, todoId, projectId);
            todoUnCompleteCall.enqueue(new Callback<CompleteModel>() {
                @Override
                public void onResponse(Call<CompleteModel> call, Response<CompleteModel> response) {
                    if (response.body() != null) {
                        if (TextUtils.equals("true", response.body().getSuccess())) {
                            showCompleteChangeSnackBar("检查项标记未完成", EVENT_TYPE_TODO, null, null, event.todoWrapper, false);
                            todoCompleteInfoToRealm(taskId, projectId, todoId, UNCOMPLETE);
                            EventBus.getDefault().post(new TaskDataChangeEvent(true));
                        }
                    }
                }

                @Override
                public void onFailure(Call<CompleteModel> call, Throwable t) {
                    EventBus.getDefault().post(new TaskDataChangeEvent(false));
                }
            });
        }
    }

    @Subscribe
    public void editTask(TaskDetailEvent event) {
        Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, event.taskId);
        intent.putExtra(TaskDetailActivity.EXTRA_PROJECT_ID, mPid);
        startActivity(intent);
    }

    private void taskCompleteInfoToRealm(final String taskId, final int isComplete) {
        TaskModel taskModel = mRealm.where(TaskModel.class)
                .equalTo("tid", taskId)
                .findFirst();
        LogUtils.i(TAG, "taskmodel | " + taskModel.getName() + " | " + taskModel.getCompleted() + " | isComplete = " + isComplete);

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                TaskModel task = mRealm.where(TaskModel.class)
                        .equalTo("tid", taskId)
                        .findFirst();
                task.setCompleted(isComplete);
            }
        });

        TaskModel taskModel2 = mRealm.where(TaskModel.class)
                .equalTo("tid", taskId)
                .findFirst();
        LogUtils.i(TAG, "taskmodel | " + taskModel2.getName() + " | " + taskModel2.getCompleted());
    }

    private void todoCompleteInfoToRealm(final String taskId, final String projectId, final String todoId, final int isComplete) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                TaskModel task = mRealm.where(TaskModel.class)
                        .equalTo("tid", taskId)
                        .equalTo("pid", projectId)
                        .findFirst();
                int index = 0;
                for (TodosEntity todosEntity : task.getTodos()) {
                    if (TextUtils.equals(todosEntity.getTodo_id(), todoId)) {
                        index = task.getTodos().indexOf(todosEntity);
                        break;
                    }
                }
                task.getTodos().get(index).setChecked(isComplete);
            }
        });
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

        if (id == R.id.nav_calendar) {

        } else {
            TWSettings.sSelectedProjectPos = id;
            getProjectEntries(id);
        }

        // FIXME: 16/3/22
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}