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

    private VideoView videoView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        //Video play

        videoView = (VideoView) view.findViewById(R.id.vv);
        String uriPath = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.video;

        videoView.setVideoPath(uriPath);

        MediaController mediaController = new MediaController(getContext());
        mediaController.setAnchorView(videoView);
        mediaController.setPadding(0, 0, 0, 0); //상위 레이어의 바닥에서 얼마 만큼? 패딩을 줌
        videoView.setMediaController(mediaController);

        videoView.start();
        return view;
    }
}
