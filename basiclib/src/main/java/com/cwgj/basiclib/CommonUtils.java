package com.cwgj.basiclib;

import android.content.Context;
import android.media.AudioManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * +----------------------------------------------------------------------
 * |  说明     ：
 * +----------------------------------------------------------------------
 * | 创建者   :  ldw
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/6/1 15:36
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/
public class CommonUtils {

    /**
     * 隐藏底部的虚拟按键
     * @param window
     */
    public static void setHideBottomNavi(Window window){
        View decorView = window.getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }


    /**
     * 设置喇叭最大音量
     * @param context
     */
    public static void setMaxVolume(Context context){

        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //获取最大媒体音量值
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //设置媒体音量为最大值，当然也可以设置媒体音量为其他给定的值
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max,0);

    }

//    /**
//     * 重新启动App -> 杀进程,会短暂黑屏,启动慢
//     */
//    public void restartApp() {
//        //启动页
//        Intent intent = new Intent(BaseApplication.instance(), SplashActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        BaseApplication.instance().startActivity(intent);
//        android.os.Process.killProcess(android.os.Process.myPid());
//    }


}
