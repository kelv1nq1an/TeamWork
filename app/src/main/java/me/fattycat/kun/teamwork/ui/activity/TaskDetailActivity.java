package me.fattycat.kun.teamwork.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import me.fattycat.kun.teamwork.R;
import me.fattycat.kun.teamwork.model.TaskModel;
import me.fattycat.kun.teamwork.model.TodosEntity;
import me.fattycat.kun.teamwork.ui.adapter.TaskDetailTodosAdapter;

public class TaskDetailActivity extends BaseActivity {
    private static final String TAG = "TW_TaskDetailActivity";
    public static final String EXTRA_TASK_ID = "taskId";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.task_detail_name)
    EditText mTaskDetailName;
    @Bind(R.id.task_detail_desc)
    EditText mTaskDetailDesc;
    @Bind(R.id.task_detail_todos)
    RecyclerView mTaskDetailTodos;
    private Realm mRealm;
    private List<TodosEntity> mTodosEntities;
    private TaskDetailTodosAdapter mDetailTodosAdapter;

    private String mTaskId;
    private TaskModel mTask;
    private boolean mIsEditable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        ButterKnife.bind(this);
        mRealm = Realm.getDefaultInstance();
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initTaskData();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsEditable) {
                    mIsEditable = true;
                    mFab.setImageResource(R.drawable.ic_done_black_24dp);
                    mDetailTodosAdapter.setEditable(mIsEditable);
                } else {
                    mIsEditable = false;
                    mFab.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                    mDetailTodosAdapter.setEditable(mIsEditable);
                }

                mTaskDetailName.setEnabled(mIsEditable);
                mTaskDetailDesc.setEnabled(mIsEditable);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    private void initTaskData() {
        mTaskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        mTask = mRealm.where(TaskModel.class).equalTo("tid", mTaskId).findFirst();
        mTodosEntities = mTask.getTodos();
        mDetailTodosAdapter = new TaskDetailTodosAdapter();

        mTaskDetailName.setText(mTask.getName());
        mTaskDetailDesc.setText(mTask.getDesc());
        mDetailTodosAdapter.setData(mTodosEntities);

        mTaskDetailTodos.setLayoutManager(new LinearLayoutManager(this));
        mTaskDetailTodos.setAdapter(mDetailTodosAdapter);
    }

}
