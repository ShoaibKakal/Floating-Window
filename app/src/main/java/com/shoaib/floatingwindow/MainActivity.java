package com.shoaib.floatingwindow;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.shoaib.floatingwindow.network.ApiClient;
import com.shoaib.floatingwindow.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ClickListener{

    private static final String TAG = "testtag";
    private List<Status> statusList = new ArrayList<>();
    private ConstraintLayout constraintLayout;
    private Button useButton, buttonHome, buttonHistory;
    private RecyclerView recyclerView;
    private StatusAdapter statusAdapter;
    private EditText title, stringUrl, xSeconds, ySeconds;
    private DatabaseViewModel databaseViewModel;
    private Status status;
    private HistoryViewModel historyViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        useButton = findViewById(R.id.buttonUse);
        constraintLayout = findViewById(R.id.layoutInputs);
        recyclerView = findViewById(R.id.recyclerViewHistory);
        buttonHome = findViewById(R.id.buttonHome);
        buttonHistory = findViewById(R.id.buttonHistory);
        title = findViewById(R.id.textTitle);
        stringUrl = findViewById(R.id.textUrl);
        xSeconds = findViewById(R.id.textXSeconds);
        ySeconds = findViewById(R.id.textYSeconds);


        status = new Status();


        databaseViewModel = new ViewModelProvider(this).get(DatabaseViewModel.class);
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);






        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (constraintLayout.getVisibility() == View.GONE) {
                    recyclerView.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                }

            }
        });

        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerView.getVisibility() == View.GONE) {
                    constraintLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    getAllStatus();


                }
            }
        });

        getPermission();


        useButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (title.getText().toString().isEmpty() || stringUrl.getText().toString().isEmpty() || xSeconds.getText().toString().isEmpty() || ySeconds.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Some Fields are empty", Toast.LENGTH_LONG).show();
                } else {
                    stringUrl.getText().append("/");
                    ApiService apiService = ApiClient.getRetrofit(stringUrl.getText().toString()).create(ApiService.class);
                    apiService.getStringResponse(stringUrl.getText().toString())
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                    String str = response.body();
                                    if (!Settings.canDrawOverlays(MainActivity.this)) {

                                        getPermission();

                                    } else if (response.isSuccessful()) {
                                        Intent intent = new Intent(MainActivity.this, WidgetService.class);
                                        intent.putExtra("string", str);
                                        intent.putExtra("url", stringUrl.getText().toString());
                                        intent.putExtra("xSeconds", xSeconds.getText().toString());
                                        intent.putExtra("ySeconds", ySeconds.getText().toString());
                                        startService(intent);

                                        addToHistory();

                                    } else if (!response.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Network Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }


            }


        });


    }


    private void getPermission() {
        // check for alert window permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addToHistory() {

        status.setTitle(title.getText().toString());
        status.setUrl(stringUrl.getText().toString());
        status.setXSeconds(xSeconds.getText().toString());
        status.setYSeconds(ySeconds.getText().toString());

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(databaseViewModel.addToHistory(status)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(() ->{
            Toast.makeText(MainActivity.this, "Added to History", Toast.LENGTH_SHORT).show();
            compositeDisposable.dispose();
        }));
    }

    private void getAllStatus(){
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(historyViewModel.getAllStatus().subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(statuses -> {

            if(statusList.size() > 0){
                statusList.clear();
            }
            statusList.addAll(statuses);
            statusAdapter = new StatusAdapter(statusList, this);
            recyclerView.setAdapter(statusAdapter);
            recyclerView.setHasFixedSize(true);
            statusAdapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            compositeDisposable.dispose();
        }));
    }

    @Override
    public void onDeleteClicked(Status status, int position) {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(historyViewModel.removeFromHistory(status)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(() -> {
                statusList.remove(position);
                statusAdapter.notifyItemRemoved(position);
                statusAdapter.notifyItemRangeChanged(position, statusAdapter.getItemCount());
                Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show();
                compositeDisposable.dispose();
            }));

    }

    @Override
    public void onUseClicked(Status status) {


            ApiService apiService = ApiClient.getRetrofit(status.getUrl()).create(ApiService.class);
            apiService.getStringResponse(status.getUrl())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            String str = response.body();
                            if (!Settings.canDrawOverlays(MainActivity.this)) {

                                getPermission();

                            } else if (response.isSuccessful()) {
                                Toast.makeText(MainActivity.this, status.getTitle()+ " started", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, WidgetService.class);
                                intent.putExtra("string", str);
                                intent.putExtra("url", status.getUrl());
                                intent.putExtra("xSeconds", status.getXSeconds());
                                intent.putExtra("ySeconds", status.getYSeconds());
                                startService(intent);

                            } else if (!response.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Network Failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

    }
}