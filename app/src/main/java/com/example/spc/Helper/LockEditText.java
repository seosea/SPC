package com.example.spc.Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * 키보드가 닫혔을때, 시간 폼을 지정해주기 위한 함수입니다
 * 기존에 입력을 위한 EditText 라는 컴포넌트는 키보드가 내려갈 경우에 대한 이벤트를 불러올 수 없기 때문에
 * 이렇게 EditText 를 상속받아서 EditText 기능 + 코드로 추가 기능 작성을 했습니다.
 */
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

            // 시간 폼 지정
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
