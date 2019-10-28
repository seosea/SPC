package com.example.spc.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spc.Activity.MainActivity;
import com.example.spc.Helper.Constant;
import com.example.spc.Helper.LockEditText;
import com.example.spc.R;

import static com.example.spc.Activity.MainActivity._timer;
import static com.example.spc.Activity.MainActivity.setTimer;
import static com.example.spc.Activity.MainActivity.vibrator;

public class TimerFragment extends Fragment implements View.OnClickListener
{
    // 시간 입력
    public static LockEditText editHour, editMin, editSec;
    // 각 재생, 정지, 일시정지 버튼
    public static ImageButton btnPlay, btnStop, btnPause;

    // 키보드
    private InputMethodManager imm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        // 키보드 초기화
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        initView(view);
        initListener();

        // 초기 정지 버튼과 일시정지는 입력 못하도록 설정
        btnStop.setClickable(false);
        btnPause.setClickable(false);

        // 키보드 숨김
        hideKeyboard();
        return view;
    }

    // fragment 화면 출력시,
    @Override
    public void onResume() {
        super.onResume();
        if(Constant.isPause){ // 일시정지 상태일 경우

            // 현재 저장된 값을 그대로 받아와 폼 00:00:00 맞춰 출력
            if(Constant.HOUR == 0) {
                editHour.setText("00");
            } else if(Constant.HOUR<10) {
            String strHour = "0"+Constant.HOUR;
            editHour.setText(strHour);
             } else {
                editHour.setText(String.valueOf(Constant.HOUR));
            }

            if(Constant.MIN == 0) {
                editMin.setText("00");
            } else if(Constant.MIN<10) {
                String strMin = "0"+Constant.MIN;
                editMin.setText(strMin);
            } else {
                editMin.setText(String.valueOf(Constant.MIN));
            }

            if(Constant.SEC == 0) {
                editSec.setText("00");
            } else if(Constant.SEC<10) {
                String strSec = "0"+Constant.SEC;
                editSec.setText(strSec);
            } else {
                editSec.setText(String.valueOf(Constant.SEC));
            }

            btnStop.setClickable(true);
        } else {
            // 재생 상태일 경우
            btnStop.setClickable(true);
            btnPause.setClickable(true);
        }
    }

    /**
     * Timer 함수의 경우 thread를 사용하는데, thread 내부에서는 화면의 View를 변경할 수 없습니다.
     * 따라서 View에 접근하기 위해 Handler를 사용하는 겁니다
     */
    // View 핸들러
    final Handler handler = new Handler(){
        @Override public void handleMessage(Message msg){
            int hour, min, sec;

            // 입력한 시간 text 값을 숫자로 변경
            if(editHour.getText().toString().equals("")){
                hour=0;
            } else {
                hour =Integer.parseInt(editHour.getText().toString());
            }

            if(editMin.getText().toString().equals("")){
                min=0;
            } else {
                min =Integer.parseInt(editMin.getText().toString());
            }

            if(editSec.getText().toString().equals("")){
                sec=0;
            } else {
                sec =Integer.parseInt(editSec.getText().toString());
            }

            // 59 초과의 수일 경우 59로 지정
            if(hour>59) hour = 59;
            if(min>59) min = 59;
            if(sec>59) sec = 59;

            // 시간 폼 00:00:00 맞춰 출력
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
        }
    };

    // View 초기화
    private void initView(View view){
        editHour = view.findViewById(R.id.edit_hour);
        editMin = view.findViewById(R.id.edit_min);
        editSec = view.findViewById(R.id.edit_sec);

        btnPlay = view.findViewById(R.id.btn_play);
        btnStop = view.findViewById(R.id.btn_stop);
        btnPause = view.findViewById(R.id.btn_pause);
    }

    // 리스너 설정
    private void initListener(){
        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnPause.setOnClickListener(this);

        // 각각 시, 분, 초 입력 창에서 포커스가 사라졌을때 (다른 곳을 클릭) 나타날 예외 처리
        editHour.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                }else {
                    int time;
                    // 빈 칸일 경우 -> 0으로
                    if(editHour.getText().toString().equals("")){
                        time = 0;
                    } else {
                        time  =Integer.parseInt(editHour.getText().toString());
                    }

                    // 59 초과일 경우 -> 59
                    if(time>59) time = 59;

                    // 00:00:00 폼
                    if(time==0){
                        editHour.setText("00");
                    } else if(time<10) {
                        String strHour = "0"+time;
                        editHour.setText(strHour);
                    } else{
                        String strHour = ""+time;
                        editHour.setText(strHour);
                    }
                }
            }
        });

        editMin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                }else {
                    int time;
                    // 빈 칸일 경우 -> 0으로
                    if(editMin.getText().toString().equals("")){
                        time = 0;
                    } else {
                        time  =Integer.parseInt(editMin.getText().toString());
                    }

                    // 59 초과일 경우 -> 59
                    if(time>59) time = 59;

                    // 00:00:00 폼
                    if(time==0){
                        editMin.setText("00");
                    } else if(time<10) {
                        String strHour = "0"+time;
                        editMin.setText(strHour);
                    } else{
                        String strHour = ""+time;
                        editMin.setText(strHour);
                    }
                }
            }
        });

        editSec.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                }else {
                    int time;
                    // 빈 칸일 경우 -> 0으로
                    if(editSec.getText().toString().equals("")){
                        time = 0;
                    } else {
                        time  =Integer.parseInt(editSec.getText().toString());
                    }

                    // 59 초과일 경우 -> 59
                    if(time>59) time = 59;

                    // 00:00:00 폼
                    if(time==0){
                        editSec.setText("00");
                    } else if(time<10) {
                        String strHour = "0"+time;
                        editSec.setText(strHour);
                    } else{
                        String strHour = ""+time;
                        editSec.setText(strHour);
                    }
                }
            }
        });

        // 각각 시, 분, 초 입력 창에서 완료 버튼을 눌렀을때, 입력한 값에서 나타날 예외 처리 (handler 111번째 줄 함수로 이동)
        editHour.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    Log.v("keyboard", "enter");
                    Message msgProfile = handler.obtainMessage();
                    handler.sendMessage(msgProfile);
                }
                return false;
            }
        });

        editMin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    Log.v("keyboard", "enter");
                    Message msgProfile = handler.obtainMessage();
                    handler.sendMessage(msgProfile);
                }
                return false;
            }
        });

        editSec.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    Log.v("keyboard", "enter");
                    Message msgProfile = handler.obtainMessage();
                    handler.sendMessage(msgProfile);
                }
                return false;
            }
        });
    }

    //키보드 숨기기
    private void hideKeyboard(){
        imm.hideSoftInputFromWindow(editHour.getWindowToken(), 0 );
        imm.hideSoftInputFromWindow(editMin.getWindowToken(), 0 );
        imm.hideSoftInputFromWindow(editSec.getWindowToken(), 0 );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            // 재생 버튼
            case R.id.btn_play:
                int hour, min, sec;
                // 재생 버튼만 동작
                btnPlay.setClickable(false);
                btnStop.setClickable(true);
                btnPause.setClickable(true);
                Constant.isPause = false;

                // 시간이 0일 경우 동작안함
                if(Constant.TIME==0)
                    btnPlay.setClickable(true);

                // 받은 시간을 초로 환산
                if (editHour.getText().toString().equals("")) hour = 0;
                else if (Integer.parseInt(editHour.getText().toString()) > 59)
                    hour = 59 * 3600;
                else hour = Integer.parseInt(editHour.getText().toString()) * 3600;

                if (editMin.getText().toString().equals("")) min = 0;
                else if (Integer.parseInt(editMin.getText().toString()) > 59) min = 59 * 60;
                else min = Integer.parseInt(editMin.getText().toString()) * 60;

                if (editSec.getText().toString().equals("")) sec = 0;
                else if (Integer.parseInt(editSec.getText().toString()) > 59) sec = 59;
                else sec = Integer.parseInt(editSec.getText().toString());

                Constant.TIME = hour + min + sec;

                /**
                 * Timer 함수는 1초에서 끝나기 때문에
                 * 시간을 1로 지정할 경우 동작이 안되서 따로 핸들러로 delay 해주었습니다
                 */
                if (Constant.TIME == 1) {
                    Constant.isPlay = true;
                    // 시간 설정 금지
                    editHour.setFocusable(false);
                    editMin.setFocusable(false);
                    editSec.setFocusable(false);

                    editHour.setClickable(false);
                    editMin.setClickable(false);
                    editSec.setClickable(false);

                    // 1초 후 타이머 진동
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editSec.setText("00");
                            long[] pattern = {1000, 3000}; // 3초 진동, 1초 딜레이
                            vibrator.vibrate(pattern, 0); // 0이 무한 진동

                            // 시간 입력 가능 및 터치 가능, 각 버튼 클릭 true/false 지정
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
                    }, 1000);
                }
                // 시간이 1초 초과로 존재할 경우
                else if (Constant.TIME != 0) {
                    setTimer(Constant.TIME); //Main Activity에 있는 타이머 설정
                    _timer.start(); // 타이머 시작

                    Constant.isPlay = true;

                    //시간 설정 불가
                    editHour.setFocusable(false);
                    editMin.setFocusable(false);
                    editSec.setFocusable(false);

                    editHour.setClickable(false);
                    editMin.setClickable(false);
                    editSec.setClickable(false);
                }

                hideKeyboard(); // 키보드 숨김

                break;

            // 정지 버튼
            case R.id.btn_stop:
                // 각 버튼 true/false
                btnPlay.setClickable(true);
                btnStop.setClickable(false);
                btnPause.setClickable(false);
                Constant.isPause = false;

                vibrator.cancel(); // 진동 정지

                // 시간 입력 가능
                editHour.setFocusable(true);
                editMin.setFocusable(true);
                editSec.setFocusable(true);

                editHour.setFocusableInTouchMode(true);
                editMin.setFocusableInTouchMode(true);
                editSec.setFocusableInTouchMode(true);

                editHour.setClickable(true);
                editMin.setClickable(true);
                editSec.setClickable(true);

                if (Constant.isPlay)_timer.onFinish(); // 타이머 작동 시에 중지

                // 모든 시간 초기화
                editHour.setText("00");
                editMin.setText("00");
                editSec.setText("00");
                Constant.TIME = 0;

                break;

            // 일시정지 버튼
            case R.id.btn_pause:
                // 각 버튼 true/false 지정
                Constant.isPause = true;
                btnPlay.setClickable(true);
                btnStop.setClickable(true);
                btnPause.setClickable(false);

                _timer.onFinish(); // 타이머 중지

                // 현재 타이머 시간 저장
                Constant.HOUR = Integer.parseInt(editHour.getText().toString());
                Constant.MIN = Integer.parseInt(editMin.getText().toString());
                Constant.SEC = Integer.parseInt(editSec.getText().toString())-1;

                // Log에 시간 띄움
                Log.v("time", Constant.HOUR + " " + Constant.MIN + " " + Constant.SEC);
                Log.v("time", Constant.TIME + "");

                /**
                 * Timer는 일시정지에서 1초 정도 차이가 나기 때문에
                 * 일단 따로 시, 분, 초를 다시 띄우는 방식으로 하였습니다.
                 */
                // 시간 변환
                sec = Constant.TIME;
                min = Constant.TIME / 60;
                hour = min / 60;
                min = min % 60;
                sec = sec % 60;

                // 저장된 시간을 화면에 띄움 (폼 00:00:00)
                if (hour == 0) {
                    editHour.setText("00");
                } else if (hour < 10) {
                    String strHour = "0" + hour;
                    editHour.setText(strHour);
                } else {
                    String strHour = "" + hour;
                    editHour.setText(strHour);
                }

                if (min == 0) {
                    editMin.setText("00");
                } else if (min < 10) {
                    String strMin = "0" + min;
                    editMin.setText(strMin);
                } else {
                    String strMin = "" + min;
                    editMin.setText(strMin);
                }

                if (sec == 0) {
                    editSec.setText("00");
                } else if (sec < 10) {
                    String strSec = "0" + sec;
                    editSec.setText(strSec);
                } else {
                    String strSec = "" + sec;
                    editSec.setText(strSec);
                }

                // 시간 입력 금지
                editHour.setFocusable(false);
                editMin.setFocusable(false);
                editSec.setFocusable(false);

                editHour.setClickable(false);
                editMin.setClickable(false);
                editSec.setClickable(false);

                _timer.cancel(); // 타이머 다시 한 번 중지
                break;
        }
    }
}
