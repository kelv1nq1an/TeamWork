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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kennyc.view.MultiStateView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.TWAccessToken;
import me.fattycat.kun.teamwork.TWApi;
import me.fattycat.kun.teamwork.TWRetrofit;
import me.fattycat.kun.teamwork.event.TaskListEvent;
import me.fattycat.kun.teamwork.model.TaskModel;
import me.fattycat.kun.teamwork.ui.adapter.EntryRvAdapter;
import me.fattycat.kun.teamwork.util.LogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EntryFragment extends BaseFragment {
    private static final String TAG = "TW_EntryFragment";
    private static final String ARG_PROJECT_ID = "pid";
    private static final String ARG_ENTRY_NAME = "entry_name";

    @Bind(R.id.fragment_entry_list)
    RecyclerView mEntryRecyclerView;
    @Bind(R.id.entry_multi_state_view)
    MultiStateView mMultiStateView;

    private String mPid;
    private String mEntryName;
    private EntryRvAdapter mEntryRvAdapter = new EntryRvAdapter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPid = getArguments().getString(ARG_PROJECT_ID);
            mEntryName = getArguments().getString(ARG_ENTRY_NAME);
        }

        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_entry, container, false);
        ButterKnife.bind(this, root);

        initView();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        //getTaskList();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    private void getTaskList() {
        LogUtils.i(TAG, "getTaskList");

        TWApi.TaskListService taskListService = TWRetrofit.createService(TWApi.TaskListService.class, TWAccessToken.getAccessToken());
        Call<List<TaskModel>> taskListCall = taskListService.getTaskList(mPid);
        taskListCall.enqueue(new Callback<List<TaskModel>>() {
            @Override
            public void onResponse(Call<List<TaskModel>> call, Response<List<TaskModel>> response) {
                if (response.body() != null) {

                    LogUtils.i(TAG, "getTaskList | onResponse");
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
                    mEntryRvAdapter.setData(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<TaskModel>> call, Throwable t) {

            }
        });
    }

    public static EntryFragment newInstance(String pid, String entryName) {
        EntryFragment entryFragment = new EntryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PROJECT_ID, pid);
        args.putString(ARG_ENTRY_NAME, entryName);
        entryFragment.setArguments(args);
        return entryFragment;
    }

    private void initView() {
        mEntryRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mEntryRecyclerView.setAdapter(mEntryRvAdapter);
    }

    @Subscribe
    public void onGetTaskList(TaskListEvent event) {
        LogUtils.i(TAG, "onGetTaskList | event bus receive");
        if (event.taskModelListMap.size() == 0) {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
            return;
        }
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        mEntryRvAdapter.setData(event.taskModelListMap.get(mEntryName));
    }

}
