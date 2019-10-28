package com.example.spc.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spc.R;

public class VideoFragment extends Fragment {

    private VideoView videoView; // 비디오 View

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        //Video play

        videoView = (VideoView) view.findViewById(R.id.vv);

        // 안드로이드 스튜디오 내부 영상이 저장된 위치 주소를 받아와 입력
        String uriPath = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.video;

        videoView.setVideoPath(uriPath); // 해당 uri 비디오를 연결

        // 미디어 컨트롤러를 통해 비디오를 띄움
        MediaController mediaController = new MediaController(getContext());
        mediaController.setAnchorView(videoView);
        mediaController.setPadding(0, 0, 0, 0); //상위 레이어의 바닥에서 얼마 만큼? 패딩을 줌
        videoView.setMediaController(mediaController);

        videoView.start(); // 비디오 바로 재생
        return view;
    }
}
