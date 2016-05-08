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
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.event.TaskDeleteEvent;
import me.fattycat.kun.teamwork.event.TodoAddEvent;
import me.fattycat.kun.teamwork.event.TodoDeleteEvent;
import me.fattycat.kun.teamwork.model.TodosEntity;

public class TaskDetailTodosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "TW_TaskDetailTodosAdapter";
    private static final int ITEM_TYPE_TODO_NORMAL = 0;
    private static final int ITEM_TYPE_TODO_ADD = 1;
    private static final int ITEM_TYPE_TASK_DELETE = 2;

    private List<TodosEntity> mData = new ArrayList<>();
    private boolean mIsEditable = false;
    private TaskDetailListener mTaskDetailListener;

    public void setData(List<TodosEntity> data) {
        this.mData.clear();
        for (TodosEntity entity : data) {
            this.mData.add(entity);
        }
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
        if (viewType == ITEM_TYPE_TODO_NORMAL) {
            View view = inflater.inflate(R.layout.item_task_detail_todo, parent, false);
            return new TaskDetailTodoViewHolder(view);
        } else if (viewType == ITEM_TYPE_TODO_ADD) {
            View view = inflater.inflate(R.layout.item_task_detail_todo_add, parent, false);
            return new TaskDetailTodoAddViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_task_detail_delete, parent, false);
            return new TaskDetailTodoDeleteViewHolder(view);
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
            if (todosEntity.getChecked() == 1) {
                taskDetailTodoViewHolder.mTodoCheck.setChecked(true);
            } else {
                taskDetailTodoViewHolder.mTodoCheck.setChecked(false);
            }
            if (mIsEditable) {
                taskDetailTodoViewHolder.mTodoDelete.setVisibility(View.VISIBLE);
            } else {
                taskDetailTodoViewHolder.mTodoDelete.setVisibility(View.GONE);
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
            taskDetailTodoViewHolder.mTodoDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new TodoDeleteEvent(todosEntity.getTodo_id()));
                }
            });
        } else if (holder instanceof TaskDetailTodoAddViewHolder) {
            TaskDetailTodoAddViewHolder taskDetailTodoAddViewHolder = (TaskDetailTodoAddViewHolder) holder;
            taskDetailTodoAddViewHolder.mTodoAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new TodoAddEvent());
                }
            });
        } else {
            TaskDetailTodoDeleteViewHolder taskDetailTodoDeleteViewHolder = (TaskDetailTodoDeleteViewHolder) holder;
            taskDetailTodoDeleteViewHolder.mTodoDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new TaskDeleteEvent());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mData.size() + 1) {
            return ITEM_TYPE_TASK_DELETE;
        } else if (position == mData.size()) {
            return ITEM_TYPE_TODO_ADD;
        } else {
            return ITEM_TYPE_TODO_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 2 : mData.size() + 2;
    }

    public class TaskDetailTodoViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_task_detail_todo_check)
        CheckBox mTodoCheck;
        @Bind(R.id.item_task_detail_todo_name)
        EditText mTodoName;
        @Bind(R.id.item_task_detail_todo_delete)
        ImageView mTodoDelete;

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

    public class TaskDetailTodoDeleteViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_task_detail_todo_delete)
        ImageButton mTodoDelete;

        public TaskDetailTodoDeleteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface TaskDetailListener {
        void onTodoTextChange(String todoId, String name);
    }
}
