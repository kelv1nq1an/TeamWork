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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.event.TaskAddEvent;
import me.fattycat.kun.teamwork.model.TodosEntity;

public class TaskDetailTodosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "TW_TaskDetailTodosAdapter";
    private static final int ITEM_TYPE_NORMAL = 0;
    private static final int ITEM_TYPE_ADD = 1;

    private List<TodosEntity> mData = new ArrayList<>();
    private boolean mIsEditable = false;
    private TaskDetailListener mTaskDetailListener;

    public void setData(List<TodosEntity> data) {
        this.mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setTaskDetailListener(TaskDetailListener listener) {
        mTaskDetailListener = listener;
    }

    public void setEditable(boolean editable) {
        this.mIsEditable = editable;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ITEM_TYPE_NORMAL) {
            View view = inflater.inflate(R.layout.item_task_detail_todo, parent, false);
            return new TaskDetailTodoViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_task_detail_todo_add, parent, false);
            return new TaskDetailTodoAddViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TaskDetailTodoViewHolder) {
            if (mData == null || position >= mData.size()) {
                return;
            }
            final TaskDetailTodoViewHolder taskDetailTodoViewHolder = (TaskDetailTodoViewHolder) holder;

            final TodosEntity todosEntity = mData.get(position);
            int endDrawableId;
            if (todosEntity.getChecked() == 1) {
                taskDetailTodoViewHolder.mTodoCheck.setChecked(true);
            } else {
                taskDetailTodoViewHolder.mTodoCheck.setChecked(false);
            }
            if (mIsEditable) {
                endDrawableId = R.drawable.ic_mode_edit_teal_24dp;
            } else {
                endDrawableId = 0;
            }
            taskDetailTodoViewHolder.mTodoCheck.setEnabled(mIsEditable);
            taskDetailTodoViewHolder.mTodoName.setText(todosEntity.getName());
            taskDetailTodoViewHolder.mTodoName.setEnabled(mIsEditable);
            taskDetailTodoViewHolder.mTodoName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (mTaskDetailListener != null) {
                        mTaskDetailListener.onTodoTextChange(todosEntity.getTodo_id(), taskDetailTodoViewHolder.mTodoName.getText().toString());
                    }
                }
            });
        } else if (holder instanceof TaskDetailTodoAddViewHolder) {
            TaskDetailTodoAddViewHolder taskDetailTodoAddViewHolder = (TaskDetailTodoAddViewHolder) holder;
            taskDetailTodoAddViewHolder.mTodoAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new TaskAddEvent());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == mData.size() ? ITEM_TYPE_ADD : ITEM_TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 1 : mData.size() + 1;
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

    public class TaskDetailTodoAddViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_task_detail_todo_add)
        ImageButton mTodoAdd;

        public TaskDetailTodoAddViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface TaskDetailListener {
        void onTodoTextChange(String todoId, String name);
    }
}
