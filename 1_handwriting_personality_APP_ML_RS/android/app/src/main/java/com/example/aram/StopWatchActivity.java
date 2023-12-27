package com.example.aram;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class StopWatchActivity extends AppCompatActivity {
    private TextView todayquote;
    Handler handler;
    int Seconds, Minutes, MilliSeconds;
    private TextView timeView;
    long MillisecondTime = 0L;  // 스탑워치 시작 버튼을 누르고 흐른 시간
    long StartTime = 0L;        // 스탑워치 시작 버튼 누르고 난 이후 부터의 시간
    long TimeBuff = 0L;         // 스탑워치 일시정지 버튼 눌렀을 때의 총 시간
    long UpdateTime = 0L;       // 스탑워치 일시정지 버튼 눌렀을 때의 총 시간 + 시작 버튼 누르고 난 이후 부터의 시간 = 총 시간

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        todayquote = findViewById(R.id.today_quote);
        Button nextBtn = findViewById(R.id.next);
        Button startBtn = findViewById(R.id.start_btn);
        Button stopSaveBtn = findViewById(R.id.stop_save_btn);
        Button resetBtn = findViewById(R.id.reset_btn);
        timeView = findViewById(R.id.timeView);
        handler = new Handler();

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SystemClock.uptimeMillis()는 디바이스를 부팅한후 부터 쉰 시간을 제외한 밀리초를 반환
                StartTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);

                // 시작 버튼 클릭 시 리셋 버튼을 비활성화 시켜준다.
                resetBtn.setEnabled(false);
            }
        });
        stopSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 스탑워치 일시정지 버튼 눌렀을 때의 총 시간
                TimeBuff += MillisecondTime;

                // Runnable 객체 제거
                handler.removeCallbacks(runnable);

                // 일시정지 버튼 클릭 시 리셋 버튼을 활성화 시켜준다.
                resetBtn.setEnabled(true);

            }

        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 측정 시간을 모두 0으로 리셋시켜준다.
                MillisecondTime = 0L ;
                StartTime = 0L ;
                TimeBuff = 0L ;
                UpdateTime = 0L ;
                Seconds = 0 ;
                Minutes = 0 ;
                MilliSeconds = 0 ;

                // 초를 나타내는 TextView를 0초로 갱신시켜준다.
                timeView.setText("00:00:00");
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String time = timeView.getText().toString();
                String [] times = time.split(":");
                float timer = (float) (Float.parseFloat(times[0])*60.0 + Float.parseFloat(times[1]) + Float.parseFloat(times[2])*0.01) ;

                timeView.setText("");
                Intent intent = new Intent(StopWatchActivity.this, TodayPostActivity.class);
                intent.putExtra("time",timer);
                startActivity(intent);
                finish();
            }
        });
    }
    public Runnable runnable = new Runnable() {

        public void run() {

            // 디바이스를 부팅한 후 부터 현재까지 시간 - 시작 버튼을 누른 시간
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            // 스탑워치 일시정지 버튼 눌렀을 때의 총 시간 + 시작 버튼 누르고 난 이후 부터의 시간 = 총 시간
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            MilliSeconds = (int) (UpdateTime % 100);
            // TextView에 UpdateTime을 갱신해준다
            timeView.setText("" + Minutes + ":"
                    + String.format("%02d", Seconds) + ":"
                    + String.format("%02d", MilliSeconds));
            handler.postDelayed(this, 0);
        }
    };



}