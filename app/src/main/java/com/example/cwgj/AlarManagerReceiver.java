package com.example.cwgj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.cwgj.imgupload.utils.UploadPicManager;
import com.device.VideoDeviceManager;
import com.example.cwgj.toptechvideolpr.AlarManagerHelper;


/**
 * +----------------------------------------------------------------------
 * |  说明     ：定时广播
 * +----------------------------------------------------------------------
 * | 创建者   :  zzh
 * +----------------------------------------------------------------------
 *   时　　间 ：2017/10/12 11:19
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
*/
public class AlarManagerReceiver extends BroadcastReceiver {

    private static final String TAG = "xxxxxxxxx";

    //相机action
    public static String ACTION_ALARMANAGER_CAMERA_CONNEC_NOTIFY = "ACTION_ALARMANAGER_CAMERA_CONNEC_NOTIFY";
    //相机code
    public static int REQUEST_CODE_CAMERA_CONNEC_NOTIFY = 100;

    //图片oss action
    public static String ACTION_ALARMANAGER_OSS_NOTIFY = "ACTION_ALARMANAGER_OSS_NOTIFY";
    //图片oss code
    public static int REQUEST_CODE_OSS_NOTIFY = 101;

    //图片清理
    public static String ACTION_ALARMANAGER_CLEAN_NOTIFY = "ACTION_ALARMANAGER_CLEAN_NOTIFY";
    //图片清理
    public static int REQUEST_CODE_CLEAN_NOTIFY = 102;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

       if(ACTION_ALARMANAGER_CAMERA_CONNEC_NOTIFY.equals(action)){
                if(!VideoDeviceManager.getInstance().isConnected()){
                    Log.d(TAG, "相机断开,需要重连");
                    VideoDeviceManager.getInstance().resetDevice();
                }else {
//                    Log.d(TAG, " 相机已经连接");
                }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                  AlarManagerHelper.initCameraConnAlarmManager(context);
            }
        }

        if(ACTION_ALARMANAGER_OSS_NOTIFY.equals(action)){
           //队列上传数据库图片list
            UploadPicManager.getInstance().uploadPicsAsynSilently();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                AlarManagerHelper.initOSSAlarmManager(context);
            }
        }

        if(ACTION_ALARMANAGER_CLEAN_NOTIFY.equals(action)){
            //定时清理图片
            UploadPicManager.getInstance().cleanPic();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                AlarManagerHelper.initCleanPicAlarmManager(context);
            }
        }
    }


}
