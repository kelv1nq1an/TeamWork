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
package me.fattycat.kun.teamwork.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.model.TaskModel;
import me.fattycat.kun.teamwork.model.TodosEntity;

public class EntryRvAdapter extends RecyclerView.Adapter<EntryRvAdapter.EntryViewHolder> {
    private static final String TAG = "TW_EntryRvAdapter";
    private static final String TODO_DOT = "· ";

    private RealmResults<TaskModel> mRealmResults;

    public void setData(RealmResults<TaskModel> data) {
        mRealmResults = data;
    }

    @Override
    public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entry_list_task, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EntryViewHolder holder, int position) {
        if (mRealmResults.size() < 0) {
            return;
        }

        TaskModel task = mRealmResults.get(position);

        String taskName = task.getName();
        String taskDesc = task.getDesc();
        String taskTodos = "";

        int todoNum = task.getTodos().size();
        int completed = task.getCompleted();
        int todoCheckNum = 0;
        for (TodosEntity entity : task.getTodos()) {
            if (entity.getChecked() == 1)
                todoCheckNum += 1;
            taskTodos += TODO_DOT + entity.getName() + "\n";
        }

       /* if (todoNum == 0) {
            holder.taskProgress.setProgress(1);
            if (completed == 0) {
                holder.taskProgress.setSecondaryProgress(0);
            } else {
                holder.taskProgress.setSecondaryProgress(1);
            }
        } else {
            holder.taskProgress.setProgress(todoNum);
            holder.taskProgress.setSecondaryProgress(todoCheckNum);
        }*/
        if (TextUtils.isEmpty(taskTodos)) {
            holder.taskTodos.setVisibility(View.GONE);
        } else {
            holder.taskTodos.setVisibility(View.VISIBLE);
        }

        holder.taskName.setText(taskName);
        holder.taskTodos.setText(taskTodos);
        holder.taskDesc.setText(taskDesc);
    }

    @Override
    public int getItemCount() {
        return mRealmResults == null ? 0 : mRealmResults.size();
    }

    public class EntryViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.entry_list_item_task_name)
        TextView taskName;
        @Bind(R.id.entry_list_item_task_todos)
        TextView taskTodos;
        @Bind(R.id.entry_list_item_task_desc)
        TextView taskDesc;

        public EntryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
