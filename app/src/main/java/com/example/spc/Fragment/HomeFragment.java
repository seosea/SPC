package com.example.spc.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spc.Activity.MainActivity;
import com.example.spc.Helper.Constant;
import com.example.spc.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends Fragment {

    private RelativeLayout rlLeftLeg, rlLeftHip, rlRightLeg, rlRightHip;
    private TextView tvLeftLeg, tvLeftHip, tvRightLeg, tvRightHip;

    private TextView tvPosition;
    private LinearLayout llNotGood;
    private TextView tvGood;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view);
        setView(25,25,25,25);

        MainActivity.SensorCallBack sensorCallBack = new MainActivity.SensorCallBack() {
            @Override
            public void updateNumber() {
                setView(Constant.doubleValue[0],Constant.doubleValue[3],Constant.doubleValue[1],Constant.doubleValue[2]);
            }
        };
        ((MainActivity)getActivity()).setSensorData(sensorCallBack);

        return view;
    }

    private void initView(View view){
        tvPosition = view.findViewById(R.id.tv_position_home);

        rlLeftLeg = view.findViewById(R.id.rl_left_leg);
        rlLeftHip = view.findViewById(R.id.rl_left_hip);
        rlRightLeg = view.findViewById(R.id.rl_right_leg);
        rlRightHip = view.findViewById(R.id.rl_right_hip);

        tvLeftLeg = view.findViewById(R.id.tv_left_leg);
        tvLeftHip = view.findViewById(R.id.tv_left_hip);
        tvRightLeg = view.findViewById(R.id.tv_right_leg);
        tvRightHip = view.findViewById(R.id.tv_right_hip);

        llNotGood = view.findViewById(R.id.ll_not_good);
        tvGood = view.findViewById(R.id.tv_good);
    }

    private void setView(double leftLeg, double leftHip, double rightLeg, double rightHip) {
        tvLeftLeg.setText(leftLeg + "%");
        tvLeftHip.setText(leftHip + "%");
        tvRightLeg.setText(rightLeg + "%");
        tvRightHip.setText(rightHip + "%");

        if (leftLeg < 17.99) rlLeftLeg.setBackgroundResource(R.color.c0_18);
        else if (leftLeg < 19.49) rlLeftLeg.setBackgroundResource(R.color.c18_19);
        else if (leftLeg < 21.99) rlLeftLeg.setBackgroundResource(R.color.c19_22);
        else if (leftLeg < 27.99) {
            rlLeftLeg.setBackgroundResource(R.color.c22_28_valance);
        }
        else if (leftLeg < 28.99) rlLeftLeg.setBackgroundResource(R.color.c28_29);
        else if (leftLeg < 29.99) rlLeftLeg.setBackgroundResource(R.color.c29_30);
        else rlLeftLeg.setBackgroundResource(R.color.c30_100);

        if (leftHip < 17.99) rlLeftHip.setBackgroundResource(R.color.c0_18);
        else if (leftHip < 19.49) rlLeftHip.setBackgroundResource(R.color.c18_19);
        else if (leftHip < 21.99) rlLeftHip.setBackgroundResource(R.color.c19_22);
        else if (leftHip < 27.99) {
            rlLeftHip.setBackgroundResource(R.color.c22_28_valance);
        }
        else if (leftHip < 28.99) rlLeftHip.setBackgroundResource(R.color.c28_29);
        else if (leftHip < 29.99) rlLeftHip.setBackgroundResource(R.color.c29_30);
        else rlLeftHip.setBackgroundResource(R.color.c30_100);

        if (rightLeg < 17.99) rlRightLeg.setBackgroundResource(R.color.c0_18);
        else if (rightLeg < 19.49) rlRightLeg.setBackgroundResource(R.color.c18_19);
        else if (rightLeg < 21.99) rlRightLeg.setBackgroundResource(R.color.c19_22);
        else if (rightLeg < 27.99) {
            rlRightLeg.setBackgroundResource(R.color.c22_28_valance);
        }
        else if (rightLeg < 28.99) rlRightLeg.setBackgroundResource(R.color.c28_29);
        else if (rightLeg < 29.99) rlRightLeg.setBackgroundResource(R.color.c29_30);
        else rlRightLeg.setBackgroundResource(R.color.c30_100);

        if (rightHip < 17.99) rlRightHip.setBackgroundResource(R.color.c0_18);
        else if (rightHip < 19.49) rlRightHip.setBackgroundResource(R.color.c18_19);
        else if (rightHip < 21.99) rlRightHip.setBackgroundResource(R.color.c19_22);
        else if (rightHip < 27.99) {
            rlRightHip.setBackgroundResource(R.color.c22_28_valance);
        }
        else if (rightHip < 28.99) rlRightHip.setBackgroundResource(R.color.c28_29);
        else if (rightHip < 29.99) rlRightHip.setBackgroundResource(R.color.c29_30);
        else rlRightHip.setBackgroundResource(R.color.c30_100);

        if(22<leftLeg && leftLeg<27.99
            && 22<leftHip && leftHip<27.99
            && 22<rightLeg && rightLeg<27.99
            && 22<rightHip && rightHip<27.99){
            tvGood.setVisibility(View.VISIBLE);
            llNotGood.setVisibility(View.INVISIBLE);
        } else {
            tvGood.setVisibility(View.GONE);
            llNotGood.setVisibility(View.VISIBLE);
        }

        Map<String, Double> map = new HashMap<>();
        map.put("좌측 다리", leftLeg);
        map.put("좌측 엉덩이", leftHip);
        map.put("우측 다리", rightLeg);
        map.put("우측 엉덩이", rightHip);

        Iterator it = sortByValue(map).iterator();

        String temp = (String) it.next();
        tvPosition.setText(temp);
    }

    // 정렬
    public static List sortByValue(final Map map) {
        List<String> list = new ArrayList();
        list.addAll(map.keySet());
        Collections.sort(list,new Comparator() {
            public int compare(Object o1,Object o2) {
                Object v1 = map.get(o1);
                Object v2 = map.get(o2);
                return ((Comparable) v2).compareTo(v1);
            }
        });
        return list;

    }
}
