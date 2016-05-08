package me.fattycat.kun.teamwork.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.TWAccessToken;
import me.fattycat.kun.teamwork.TWApi;
import me.fattycat.kun.teamwork.TWRetrofit;
import me.fattycat.kun.teamwork.event.TaskDataChangeEvent;
import me.fattycat.kun.teamwork.event.TaskDeleteEvent;
import me.fattycat.kun.teamwork.event.TodoAddEvent;
import me.fattycat.kun.teamwork.event.TodoDeleteEvent;
import me.fattycat.kun.teamwork.model.CompleteModel;
import me.fattycat.kun.teamwork.model.NewTodoBody;
import me.fattycat.kun.teamwork.model.NewTodoModel;
import me.fattycat.kun.teamwork.model.TaskModel;
import me.fattycat.kun.teamwork.model.TaskNameChangeBody;
import me.fattycat.kun.teamwork.model.TodoChangeModel;
import me.fattycat.kun.teamwork.model.TodoNameChangeBody;
import me.fattycat.kun.teamwork.model.TodosEntity;
import me.fattycat.kun.teamwork.ui.adapter.TaskDetailTodosAdapter;
import me.fattycat.kun.teamwork.util.LogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetailActivity extends BaseActivity {
    private static final String TAG = "TW_TaskDetailActivity";
    public static final String EXTRA_TASK_ID = "taskId";
    public static final String EXTRA_PROJECT_ID = "pId";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.task_detail_name)
    EditText mEtTaskDetailName;
    @Bind(R.id.task_detail_desc)
    EditText mEtTaskDetailDesc;
    @Bind(R.id.task_detail_todos)
    RecyclerView mRvTaskDetailTodos;
    private ProgressDialog mPdCommitChange;
    private ProgressDialog mPdDelete;
    private AlertDialog mAdAddTask;
    private AlertDialog mAdDeleteTask;
    private Realm mRealm;
    private RealmList<TodosEntity> mTodosEntities;
    private TaskDetailTodosAdapter mDetailTodosAdapter;

    private String mTaskId;
    private String mPid;
    private TaskModel mTask;
    private boolean mIsEditable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        ButterKnife.bind(this);
        mRealm = Realm.getDefaultInstance();
        EventBus.getDefault().register(this);
        initView();

        initTaskData();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int endDrawableId = 0;
                if (!mIsEditable) {
                    mIsEditable = true;
                    mFab.setImageResource(R.drawable.ic_done_black_24dp);
                    mDetailTodosAdapter.setEditable(mIsEditable);
                    endDrawableId = R.drawable.ic_mode_edit_white_24dp;
                } else {
                    mIsEditable = false;
                    mFab.setImageResource(R.drawable.ic_mode_edit_white_24dp);
                    mDetailTodosAdapter.setEditable(mIsEditable);

                    saveData();

                }
                refreshTextState();
                mEtTaskDetailName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, endDrawableId, 0);
                mEtTaskDetailDesc.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, endDrawableId, 0);
            }
        });

        TaskDetailTodosAdapter.TaskDetailListener taskDetailListener = new TaskDetailTodosAdapter.TaskDetailListener() {
            @Override
            public void onTodoTextChange(final String todoId, final String name) {
                // TODO: 16/4/13 todoNameChange
                putTodoData(todoId, mPid, name);
            }
        };
        mDetailTodosAdapter.setTaskDetailListener(taskDetailListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        refreshTextState();

        mPdCommitChange = new ProgressDialog(this);
        mPdCommitChange.setMessage("数据保存中 ...");
        mPdDelete = new ProgressDialog(this);
        mPdDelete.setMessage("删除中 ...");
    }

    private void refreshTextState() {
        mEtTaskDetailName.setEnabled(mIsEditable);
        mEtTaskDetailDesc.setEnabled(mIsEditable);
    }

    private void saveData() {
        final String taskName = mEtTaskDetailName.getText().toString();
        final String taskDesc = mEtTaskDetailDesc.getText().toString();
        mPdCommitChange.show();
        LogUtils.i(TAG, "tid= " + mTaskId + " | pid = " + mPid + " | accesstoken " + TWAccessToken.getAccessToken());

        TWApi.TaskNameChangeService taskNameChangeService = TWRetrofit.createServiceWithToken(TWApi.TaskNameChangeService.class, TWAccessToken.getAccessToken());
        Call<CompleteModel> taskNameChangeCall = taskNameChangeService.putTaskNameChange(mTaskId, mPid, new TaskNameChangeBody(taskName, taskDesc));
        taskNameChangeCall.enqueue(new Callback<CompleteModel>() {
            @Override
            public void onResponse(Call<CompleteModel> call, Response<CompleteModel> response) {
                if (response.body() != null) {
                    if (TextUtils.equals(response.body().getSuccess(), "true")) {
                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                mTask.setName(taskName);
                                mTask.setDesc(taskDesc);
                            }
                        });
                        EventBus.getDefault().post(new TaskDataChangeEvent(true));
                        mPdCommitChange.dismiss();
                        Snackbar.make(mFab, "数据保存成功", Snackbar.LENGTH_SHORT).show();
                    } else {
                        onSaveFailure(taskName, taskDesc);
                    }
                } else {
                    onSaveFailure(taskName, taskDesc);
                }
            }

            @Override
            public void onFailure(Call<CompleteModel> call, Throwable t) {
                onSaveFailure(taskName, taskDesc);
            }
        });

    }

    private void addNewTodo(String name) {
        TWApi.AddNewTodoService addNewTodoService = TWRetrofit.createServiceWithToken(TWApi.AddNewTodoService.class, TWAccessToken.getAccessToken());
        Call<NewTodoModel> addNewTodoCall = addNewTodoService.postNewTodo(mTaskId, mPid, new NewTodoBody(name));
        addNewTodoCall.enqueue(new Callback<NewTodoModel>() {
            @Override
            public void onResponse(Call<NewTodoModel> call, Response<NewTodoModel> response) {
                if (response.body() != null) {
                    String todoName = response.body().getName();
                    String todoId = response.body().getTodo_id();
                    final int pos = response.body().getPos();
                    final TodosEntity todosEntity = new TodosEntity();
                    todosEntity.setName(todoName);
                    todosEntity.setTodo_id(todoId);
                    todosEntity.setChecked(0);
                    todosEntity.setPos(pos);

                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mTask.getTodos().add(pos, todosEntity);
                        }
                    });
                    mDetailTodosAdapter.setData(mTodosEntities);
                }
                mPdCommitChange.dismiss();
                EventBus.getDefault().post(new TaskDataChangeEvent(true));
            }

            @Override
            public void onFailure(Call<NewTodoModel> call, Throwable t) {
                mPdCommitChange.dismiss();
                Snackbar.make(mFab, "数据同步失败，请重新同步", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void deleteTodo(final String todoId) {
        mPdDelete.show();
        TWApi.TodoDeleteService todoDeleteService = TWRetrofit.createServiceWithToken(TWApi.TodoDeleteService.class, TWAccessToken.getAccessToken());
        Call<CompleteModel> todoDeleteCall = todoDeleteService.deleteTodo(mTaskId, todoId, mPid);
        todoDeleteCall.enqueue(new Callback<CompleteModel>() {
            @Override
            public void onResponse(Call<CompleteModel> call, Response<CompleteModel> response) {
                if (response.body() != null) {
                    if (TextUtils.equals("true", response.body().getSuccess())) {
                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                TodosEntity todosEntity = mTodosEntities.where().equalTo("todo_id", todoId).findFirst();
                                todosEntity.removeFromRealm();
                            }
                        });
                        mDetailTodosAdapter.setData(mTodosEntities);
                        EventBus.getDefault().post(new TaskDataChangeEvent(true));
                    }
                }
                mPdDelete.dismiss();
            }

            @Override
            public void onFailure(Call<CompleteModel> call, Throwable t) {
                mPdDelete.dismiss();
                Snackbar.make(mFab, "数据同步失败，请重新同步", Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void deleteTask() {
        mPdDelete.show();
        TWApi.TaskDeleteService taskDeleteService = TWRetrofit.createServiceWithToken(TWApi.TaskDeleteService.class, TWAccessToken.getAccessToken());
        Call<CompleteModel> deleteTaskCall = taskDeleteService.deleteTask(mTaskId, mPid);
        deleteTaskCall.enqueue(new Callback<CompleteModel>() {
            @Override
            public void onResponse(Call<CompleteModel> call, Response<CompleteModel> response) {
                if (response.body() != null) {
                    if (TextUtils.equals("true", response.body().getSuccess())) {
                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmResults<TaskModel> results = mRealm.where(TaskModel.class).equalTo("tid", mTaskId).findAll();
                                results.remove(0);
                            }
                        });
                        mPdDelete.dismiss();
                        EventBus.getDefault().post(new TaskDataChangeEvent(true));
                        finish();
                    }
                }
                mPdDelete.dismiss();
            }

            @Override
            public void onFailure(Call<CompleteModel> call, Throwable t) {
                mPdDelete.dismiss();
                Snackbar.make(mFab, "数据同步失败，请重新同步", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void onSaveFailure(final String taskName, final String taskDesc) {
        // FIXME: 16/4/13 worktile api bug
        /*mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mTask.setName(taskName);
                mTask.setDesc(taskDesc);
            }
        });
        EventBus.getDefault().post(new TaskDataChangeEvent(true));
        mPdCommitChange.dismiss();
        Snackbar.make(mFab, "数据保存成功", Snackbar.LENGTH_SHORT).show();*/
        mPdCommitChange.dismiss();
        Snackbar.make(mFab, "数据保存失败，请重试", Snackbar.LENGTH_SHORT)
                .setAction("重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveData();
                    }
                }).show();
    }

    private void putTodoData(final String todoId, String pid, String name) {
        TWApi.TodoChangeService todoChangeService = TWRetrofit.createServiceWithToken(TWApi.TodoChangeService.class, TWAccessToken.getAccessToken());
        Call<TodoChangeModel> todoChangeCall = todoChangeService.putTodoChange(mTaskId, todoId, pid, new TodoNameChangeBody(name));
        todoChangeCall.enqueue(new Callback<TodoChangeModel>() {
            @Override
            public void onResponse(Call<TodoChangeModel> call, final Response<TodoChangeModel> response) {
                if (response.body() != null) {
                    LogUtils.i(TAG, response.body().getName());
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            TodosEntity todosEntity = mTodosEntities.where().equalTo("todo_id", todoId).findFirst();
                            todosEntity.setName(response.body().getName());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<TodoChangeModel> call, Throwable t) {

            }
        });
    }

    private void initTaskData() {
        mTaskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        mPid = getIntent().getStringExtra(EXTRA_PROJECT_ID);
        mTask = mRealm.where(TaskModel.class).equalTo("tid", mTaskId).findFirst();
        mTodosEntities = mTask.getTodos();
        mDetailTodosAdapter = new TaskDetailTodosAdapter();

        mEtTaskDetailName.setText(mTask.getName());
        mEtTaskDetailDesc.setText(mTask.getDesc());
        mDetailTodosAdapter.setData(mTodosEntities);

        mRvTaskDetailTodos.setLayoutManager(new LinearLayoutManager(this));
        mRvTaskDetailTodos.setAdapter(mDetailTodosAdapter);
    }

    @Subscribe
    public void onTaskTodoAdd(TodoAddEvent event) {
        if (mIsEditable) {
            final LinearLayout dialogContainer = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_new_todo, null);

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            mAdAddTask = alertDialogBuilder.setTitle("检查项内容")
                    .setView(dialogContainer)
                    .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText editText = (EditText) dialogContainer.findViewById(R.id.dialog_add_todo_name);
                            String text = editText.getText().toString();
                            if (!TextUtils.isEmpty(text)) {
                                addNewTodo(text);
                                mPdCommitChange.show();
                            } else {
                                Snackbar.make(mFab, "没有输入内容", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create();
            mAdAddTask.show();
        } else {
            Snackbar.make(mFab, "请先点击按钮进入编辑模式", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onTaskDelete(TaskDeleteEvent event) {
        if (mIsEditable) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            mAdDeleteTask = alertBuilder.setTitle("确认删除")
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteTask();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create();
            mAdDeleteTask.show();
        } else {
            Snackbar.make(mFab, "请先点击按钮进入编辑模式", Snackbar.LENGTH_SHORT).show();
        }
    }


    @Subscribe
    public void deleteTodo(TodoDeleteEvent event) {
        final String todoId = event.todoId;
        if (mIsEditable) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            mAdDeleteTask = alertBuilder.setTitle("确认删除")
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteTodo(todoId);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create();
            mAdDeleteTask.show();
        } else {
            Snackbar.make(mFab, "请先点击按钮进入编辑模式", Snackbar.LENGTH_SHORT).show();
        }

    }


}
