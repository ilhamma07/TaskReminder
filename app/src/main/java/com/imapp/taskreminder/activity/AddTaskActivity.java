package com.imapp.taskreminder.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
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

public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener, DatePickerFragment.DialogDateListener, TimePickerFragment.DialogTimeListener {

    private Button btnAddTask;
    private ImageButton btnAddDate, btnAddTime;
    private TextView txtDateTask, txtTimeTask;
    private EditText edtNameTask, edtDescTask;

    final String DATE_PICKER_TAG = "DatePicker";
    final String TIME_PICKER_TAG = "TimePicker";

    public static final String EXTRA_TASK = "extra_task";
    public static final int REQUEST_ADD = 100;
    public static final int RESULT_ADD = 101;

    private Task task;
    private TaskHelper taskHelper;
    private AlarmReceiver alarmReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        txtDateTask = findViewById(R.id.txt_add_date);
        txtTimeTask = findViewById(R.id.txt_add_time);
        edtNameTask = findViewById(R.id.edt_add_name);
        edtDescTask = findViewById(R.id.edt_add_desc);

        btnAddDate = findViewById(R.id.btn_add_date);
        btnAddDate.setOnClickListener(this);
        btnAddTime = findViewById(R.id.btn_add_time);
        btnAddTime.setOnClickListener(this);

        btnAddTask = findViewById(R.id.btn_add_task);
        btnAddTask.setOnClickListener(this);

        task = new Task();
        alarmReceiver = new AlarmReceiver();

        if (getSupportActionBar() != null){
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
            getSupportActionBar().setTitle(Html.fromHtml("<font color='"+getResources().getColor(R.color.colorPrimary)+"'>"+getResources().getString(R.string.title_add_task)+"</font>"));
        }

        taskHelper = TaskHelper.getInstance(getApplicationContext());
        taskHelper.open();
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_date :
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), DATE_PICKER_TAG);
                break;

            case R.id.btn_add_time :
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getSupportFragmentManager(), TIME_PICKER_TAG);
                break;

            case R.id.btn_add_task :
                String name_task = edtNameTask.getText().toString().trim();
                String date_task = txtDateTask.getText().toString().trim();
                String time_task = txtTimeTask.getText().toString().trim();
                String desc_task = edtDescTask.getText().toString().trim();
                String stat_task = "Undone";

                if (TextUtils.isEmpty(name_task)){
                    edtNameTask.setError(getString(R.string.error_blank_field));
                    return;
                } else if (date_task.equals(getString(R.string.title_task_date))) {
                    txtDateTask.setError(getString(R.string.error_blank_field));
                    return;
                } else if (time_task.equals(getString(R.string.title_task_time))){
                    txtTimeTask.setError(getString(R.string.error_blank_field));
                    return;
                } else if (TextUtils.isEmpty(desc_task)) {
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

                ContentValues val = new ContentValues();
                val.put(NAME_TASK, name_task);
                val.put(DATE_TASK, date_task);
                val.put(TIME_TASK, time_task);
                val.put(DESC_TASK, desc_task);
                val.put(STAT_TASK, stat_task);

                long result = taskHelper.insertTask(val);
                if (result > 0){
                    Toast.makeText(this, "Berhasil menambahkan data tugas baru", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_ADD, intent);
                    alarmReceiver.setAlarm(this, task);
                    finish();
                } else {
                    Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        showAllertDialog();
    }

    @Override
    public void onDialogDateSet(String tag, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        txtDateTask.setText(dateFormat.format(calendar.getTime()));
    }

    private void showAllertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Batal");
        alertDialogBuilder
                .setMessage("Apakah anda ingin membatalan perubahan pada form?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
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
    public void onDialogTimeSet(String tag, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        txtTimeTask.setText(dateFormat.format(calendar.getTime()));
    }
}
