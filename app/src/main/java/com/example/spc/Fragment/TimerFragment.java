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
    public static LockEditText editHour, editMin, editSec;
    public static ImageButton btnPlay, btnStop, btnPause;

    private InputMethodManager imm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        initView(view);
        initListener();

        btnStop.setClickable(false);
        btnPause.setClickable(false);
        hideKeyboard();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Constant.isPause){
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
            btnStop.setClickable(true);
            btnPause.setClickable(true);
        }
    }

    final Handler handler = new Handler(){
        @Override public void handleMessage(Message msg){
            int hour, min, sec;

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

            if(hour>59) hour = 59;
            if(min>59) min = 59;
            if(sec>59) sec = 59;

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

    private void initView(View view){
        editHour = view.findViewById(R.id.edit_hour);
        editMin = view.findViewById(R.id.edit_min);
        editSec = view.findViewById(R.id.edit_sec);

        btnPlay = view.findViewById(R.id.btn_play);
        btnStop = view.findViewById(R.id.btn_stop);
        btnPause = view.findViewById(R.id.btn_pause);
    }

    private void initListener(){
        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnPause.setOnClickListener(this);

        editHour.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                }else {
                    int time;
                    if(editHour.getText().toString().equals("")){
                        time = 0;
                    } else {
                        time  =Integer.parseInt(editHour.getText().toString());
                    }

                    if(time>59) time = 59;

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
                    if(editMin.getText().toString().equals("")){
                        time = 0;
                    } else {
                        time  =Integer.parseInt(editMin.getText().toString());
                    }

                    if(time>59) time = 59;

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
                    if(editSec.getText().toString().equals("")){
                        time = 0;
                    } else {
                        time  =Integer.parseInt(editSec.getText().toString());
                    }

                    if(time>59) time = 59;

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

    private void hideKeyboard(){
        //키보드 숨기기
        imm.hideSoftInputFromWindow(editHour.getWindowToken(), 0 );
        imm.hideSoftInputFromWindow(editMin.getWindowToken(), 0 );
        imm.hideSoftInputFromWindow(editSec.getWindowToken(), 0 );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_play:
                int hour, min, sec;
                btnPlay.setClickable(false);
                btnStop.setClickable(true);
                btnPause.setClickable(true);
                Constant.isPause = false;

                if(Constant.TIME==0)
                    btnPlay.setClickable(true);

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

                if (Constant.TIME == 1) {
                    Constant.isPlay = true;
                    editHour.setFocusable(false);
                    editMin.setFocusable(false);
                    editSec.setFocusable(false);

                    editHour.setClickable(false);
                    editMin.setClickable(false);
                    editSec.setClickable(false);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editSec.setText("00");
                            long[] pattern = {1000, 3000};
                            vibrator.vibrate(pattern, 0);

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
                } else if (Constant.TIME != 0) {

                    setTimer(Constant.TIME);
                    _timer.start();
                    Constant.isPlay = true;
                    editHour.setFocusable(false);
                    editMin.setFocusable(false);
                    editSec.setFocusable(false);

                    editHour.setClickable(false);
                    editMin.setClickable(false);
                    editSec.setClickable(false);
                }

                hideKeyboard();

                break;
            case R.id.btn_stop:
                btnPlay.setClickable(true);
                btnStop.setClickable(false);
                btnPause.setClickable(false);
                Constant.isPause = false;

                vibrator.cancel();

                editHour.setFocusable(true);
                editMin.setFocusable(true);
                editSec.setFocusable(true);

                editHour.setFocusableInTouchMode(true);
                editMin.setFocusableInTouchMode(true);
                editSec.setFocusableInTouchMode(true);

                editHour.setClickable(true);
                editMin.setClickable(true);
                editSec.setClickable(true);

                if (Constant.isPlay)_timer.onFinish();

                editHour.setText("00");
                editMin.setText("00");
                editSec.setText("00");
                Constant.TIME = 0;

                break;
            case R.id.btn_pause:
                Constant.isPause = true;
                btnPlay.setClickable(true);
                btnStop.setClickable(true);
                btnPause.setClickable(false);

                _timer.onFinish();

                Constant.HOUR = Integer.parseInt(editHour.getText().toString());
                Constant.MIN = Integer.parseInt(editMin.getText().toString());
                Constant.SEC = Integer.parseInt(editSec.getText().toString())-1;

                Log.v("time", Constant.HOUR + " " + Constant.MIN + " " + Constant.SEC);
                Log.v("time", Constant.TIME + "");

                sec = Constant.TIME;
                min = Constant.TIME / 60;
                hour = min / 60;
                min = min % 60;
                sec = sec % 60;

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

                editHour.setFocusable(false);
                editMin.setFocusable(false);
                editSec.setFocusable(false);

                editHour.setClickable(false);
                editMin.setClickable(false);
                editSec.setClickable(false);

                _timer.cancel();
                break;
        }
    }
}
