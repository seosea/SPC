package com.example.spc.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.spc.Fragment.HomeFragment;
import com.example.spc.Fragment.TimerFragment;
import com.example.spc.Fragment.VideoFragment;
import com.example.spc.Helper.BackPressCloseHandler;
import com.example.spc.Helper.Constant;
import com.example.spc.R;
import com.kyleduo.switchbutton.SwitchButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

import static com.example.spc.Fragment.TimerFragment.btnPause;
import static com.example.spc.Fragment.TimerFragment.btnPlay;
import static com.example.spc.Fragment.TimerFragment.btnStop;
import static com.example.spc.Fragment.TimerFragment.editHour;
import static com.example.spc.Fragment.TimerFragment.editMin;
import static com.example.spc.Fragment.TimerFragment.editSec;

/**
 * Main Activity 안에 Fragment 들을 띄우는 구조로 되어 있습니다.
 * Fragment는 각각 Home, Timer, Video 3개 이름으로 되어있습니다.
 * 이 구조는 Main에서 상/하단 버튼 및 상표를 고정하고 Main의 일정 화면만 Fragment들의 각 화면을 띄워 전환하고 있습니다!
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static String RESENT_BUTTON = "home"; //현재 누른 버튼

    //Fragment 구조 (Activity위에 Fragment를 띄움) 를 위한 변수
    FragmentManager fragmentManager;
    Fragment fragment;

    private ImageButton btnHome, btnVideo, btnTimer; // 하단 3개 버튼
    private SwitchButton btnAlarm; // 상단 진동 버튼
    private ImageButton btnInfo; // 상단 'i'버튼

    private Dialog dialog = null; // info 띄우기 위한 다이얼로그

    public static CountDownTimer _timer = null; // 타이머 함수

    public static Vibrator vibrator; // 진동 변수

    private BackPressCloseHandler backPressCloseHandler; // 뒤로 가기 2번 누르면 종료를 위한 클래스 (Helper 파일에 따로 구현)


    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓
    private OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림
    private Thread workerThread = null; // 문자열 수신에 사용되는 쓰레드
    private byte[] readBuffer; // 수신 된 문자열을 저장하기 위한 버퍼
    private int readBufferPosition; // 버퍼 내 문자 저장 위치
    private int pariedDeviceCount;

    public SensorCallBack sensorCallBack; // 센서 값이 들어오면 HomeFragment로 센서값 전달을 위한 콜백함수

    // 센서값 콜백 인터페이스
    public interface SensorCallBack {
        void updateNumber();
    }

    // 콜백 동기화 함수
    public void setSensorData(SensorCallBack sensorCallBack){
        this.sensorCallBack = sensorCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
        initFragment();

        // 백 버튼 2번 종료 활성화
        backPressCloseHandler = new BackPressCloseHandler(this);

        //초기 알람 켜기
        Constant.isOnAlarm=true;
        btnAlarm.setChecked(true);

        initBluetooth();
    }

    // 백 버튼 2번 종료 활성화
    @Override public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        vibrator.cancel(); // 어플 종료시 진동 종료
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vibrator.cancel(); // 어플 종료시 진동 종료
        try {
            bluetoothSocket.close(); // 어플 종료시 블루투스 통신 소켓 제거
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 블루투스 초기화
    private void initBluetooth(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터 연결
        if(bluetoothAdapter == null) { // 디바이스가 블루투스를 지원하지 않을 때
            Toast.makeText(getApplicationContext()
                    , "블루투스를 사용할 수 없습니다"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }
        else { // 디바이스가 블루투스를 지원 할 때
            if(bluetoothAdapter.isEnabled()) { // 블루투스가 활성화 상태 (기기에 블루투스가 켜져있음)
                selectBluetoothDevice(); // 블루투스 디바이스 선택 함수 호출
            }
            else { // 블루투스가 비 활성화 상태 (기기에 블루투스가 꺼져있음)
                // 블루투스를 활성화 하기 위한 다이얼로그 출력
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // 선택한 값이 onActivityResult 함수에서 콜백된다.
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            }
        }
    }

    public void selectBluetoothDevice() {
        // 이미 페어링 되어있는 블루투스 기기 찾기
        devices = bluetoothAdapter.getBondedDevices();

        // 페어링 된 디바이스의 크기를 저장
        pariedDeviceCount = devices.size();

        // 페어링 되어있는 장치가 없는 경우
        if(pariedDeviceCount == 0) {
            Toast.makeText(getApplicationContext()
                    , "기기와 연결이 실패했습니다"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        // 페어링 되어있는 장치가 있는 경우
        else {
            // 디바이스를 선택하기 위한 다이얼로그 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("페어링 된 블루투스 디바이스");

            // 페어링 된 각각의 디바이스의 이름과 주소를 저장
            List<String> list = new ArrayList<>();

            // 모든 디바이스의 이름을 리스트에 추가
            for(BluetoothDevice bluetoothDevice : devices) {
                list.add(bluetoothDevice.getName());
            }

            // List를 CharSequence 배열로 변경
            final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);
            list.toArray(new CharSequence[list.size()]);

            // 해당 아이템을 눌렀을 때 호출 되는 이벤트 리스너
            builder.setItems(charSequences, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 해당 디바이스와 연결하는 함수 호출
                    connectDevice(charSequences[which].toString());
                }
            });

            // 뒤로가기 버튼 누를 때 창이 안닫히도록 설정
            builder.setCancelable(false);

            // 다이얼로그 생성
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void connectDevice(String deviceName) {
        // 페어링 된 디바이스들을 모두 탐색
        for(BluetoothDevice tempDevice : devices) {
            // 사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료
            if(deviceName.equals(tempDevice.getName())) {
                bluetoothDevice = tempDevice;
                break;
            }
        }

        // UUID 생성
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        // Rfcomm 채널을 통해 블루투스 디바이스와 통신하는 소켓 생성
        try {
            // 블루투스 기기와 연결 소켓 지정 및 연결
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();

            // 블루투스 송수신 스트립 연결
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();

            receiveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveData() {
        final Handler handler = new Handler();
        // 데이터를 수신하기 위한 버퍼를 생성
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        // 데이터를 수신하기 위한 쓰레드 생성
        workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        // 데이터를 수신했는지 확인
                        int byteAvailable = inputStream.available();
                        // 데이터가 수신 된 경우
                        if(byteAvailable > 0) {
                            // 입력 스트림에서 바이트 단위로 읽음
                            byte[] bytes = new byte[byteAvailable];
                            inputStream.read(bytes);
                            // 입력 스트림 바이트를 한 바이트씩 읽음
                            for(int i = 0; i < byteAvailable; i++) {
                                byte tempByte = bytes[i];
                                // 개행문자를 기준으로 받음(한줄)
                                if(tempByte == '\n') {
                                    // readBuffer 배열을 encodedBytes로 복사
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    // 인코딩 된 바이트 배열을 문자열로 변환
                                    final String text = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            //받아온 데이터
                                            Log.v("DATA",text+"");
                                            Constant.strValue = text.split(" ");

                                            // 데이터 각 4개 구역으로 분류
                                            for(int i=0;i<4;i++) {
                                                Constant.doubleValue[i] = Double.valueOf(Constant.strValue[i]);
                                            }
                                            // Fragment의 콜백 함수에 데이터 수신 신호 보냄
                                            if (sensorCallBack != null) sensorCallBack.updateNumber();
                                        }
                                    });
                                } // 개행 문자가 아닐 경우
                                else {
                                    readBuffer[readBufferPosition++] = tempByte;
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        // 1초마다 받아옴
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        workerThread.start();
    }

    // 데이터 전송 함수
    void sendData(String text) {
        text += "\n";
        try{
            // 데이터 송신
            outputStream.write(text.getBytes());
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Main View 초기화
    private void initView(){
        btnHome = findViewById(R.id.btn_home);
        btnVideo = findViewById(R.id.btn_video);
        btnTimer = findViewById(R.id.btn_timer);
        btnAlarm = findViewById(R.id.btn_alarm);
        btnInfo = findViewById(R.id.btn_info);

        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    }

    // 리스너 초기화
    private void initListener() {
        btnHome.setOnClickListener(this);
        btnVideo.setOnClickListener(this);
        btnTimer.setOnClickListener(this);
        btnAlarm.setOnCheckedChangeListener(this);
        btnInfo.setOnClickListener(this);
    }

    // Fragment 초기화
    private void initFragment(){
        fragment = new HomeFragment();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace( R.id.main_fragment, fragment );
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * @param timer
     * 타이머는 TimerFragment 뿐만 아니라 다른 화면으로 전환하더라도 작동이 필요하기 때문에
     * Main에 따로 지정하였습니다.
     */
    // 타이머 지정
    public static void setTimer(int timer){
        _timer = new CountDownTimer(timer * 1000, 1000) { // 1초 단위로 시간지정
            public void onTick(long millisUntilFinished) {
                // 시간 설정 금지
                editHour.setFocusable(false);
                editMin.setFocusable(false);
                editSec.setFocusable(false);

                editHour.setClickable(false);
                editMin.setClickable(false);
                editSec.setClickable(false);

                // 시,분,초로 시간 변환
                int sec = Constant.TIME;
                int min = Constant.TIME/60;
                int hour = min/60;
                min = min%60;
                sec = sec%60;

                // 시간 출력 폼 00:00:00 설정
                if(hour==0){
                    editHour.setText("00");
                } else if(hour<10) {
                    String strHour = "0"+hour;
                    editHour.setText(strHour);
                } else{
                    String strHour = ""+hour;
                    editHour.setText(strHour);
                }
                if(min==0){
                    editMin.setText("00");
                } else if(min<10) {
                    String strMin = "0"+min;
                    editMin.setText(strMin);
                } else{
                    String strMin = ""+min;
                    editMin.setText(strMin);
                }
                if(sec==0){
                    editSec.setText("00");
                } else if(sec<10) {
                    String strSec = "0"+sec;
                    editSec.setText(strSec);
                } else{
                    String strSec = ""+sec;
                    editSec.setText(strSec);
                }
                Log.v("TIME",Constant.TIME+"");
                Constant.TIME--; // 시간 줄이기

                // 타이머가 1이 될 경우,
                if(Constant.TIME==1) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 시간 0으로 설정 및 진동 동작
                            editSec.setText("00");
                            long[] pattern = {1000, 3000};
                            vibrator.vibrate(pattern, 0);

                            // 각 버튼 클릭 true/false 설정 및 시간 설정 가능
                            editHour.setFocusable(true);
                            editMin.setFocusable(true);
                            editSec.setFocusable(true);

                            editHour.setFocusableInTouchMode(true);
                            editMin.setFocusableInTouchMode(true);
                            editSec.setFocusableInTouchMode(true);

                            editHour.setClickable(true);
                            editMin.setClickable(true);
                            editSec.setClickable(true);

                            btnPlay.setClickable(true);
                            btnPause.setClickable(false);
                            Constant.isPlay = false;
                        }
                    },2000);
                }
            }

            public void onFinish() {
                _timer.cancel();
            } // 타이머 중지 함수
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT :
                if(requestCode == RESULT_OK) { // '사용'을 눌렀을 때
                    selectBluetoothDevice(); // 블루투스 디바이스 선택 함수 호출
                } else { // '취소'를 눌렀을 때

                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 'i'버튼 클릭
            case R.id.btn_info:
                // 커스텀된 다이얼로그 생성 (res -> layout -> dialog_color_map이 있습니다.)
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View customLayout = View.inflate(this,R.layout.dialog_color_map,null);
                builder.setView(customLayout);

                // 색상 이미지 둥글게 지정
                customLayout.findViewById(R.id.img_1).setBackground(new ShapeDrawable(new OvalShape()));
                if(Build.VERSION.SDK_INT >= 21) {
                    customLayout.findViewById(R.id.img_1).setClipToOutline(true);
                }
                customLayout.findViewById(R.id.img_2).setBackground(new ShapeDrawable(new OvalShape()));
                if(Build.VERSION.SDK_INT >= 21) {
                    customLayout.findViewById(R.id.img_2).setClipToOutline(true);
                }
                customLayout.findViewById(R.id.img_3).setBackground(new ShapeDrawable(new OvalShape()));
                if(Build.VERSION.SDK_INT >= 21) {
                    customLayout.findViewById(R.id.img_3).setClipToOutline(true);
                }
                customLayout.findViewById(R.id.img_4).setBackground(new ShapeDrawable(new OvalShape()));
                if(Build.VERSION.SDK_INT >= 21) {
                    customLayout.findViewById(R.id.img_4).setClipToOutline(true);
                }
                customLayout.findViewById(R.id.img_5).setBackground(new ShapeDrawable(new OvalShape()));
                if(Build.VERSION.SDK_INT >= 21) {
                    customLayout.findViewById(R.id.img_5).setClipToOutline(true);
                }
                customLayout.findViewById(R.id.img_6).setBackground(new ShapeDrawable(new OvalShape()));
                if(Build.VERSION.SDK_INT >= 21) {
                    customLayout.findViewById(R.id.img_6).setClipToOutline(true);
                }
                customLayout.findViewById(R.id.img_7).setBackground(new ShapeDrawable(new OvalShape()));
                if(Build.VERSION.SDK_INT >= 21) {
                    customLayout.findViewById(R.id.img_7).setClipToOutline(true);
                }

                // 다이얼로그 띄움
                dialog = builder.create();
                dialog.show();
                break;

            // 하단 home 버튼
            case R.id.btn_home:
               if(!RESENT_BUTTON.equals("home")) {
                   RESENT_BUTTON = "home";
                   // Fragment 화면 변경
                   getSupportFragmentManager()
                           .beginTransaction()
                           .replace(R.id.main_fragment, new HomeFragment())
                           .addToBackStack(null)
                           .detach(fragment)
                           .attach(fragment)
                           .commit();
                    // 하단 버튼 색 변경
                   btnHome.setBackground(getResources().getDrawable(R.drawable.box_button_background));
                   btnHome.setImageResource(R.drawable.ic_home_white);
                   btnVideo.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                   btnVideo.setImageResource(R.drawable.ic_video_black);
                   btnTimer.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                   btnTimer.setImageResource(R.drawable.ic_timer_black);
               }
                break;

            // 하단 video 버튼
            case R.id.btn_video:
                if(!RESENT_BUTTON.equals("video")) {
                    RESENT_BUTTON = "video";
                    // Fragment 화면 변경
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment, new VideoFragment())
                            .addToBackStack(null)
                            .detach(fragment)
                            .attach(fragment)
                            .commit();
                    // 하단 버튼 색 변경
                    btnVideo.setBackground(getResources().getDrawable(R.drawable.box_button_background));
                    btnVideo.setImageResource(R.drawable.ic_video_white);
                    btnHome.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    btnHome.setImageResource(R.drawable.ic_home_black);
                    btnTimer.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    btnTimer.setImageResource(R.drawable.ic_timer_black);
                }
                break;

            // 하단 timer 버튼
            case R.id.btn_timer:
                if(!RESENT_BUTTON.equals("timer")) {
                    RESENT_BUTTON = "timer";
                    // Fragment 화면 변경
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment, new TimerFragment())
                            .addToBackStack(null)
                            .detach(fragment)
                            .attach(fragment)
                            .commit();

                    // 하단 버튼 색 변경
                    btnTimer.setBackground(getResources().getDrawable(R.drawable.box_button_background));
                    btnTimer.setImageResource(R.drawable.ic_timer_white);
                    btnHome.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    btnHome.setImageResource(R.drawable.ic_home_black);
                    btnVideo.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    btnVideo.setImageResource(R.drawable.ic_video_black);
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        // 진동 버튼 클릭
        if (isChecked){
            //알람 켜기
            sendData("1"); // 블루투스 기기에 1 전송
            Constant.isOnAlarm=true;
        }else{
            //알람 끄기
            sendData("0"); // 블루투스 기기에 0 전송
            Constant.isOnAlarm=false;
        }
    }
}
