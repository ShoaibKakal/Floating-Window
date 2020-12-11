package com.shoaib.floatingwindow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.MyStatusViewHolder> {

    private List<Status> statusList;
    private ClickListener clickListener;
    public StatusAdapter(List<Status> statusList, ClickListener clickListener) {
        this.statusList = statusList;
        this.clickListener = clickListener;
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
        private TextView textUrl;
        private TextView textXSeconds;
        private TextView textYSeconds;
        private ImageView deleteButton;
        private Button useButton;

        public MyStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textTitle = itemView.findViewById(R.id.textTitle);
            this.textXSeconds = itemView.findViewById(R.id.textXSeconds);
            this.textYSeconds = itemView.findViewById(R.id.textYSeconds);
            this.deleteButton = itemView.findViewById(R.id.imageDeleteHistory);
            this.useButton = itemView.findViewById(R.id.buttonUse);
            this.textUrl = itemView.findViewById(R.id.textUrlString);
        }


        public void bindView(Status status){
            textTitle.setText(status.getTitle());
            textUrl.setText(status.getUrl());
            textXSeconds.setText(String.format("X seconds: %s", status.getXSeconds()));
            textYSeconds.setText(String.format("Y seconds: %s", status.getYSeconds()));

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onDeleteClicked(status, getAdapterPosition());
                }
            });

            useButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onUseClicked(status);
                }
            });
        }
    }
}
