package com.imapp.taskreminder.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.imapp.taskreminder.R;
import com.imapp.taskreminder.adapter.TaskAdapter;
import com.imapp.taskreminder.helper.MappingHelper;
import com.imapp.taskreminder.helper.TaskHelper;
import com.imapp.taskreminder.model.Task;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.DATE_TASK;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.DESC_TASK;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.NAME_TASK;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.STAT_TASK;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.TIME_TASK;

public class AllTaskActivity extends AppCompatActivity implements LoadAllTaskCallback{

    private TextView emptyText;
    private ProgressBar progressBar;
    private FloatingActionButton fab;

    private RecyclerView rvAllTask;
    private TaskAdapter taskAdapter;
    private TaskHelper taskHelper;

    private ArrayList<Task> tempTasks;
    private int menuPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_task);

        if (getSupportActionBar() != null){
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
            getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.colorPrimary)+"'>"+getResources().getString(R.string.app_name)+"</font>"));
        }

        menuPos = 0;

        emptyText = findViewById(R.id.text_empty_view);
        progressBar = findViewById(R.id.progress_bar_all_task);

        taskAdapter = new TaskAdapter(this);
        taskAdapter.notifyDataSetChanged();

        tempTasks = new ArrayList<>();

        rvAllTask = findViewById(R.id.rv_all_task);
        rvAllTask.setLayoutManager(new LinearLayoutManager(this));
        rvAllTask.setHasFixedSize(true);
        rvAllTask.setAdapter(taskAdapter);

        taskHelper = TaskHelper.getInstance(this);
        taskHelper.open();

        taskAdapter.setOnItemClickCallback(new TaskAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(Task data, int pos) {
                Intent intent = new Intent(AllTaskActivity.this, ViewEditActivity.class);

                intent.putExtra(ViewEditActivity.EXTRA_POSITION, pos);
                intent.putExtra(ViewEditActivity.EXTRA_TASK, data);
                intent.putExtra(ViewEditActivity.EXTRA_STATS, data.getStatTask());
                startActivityForResult(intent, ViewEditActivity.REQUEST_UPDATE);
            }

            @Override
            public void onSwitchClicked(Task data, int pos) {
                if (data.getStatTask().equals("Done")){
                    setStat(data,"Undone");
                    if (menuPos > 0){
                        taskAdapter.removeData(pos);
                    } else {
                        taskAdapter.notifyItemChanged(pos);
                    }
                } else if (data.getStatTask().equals("Undone")){
                    setStat(data,"Done");
                    if (menuPos > 0){
                        taskAdapter.removeData(pos);
                    } else {
                        taskAdapter.notifyItemChanged(pos);
                    }
                }
            }
        });

        fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iNewTask = new Intent(AllTaskActivity.this, AddTaskActivity.class);
                startActivityForResult(iNewTask, AddTaskActivity.REQUEST_ADD);
            }
        });

        new LoadTaskAsync(taskHelper, this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_all_task, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (searchManager != null){
            SearchView searchView = (SearchView)(menu.findItem(R.id.menu_search)).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryHint(getResources().getString(R.string.search_hint));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    taskAdapter.clearData();
                    for (int i = 0; i < tempTasks.size(); i++){
                        if (tempTasks.get(i).getTitleTask().contains(query)){
                            taskAdapter.addData(tempTasks.get(i));
                        } else {
                            Toast.makeText(AllTaskActivity.this, "Tugas "+query+" tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_all :
                taskHelper.open();
                menuPos = 0;
                new LoadTaskAsync(taskHelper, this).execute();
                break;
            case R.id.menu_done :
                menuPos = 1;
                taskAdapter.setDataByStats(tempTasks, "Done");
                break;
            case R.id.menu_undone :
                menuPos = 2;
                taskAdapter.setDataByStats(tempTasks, "Undone");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        menuPos = 0;
        taskHelper.open();
        new LoadTaskAsync(taskHelper, this).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            if (requestCode == AddTaskActivity.REQUEST_ADD){
                if (resultCode == AddTaskActivity.RESULT_ADD){
                    Task task = data.getParcelableExtra(AddTaskActivity.EXTRA_TASK);

                    taskAdapter.addData(task);
                    rvAllTask.smoothScrollToPosition(taskAdapter.getItemCount() - 1);

                    Snackbar.make(rvAllTask, "Satu item berhasil ditambahkan", Snackbar.LENGTH_SHORT).show();
                }
            } else if (requestCode == ViewEditActivity.REQUEST_UPDATE) {
                if (resultCode == ViewEditActivity.RESULT_UPDATE){
                    Task task = data.getParcelableExtra(ViewEditActivity.EXTRA_TASK);
                    int position = data.getIntExtra(ViewEditActivity.EXTRA_POSITION, 0);

                    taskAdapter.updateItem(position, task);
                    rvAllTask.smoothScrollToPosition(position);

                    Snackbar.make(rvAllTask, "Satu item berhasil diubah", Snackbar.LENGTH_SHORT).show();
                } else if (resultCode == ViewEditActivity.RESULT_DELETE){
                    int position = data.getIntExtra(ViewEditActivity.EXTRA_POSITION, 0);

                    taskAdapter.removeData(position);

                    Snackbar.make(rvAllTask, "Satu item berhasil dihapus", Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setStat(Task task, String stats){
        taskHelper.open();
        String name_task = task.getTitleTask();
        String date_task = task.getDateTask();
        String time_task = task.getTimeTask();
        String desc_task = task.getDescTask();
        String stat_task = stats;

        task.setTitleTask(name_task);
        task.setDateTask(date_task);
        task.setTimeTask(time_task);
        task.setDescTask(desc_task);
        task.setStatTask(stat_task);

        ContentValues val = new ContentValues();
        val.put(NAME_TASK, name_task);
        val.put(DATE_TASK, date_task);
        val.put(TIME_TASK, time_task);
        val.put(DESC_TASK, desc_task);
        val.put(STAT_TASK, stat_task);

        long result = taskHelper.updateTask(task.getIdTask(),val);
        if (result > 0){
            Toast.makeText(this, "Status tugas "+task.getTitleTask()+" diubah", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void preExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void postExecute(ArrayList<Task> tasks) {
        if (tasks.size() > 0){
            taskAdapter.clearData();
            taskAdapter.setDataAll(tasks);

            tempTasks.clear();
            tempTasks.addAll(tasks);

            emptyText.setVisibility(View.GONE);
        } else {
            taskAdapter.setDataAll(new ArrayList<Task>());
            emptyText.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }

    private static class LoadTaskAsync extends AsyncTask<Void, Void, ArrayList<Task>> {
        private final WeakReference<TaskHelper> weakTaskHelper;
        private final WeakReference<LoadAllTaskCallback> weakCallback;

        private LoadTaskAsync(TaskHelper taskHelper, LoadAllTaskCallback callback) {
            weakTaskHelper = new WeakReference<>(taskHelper);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected ArrayList<Task> doInBackground(Void... voids) {
            Cursor dataCursor = weakTaskHelper.get().queryTaskAll();
            return MappingHelper.mapCursorToArrayList(dataCursor);
        }

        @Override
        protected void onPostExecute(ArrayList<Task> tasks) {
            super.onPostExecute(tasks);
            weakCallback.get().postExecute(tasks);
        }
    }
}

interface LoadAllTaskCallback {
    void preExecute();
    void postExecute(ArrayList<Task> tasks);
}