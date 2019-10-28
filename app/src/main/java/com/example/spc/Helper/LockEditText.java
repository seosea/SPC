package com.example.spc.Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

@SuppressLint("AppCompatCustomView")
public class LockEditText extends EditText {
    /* Must use this constructor in order for the layout files to instantiate the class properly */
    public LockEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    //키보드 닫힘
    @Override
    public boolean onKeyPreIme (int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP) {

            int time;
            if(this.getText().toString().equals("")){
                time=0;
            } else {
                time =Integer.parseInt(this.getText().toString());
            }

            if(time>59) time = 59;

            if(time==0){
                this.setText("00");
            } else if(time<10) {
                String strHour = "0"+time;
                this.setText(strHour);
            } else{
                String strHour = ""+time;
                this.setText(strHour);
            }
            return false;
        }
        return false;
    }

}
