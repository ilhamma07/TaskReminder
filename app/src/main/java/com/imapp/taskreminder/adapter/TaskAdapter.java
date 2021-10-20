package com.imapp.taskreminder.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imapp.taskreminder.R;
import com.imapp.taskreminder.model.Task;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private ArrayList<Task> dataTask = new ArrayList<>();
    private Context context;

    private OnItemClickCallback onItemClickCallback;

    public TaskAdapter(Context context){
        this.context = context;
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback){
        this.onItemClickCallback = onItemClickCallback;
    }

    public void clearData(){
        this.dataTask.clear();
        notifyDataSetChanged();
    }

    public void removeData(int position){
        dataTask.remove(position);
        notifyItemRemoved(position);
    }

    public void addData(Task task){
        this.dataTask.add(task);
        notifyItemInserted(dataTask.size() - 1);
    }

    public void setDataAll(ArrayList<Task> tasks){
        if (dataTask.size() > 0){
            this.dataTask.clear();
        }

        this.dataTask.addAll(tasks);
        notifyDataSetChanged();
    }

    public void setDataByStats(ArrayList<Task> tasks, String stats){
        if (dataTask.size() > 0){
            this.dataTask.clear();
        }
        for (int i = 0; i< tasks.size(); i++){
            if (tasks.get(i).getStatTask().equals(stats)){
                this.dataTask.add(tasks.get(i));
            }
        }
        notifyDataSetChanged();
    }

    public void updateItem(int position, Task task){
        this.dataTask.set(position, task);
        notifyItemChanged(position, task);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TaskViewHolder holder, final int position) {

        holder.bind(dataTask.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickCallback.onItemClicked(dataTask.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
        holder.imgSwitchStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickCallback.onSwitchClicked(dataTask.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataTask.size();
    }


    public class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtDate, txtTime, txtDesc;
        ImageView imgStatus, imgSwitchStatus;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.item_task_title);
            txtDate = itemView.findViewById(R.id.item_task_date);
            txtTime = itemView.findViewById(R.id.item_task_time);
            txtDesc = itemView.findViewById(R.id.item_task_desc);
            imgStatus = itemView.findViewById(R.id.img_status);
            imgSwitchStatus = itemView.findViewById(R.id.item_btn_status);
        }

        public void bind(Task task) {
            txtTitle.setText(task.getTitleTask());
            txtDate.setText(task.getDateTask());
            txtTime.setText(task.getTimeTask());
            txtDesc.setText(task.getDescTask());
            if (task.getStatTask().equals("Undone")){
                imgStatus.setBackground(new ColorDrawable(context.getResources().getColor(R.color.colorStatUndone)));
                imgSwitchStatus.setImageResource(R.drawable.ic_stats_undone);
            } else if (task.getStatTask().equals("Done")){
                imgStatus.setBackground(new ColorDrawable(context.getResources().getColor(R.color.colorStatDone)));
                imgSwitchStatus.setImageResource(R.drawable.ic_stats_done);
            }
        }
    }

    public interface OnItemClickCallback{
        void onItemClicked(Task data, int pos);
        void onSwitchClicked(Task data, int pos);
    }
}
