package com.device;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ToastUtils;
import com.device.player.MediaView;
import com.example.cwgj.lprlib.R;

/**
 * +----------------------------------------------------------------------
 * |  说明     ：
 * +----------------------------------------------------------------------
 * | 创建者   :  ldw
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/5/3 16:34
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/
public class VideoSetView extends LinearLayout {

    public static final int  LONG_TIME_NO_DATA = 0x1001;

    private boolean  playFlag = false;

    MediaView mMediaView;

    TextView tv_device_name;

    Button btn_operate_video;


    public VideoSetView(Context context) {
        this(context, null);
    }

    public VideoSetView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_video_view, this);
        RelativeLayout video_view_layout = view.findViewById(R.id.video_view_layout);
        tv_device_name = view.findViewById(R.id.tv_device_name);
        mMediaView = new MediaView(getContext());
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        mMediaView.setLayoutParams(lp);
        video_view_layout.addView(mMediaView,0);
        mMediaView.setVideoView(this);
        btn_operate_video = view.findViewById(R.id.btn_operate_video);
        btn_operate_video.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnVideoLprListener == null)
                    return;
                if(playFlag)
                  mOnVideoLprListener.stopVideo();
                else
                  mOnVideoLprListener.startVideo();
            }
        });
        view.findViewById(R.id.btn_operate_door).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnVideoLprListener == null)
                    return;
                mOnVideoLprListener.operateDoor();
            }
        });

        view.findViewById(R.id.btn_shot_up).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnVideoLprListener == null)
                    return;
                mOnVideoLprListener.shotUp();
            }
        });
    }

    public void setDeviceName(String name){
        tv_device_name.setText(name);
    }

    public void startPlay( ) {
        if(mMediaView == null)
            return;
        if(TextUtils.isEmpty(mMediaView.getUrlip())){
            ToastUtils.showToast("请先打开设备");
            return;
        }
        if(mMediaView.isVideoPlaying())
            mMediaView.stopPlay();
        mMediaView.startPlay();
        playFlag = true;
        btn_operate_video.setText("关闭");
    }

    public void stopPlay( ) {
        if(mMediaView.isVideoPlaying())
            mMediaView.stopPlay();
        playFlag = false;
        btn_operate_video.setText("打开");
    }

    public void pause() {
        mMediaView.pause();
    }

    public void resume() {
        mMediaView.resume();
    }

    public void longTimeNoData(){
        ((Activity)getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
             stopPlay();
             startPlay();
            }
        });

    }

    public void setUrlip(String ip ) {
        mMediaView.setUrlip(ip);
    }

   OnVideoLprListener mOnVideoLprListener;

    public void setOnVideoLprListener(OnVideoLprListener listener){
        this.mOnVideoLprListener = listener;
    }

   public interface OnVideoLprListener{
        void startVideo();
        void stopVideo();
        void operateDoor();
        void shotUp();
   }


}
