package com.example.spc.Helper;

public class Constant {
    // 시, 분, 초
    public static int HOUR =0;
    public static int MIN =1;
    public static int SEC =2;
    //알람 on/off
    public static boolean isOnAlarm;

    //타이머 상태
    public static boolean isPause=false;
    public static boolean isPlay=false;
    //타이머 시간
    public static int TIME=0;

    //센서값
    public static double[] doubleValue = new double[4];
    public static String[] strValue = new String[4];
}
