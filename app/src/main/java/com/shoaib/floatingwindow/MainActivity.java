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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.shoaib.floatingwindow.network.ApiClient;
import com.shoaib.floatingwindow.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "testtag";
    private List<Status> statusList = new ArrayList<>();
    private ConstraintLayout constraintLayout;
    private Button useButton, buttonHome, buttonHistory;
    private RecyclerView recyclerView;
    private StatusAdapter statusAdapter;
    private EditText title, stringUrl, xSeconds, ySeconds;

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


        statusList.add(new Status("ABCD", "www.status.com", "X Seconds = 5", "Y Seconds = 20"));
        statusList.add(new Status("MAT", "www.status.com", "X Seconds = 2", "Y Seconds = 13"));
        statusList.add(new Status("Shoaib", "www.status.com", "X Seconds = 9", "Y Seconds = 5"));


        statusAdapter = new StatusAdapter(statusList);
        recyclerView.setAdapter(statusAdapter);
        statusAdapter.notifyDataSetChanged();

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
}