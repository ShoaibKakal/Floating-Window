package com.shoaib.floatingwindow;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shoaib.floatingwindow.network.ApiClient;
import com.shoaib.floatingwindow.network.ApiService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WidgetService extends Service {

    int LAYOUT_FLAG;
    View floatingView;
    WindowManager windowManager;
    ImageView imageClose;
    TextView textBubble;
    float height, width;
    int hashCounts = 0;

    ArrayList<String> arrayList = new ArrayList<>();
    int value = 0;
    Handler handler = new Handler();
    String responseString;
    Handler handlerYSeconds = new Handler();
    String xSeconds, ySeconds, url;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        responseString = intent.getStringExtra("string");
        xSeconds = intent.getStringExtra("xSeconds");
        ySeconds = intent.getStringExtra("ySeconds");
        url = intent.getStringExtra("url");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        //inflate widget layout
        floatingView = LayoutInflater.from(this).inflate(R.layout.layout_widget, null);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //initial Position
        layoutParams.gravity = Gravity.TOP | Gravity.CENTER;
        layoutParams.x = 0;
        layoutParams.y = 210;

        //layout params for clone button


        WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams(100, 100, LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        imageParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        imageParams.y = 100;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        imageClose = new ImageView(this);
        imageClose.setImageResource(R.drawable.ic_close);
        imageClose.setBackgroundColor(R.color.black);

        imageClose.setVisibility(View.INVISIBLE);
        windowManager.addView(imageClose, imageParams);
        windowManager.addView(floatingView, layoutParams);
        floatingView.setVisibility(View.VISIBLE);

        height = windowManager.getDefaultDisplay().getHeight();
        width = windowManager.getDefaultDisplay().getWidth();

        textBubble = floatingView.findViewById(R.id.textViewBubble);

        //drag movement for widget


        textBubble.setOnTouchListener(new View.OnTouchListener() {
            int initialX, initialY;
            float initialTouchX, initialTouchY;
            long startClickTime;
            int MAX_CLICK_DURATION = 200;

            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        imageClose.setVisibility(View.VISIBLE);

                        initialX = layoutParams.x;
                        initialY = layoutParams.y;

                        //touch position
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return true;


                    case MotionEvent.ACTION_UP:
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        imageClose.setVisibility(View.GONE);

                        layoutParams.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                        layoutParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);

                        if (clickDuration < MAX_CLICK_DURATION) {
                            Toast.makeText(WidgetService.this, "Time: " + textBubble.getText().toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            if (layoutParams.y > (height * 0.8)) {
                                stopSelf();
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //calculate X and Y coordinates of view
                        layoutParams.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                        layoutParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);

                        //update layout with new coordinates
                        windowManager.updateViewLayout(floatingView, layoutParams);
                        if (layoutParams.y > (height * 0.8)) {
                            imageClose.setImageResource(R.drawable.ic_close);
                        } else {
                            imageClose.setImageResource(R.drawable.ic_close);
                        }
                        return true;
                }
                return false;
            }
        });
        showTextOnBubble();

        return START_STICKY;
    }


    private void showTextOnBubble() {
        String[] spiltStr = responseString.split("#");
        if (arrayList.size() != 0) {
            arrayList.clear();
        }
        arrayList.addAll(Arrays.asList(spiltStr));
//        textBubble.setText(responseString);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (value >= arrayList.size()) {
                    value = 0;
                }
                textBubble.setText(arrayList.get(value));
                value += 1;
                handler.postDelayed(this, Integer.parseInt(xSeconds) * 1000);
            }
        }, 0);

        handlerYSeconds.postDelayed(new Runnable() {
            @Override
            public void run() {
                arrayList.clear();
                handler.removeCallbacksAndMessages(null);
                //  showTextOnBubble(responseString, xSeconds, ySeconds);
//                Toast.makeText(WidgetService.this, "Again called", Toast.LENGTH_SHORT).show();
                networkCall();
                handlerYSeconds.removeCallbacksAndMessages(null);
            }
        }, Integer.parseInt(ySeconds) * 1000);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        handlerYSeconds.removeCallbacksAndMessages(null);
        if (floatingView != null) {
            windowManager.removeView(floatingView);
        }

        if (imageClose != null) {
            windowManager.removeView(imageClose);
        }
    }

    private void networkCall() {
        ApiService apiService = ApiClient.getRetrofit(url).create(ApiService.class);
        apiService.getStringResponse(url)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        String str = response.body();
                        if (response.isSuccessful()) {
                            responseString = str;
                            showTextOnBubble();
                        } else {
                            Toast.makeText(WidgetService.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Toast.makeText(WidgetService.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}