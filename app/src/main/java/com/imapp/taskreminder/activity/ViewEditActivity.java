package com.imapp.taskreminder.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.imapp.taskreminder.R;
import com.imapp.taskreminder.alarm.AlarmReceiver;
import com.imapp.taskreminder.fragment.DatePickerFragment;
import com.imapp.taskreminder.fragment.TimePickerFragment;
import com.imapp.taskreminder.helper.TaskHelper;
import com.imapp.taskreminder.model.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.DATE_TASK;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.DESC_TASK;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.NAME_TASK;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.STAT_TASK;
import static com.imapp.taskreminder.db.DatabaseContract.DbColumns.TIME_TASK;

public class ViewEditActivity extends AppCompatActivity implements View.OnClickListener, DatePickerFragment.DialogDateListener, TimePickerFragment.DialogTimeListener {

    private Button btnUpdateTask;
    private ImageButton btnDateTask, btnTimeTask;
    private TextView txtDateTask, txtTimeTask;
    private EditText edtNameTask, edtDescTask;

    private Task task;
    private TaskHelper taskHelper;
    private AlarmReceiver alarmReceiver;

    final String DATE_PICKER_TAG = "DatePicker";
    final String TIME_PICKER_TAG = "TimePicker";
    public static final String EXTRA_TASK = "extra_task";
    public static final String EXTRA_STATS = "extra_stats";
    public static final String EXTRA_POSITION = "extra_position";

    private boolean isDone = false;

    public static final int REQUEST_UPDATE = 200;
    public static final int RESULT_UPDATE = 201;
    public static final int RESULT_DELETE = 301;
    private final int ALLERT_DIALOG_CLOSE = 10;
    private final int ALLERT_DIALOG_DELETE = 20;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit);

        txtDateTask = findViewById(R.id.txt_edit_date);
        txtTimeTask = findViewById(R.id.txt_edit_time);
        edtNameTask = findViewById(R.id.edt_edit_name);
        edtDescTask = findViewById(R.id.edt_edit_desc);

        btnUpdateTask = findViewById(R.id.btn_update_task);
        btnUpdateTask.setOnClickListener(this);

        btnDateTask = findViewById(R.id.btn_edit_date);
        btnDateTask.setOnClickListener(this);
        btnTimeTask = findViewById(R.id.btn_edit_time);
        btnTimeTask.setOnClickListener(this);

        alarmReceiver = new AlarmReceiver();

        taskHelper = TaskHelper.getInstance(getApplicationContext());
        taskHelper.open();

        task = getIntent().getParcelableExtra(EXTRA_TASK);

        if (task != null) {
            position = getIntent().getIntExtra(EXTRA_POSITION, 0);
            String stats = getIntent().getStringExtra(EXTRA_STATS);

            if (stats.equals("Done")){
                isDone = true;
            } else {
                isDone = false;
            }

            edtNameTask.setText(task.getTitleTask());
            txtDateTask.setText(task.getDateTask());
            txtTimeTask.setText(task.getTimeTask());
            edtDescTask.setText(task.getDescTask());
        }

        if (getSupportActionBar() != null){
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
            getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.colorPrimary)+"'>"+getResources().getString(R.string.title_edit_task)+"</font>"));
        }
    }

    @Override
    public void onBackPressed() {
        showAllertDialog(ALLERT_DIALOG_CLOSE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskHelper.open();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        taskHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_edit_task , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_delete_task :
                showAllertDialog(ALLERT_DIALOG_DELETE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_edit_date :
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), DATE_PICKER_TAG);
                break;

            case R.id.btn_edit_time :
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getSupportFragmentManager(), TIME_PICKER_TAG);
                break;

            case R.id.btn_update_task :
                String name_task = edtNameTask.getText().toString().trim();
                String date_task = txtDateTask.getText().toString().trim();
                String time_task = txtTimeTask.getText().toString().trim();
                String desc_task = edtDescTask.getText().toString().trim();
                String stat_task = task.getStatTask();

                if (TextUtils.isEmpty(name_task)){
                    edtNameTask.setError(getString(R.string.error_blank_field));
                    return;
                } else if (date_task.equals(getString(R.string.title_task_date))){
                    txtDateTask.setError(getString(R.string.error_blank_field));
                    return;
                } else if (time_task.equals(getString(R.string.title_task_time))){
                    txtTimeTask.setError(getString(R.string.error_blank_field));
                    return;
                }else if (TextUtils.isEmpty(desc_task)) {
                    edtDescTask.setError(getString(R.string.error_blank_field));
                    return;
                }

                task.setTitleTask(name_task);
                task.setDateTask(date_task);
                task.setTimeTask(time_task);
                task.setDescTask(desc_task);
                task.setStatTask(stat_task);

                Intent intent = new Intent();
                intent.putExtra(EXTRA_TASK, task);
                intent.putExtra(EXTRA_POSITION, position);

                ContentValues val = new ContentValues();
                val.put(NAME_TASK, name_task);
                val.put(DATE_TASK, date_task);
                val.put(TIME_TASK, time_task);
                val.put(DESC_TASK, desc_task);
                val.put(STAT_TASK, stat_task);

                long result = taskHelper.updateTask(task.getIdTask(),val);
                if (result > 0){
                    setResult(RESULT_UPDATE, intent);
                    finish();
                } else {
                    Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showAllertDialog(int type){
        final boolean isDialogeClose = type == ALLERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage;

        if (isDialogeClose){
            dialogTitle = "Batal";
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?";
        } else {
            dialogTitle = "Hapus Task";
            dialogMessage = "Apakah anda yakin ingin menghapus tugas ini?";
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isDialogeClose){
                            finish();
                        } else {
                            long result = taskHelper.deleteTaskById(String.valueOf(task.getIdTask()));
                            if (result > 0){
                                Intent intent = new Intent();
                                intent.putExtra(EXTRA_POSITION, position);
                                setResult(RESULT_DELETE, intent);
                                finish();
                            } else {
                                Toast.makeText(ViewEditActivity.this, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onDialogDateSet(String tag, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        txtDateTask.setText(dateFormat.format(calendar.getTime()));
    }

    @Override
    public void onDialogTimeSet(String tag, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        txtTimeTask.setText(dateFormat.format(calendar.getTime()));
    }
}
