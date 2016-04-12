/*
 * TeamWork
 * Copyright (C) 2016  FattycatR
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.model.TodosEntity;

public class TaskDetailTodosAdapter extends RecyclerView.Adapter<TaskDetailTodosAdapter.TaskDetailTodoViewHolder> {
    private static final String TAG = "TW_TaskDetailTodosAdapter";
    private LayoutInflater inflater;
    private List<TodosEntity> mData;
    private boolean mIsEditable = false;

    public void setData(List<TodosEntity> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public void setEditable(boolean editable) {
        this.mIsEditable = editable;
        notifyDataSetChanged();
    }

    @Override
    public TaskDetailTodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_task_detail_todo, parent, false);
        return new TaskDetailTodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskDetailTodoViewHolder holder, int position) {
        if (mData == null) {
            return;
        }

        TodosEntity todosEntity = mData.get(position);
        if (todosEntity.getChecked() == 1) {
            holder.mTodoCheck.setChecked(true);
        } else {
            holder.mTodoCheck.setChecked(false);
        }
        holder.mTodoCheck.setEnabled(mIsEditable);
        holder.mTodoName.setEnabled(mIsEditable);
        holder.mTodoName.setText(todosEntity.getName());
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class TaskDetailTodoViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_task_detail_todo_check)
        CheckBox mTodoCheck;
        @Bind(R.id.item_task_detail_todo_name)
        EditText mTodoName;

        public TaskDetailTodoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
