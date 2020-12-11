package com.shoaib.floatingwindow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.MyStatusViewHolder> {

    private List<Status> statusList;

    public StatusAdapter(List<Status> statusList) {
        this.statusList = statusList;
    }

    @NonNull
    @Override
    public MyStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_continer_histroy, parent, false);
        return new MyStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyStatusViewHolder holder, int position) {
        holder.bindView(statusList.get(position));
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public class MyStatusViewHolder extends RecyclerView.ViewHolder {
        private TextView textTitle;
        private TextView textXSeconds;
        private TextView textYSeconds;

        public MyStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textTitle = itemView.findViewById(R.id.textTitle);
            this.textXSeconds = itemView.findViewById(R.id.textXSeconds);
            this.textYSeconds = itemView.findViewById(R.id.textYSeconds);
        }

        public void bindView(Status status){
            textTitle.setText(status.getTitle());
            textXSeconds.setText(status.getXSeconds());
            textYSeconds.setText(status.getYSeconds());
        }
    }
}
