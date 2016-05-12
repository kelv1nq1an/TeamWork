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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.event.TaskCompleteEvent;
import me.fattycat.kun.teamwork.event.TaskDetailEvent;
import me.fattycat.kun.teamwork.event.TodoCompleteEvent;
import me.fattycat.kun.teamwork.model.TaskModel;
import me.fattycat.kun.teamwork.model.TodoWrapper;
import me.fattycat.kun.teamwork.model.TodosEntity;
import me.fattycat.kun.teamwork.util.LogUtils;

public class EntryRvAdapter extends RecyclerView.Adapter<EntryRvAdapter.EntryViewHolder>
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "TW_EntryRvAdapter";

    private List<TaskModel> mData = new ArrayList<>();
    private LayoutInflater inflater;

    public void setData(RealmResults<TaskModel> data) {
        mData.clear();
        for (TaskModel model : data) {
            mData.add(model);
        }
        notifyDataSetChanged();
    }

    @Override
    public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_entry_list_task, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EntryViewHolder holder, int position) {
        if (mData.size() < 0) {
            return;
        }

        TaskModel task = mData.get(position);

        String taskName = task.getName();
        String taskDesc = task.getDesc();

        holder.taskTodos.removeAllViews();
        for (TodosEntity todoEntity : task.getTodos()) {
            View mTodoView = inflater.inflate(R.layout.item_todo, holder.taskTodos, false);
            CheckBox todoComplete = (CheckBox) mTodoView.findViewById(R.id.item_todo_check);
            TextView todoName = (TextView) mTodoView.findViewById(R.id.item_todo_name);
            todoComplete.setOnCheckedChangeListener(null);
            if (todoEntity.getChecked() == 1) {
                todoComplete.setChecked(true);
            } else {
                todoComplete.setChecked(false);
            }
            todoName.setText(todoEntity.getName());
            TodoWrapper todoWrapper = new TodoWrapper(todoEntity.getTodo_id(), task.getTid(), task.getPid(), todoEntity.getName());
            mTodoView.setTag(todoWrapper);
            todoComplete.setTag(todoWrapper);

            todoComplete.setOnCheckedChangeListener(this);
            mTodoView.setOnClickListener(this);
            holder.taskTodos.addView(mTodoView);
        }

        holder.taskComplete.setOnCheckedChangeListener(null);
        if (task.getCompleted() == 1) {
            holder.taskComplete.setChecked(true);
        } else {
            holder.taskComplete.setChecked(false);
        }

        if (TextUtils.isEmpty(taskDesc)) {
            holder.taskDesc.setVisibility(View.GONE);
        } else {
            holder.taskDesc.setVisibility(View.VISIBLE);
        }

        holder.taskName.setText(taskName);
        holder.taskDesc.setText(taskDesc);
        holder.taskComplete.setTag(task);
        holder.taskEdit.setTag(task.getTid());
        holder.taskComplete.setOnCheckedChangeListener(this);
        holder.taskEdit.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public void onClick(View v) {
        if (v instanceof Button) {
            String taskId = (String) v.getTag();
            EventBus.getDefault().post(new TaskDetailEvent(taskId));
        } else if (v instanceof LinearLayout) {
            CheckBox todoCheck = (CheckBox) v.findViewById(R.id.item_todo_check);
            boolean isChecked = !todoCheck.isChecked();
            todoCheck.setChecked(isChecked);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getTag() instanceof TaskModel) {
            TaskModel task = (TaskModel) buttonView.getTag();
            if (task != null) {
                EventBus.getDefault().post(new TaskCompleteEvent(task.getTid(), task.getPid(), isChecked));
            }
            LogUtils.i(TAG, ((TaskModel) buttonView.getTag()).getName() + " | " + isChecked);
        } else if (buttonView.getTag() instanceof TodoWrapper) {
            TodoWrapper todoWrapper = (TodoWrapper) buttonView.getTag();
            EventBus.getDefault().post(new TodoCompleteEvent(todoWrapper, isChecked));
        }
    }

    public class EntryViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.entry_list_item_task_name)
        TextView taskName;
        @Bind(R.id.entry_list_item_task_check)
        CheckBox taskComplete;
        @Bind(R.id.entry_list_item_task_todos)
        LinearLayout taskTodos;
        @Bind(R.id.entry_list_item_task_desc)
        TextView taskDesc;
        @Bind(R.id.entry_list_item_task_edit)
        Button taskEdit;

        public EntryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
