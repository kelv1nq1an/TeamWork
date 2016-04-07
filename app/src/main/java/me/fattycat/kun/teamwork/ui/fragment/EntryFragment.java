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

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.event.TaskListEvent;
import me.fattycat.kun.teamwork.model.TaskModel;
import me.fattycat.kun.teamwork.ui.adapter.EntryRvAdapter;
import me.fattycat.kun.teamwork.util.LogUtils;

public class EntryFragment extends BaseFragment {
    private static final String TAG = "TW_EntryFragment";
    private static final String ARG_ENTRY_ID = "entry_id";

    @Bind(R.id.fragment_entry_list)
    RecyclerView mEntryRecyclerView;
    @Bind(R.id.entry_multi_state_view)
    MultiStateView mMultiStateView;

    private EntryFragment me;
    private String mEntryId;
    private EntryRvAdapter mEntryRvAdapter = new EntryRvAdapter();
    private Realm mRealm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        me = this;
        if (getArguments() != null) {
            mEntryId = getArguments().getString(ARG_ENTRY_ID);
        }
        LogUtils.i(TAG, "onCreate | id = " + mEntryId);

        mRealm = Realm.getDefaultInstance();
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

        if (getArguments() != null) {
            mEntryId = getArguments().getString(ARG_ENTRY_ID);
        }
        LogUtils.i(TAG, "onResume | id = " + mEntryId + " | " + me);

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
        mRealm.close();
        LogUtils.i(TAG, "onDestroy | " + me);
        EventBus.getDefault().unregister(this);
    }

    private void updateData() {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);

        RealmResults<TaskModel> results = mRealm.where(TaskModel.class).equalTo("entry_id", mEntryId).findAll();
        results.sort("pos", Sort.ASCENDING);
        mEntryRvAdapter.setData(results);
    }

    public static EntryFragment newInstance(String entryId) {
        EntryFragment entryFragment = new EntryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ENTRY_ID, entryId);
        entryFragment.setArguments(args);
        LogUtils.i(TAG, "newInstance | id = " + entryId + " | " + entryFragment);
        return entryFragment;
    }

    private void initView() {
        mEntryRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mEntryRecyclerView.setAdapter(mEntryRvAdapter);
    }

    @Subscribe
    public void onGetTaskList(TaskListEvent event) {
        LogUtils.i(TAG, "onGetTaskList | event bus receive | " + me);

        if (event.taskModelListMap != null
                && event.taskModelListMap.size() != 0
                && event.taskModelListMap.get(mEntryId) != null
                && event.taskModelListMap.get(mEntryId).size() != 0) {
            updateData();
            return;
        }
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);

    }

}
