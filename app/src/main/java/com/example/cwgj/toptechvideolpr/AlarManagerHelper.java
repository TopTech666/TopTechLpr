package com.example.cwgj.toptechvideolpr;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.cwgj.AlarManagerReceiver;

/**
 * +----------------------------------------------------------------------
 * |  说明     ：
 * +----------------------------------------------------------------------
 * | 创建者   :  zzh
 * +----------------------------------------------------------------------
 *   时　　间 ：2017/11/8 10:48
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
*/
public class AlarManagerHelper {

    private static String TAG = "AlarManagerHelper";

    private static final int ALARM_REPEAT_TIME_ONE_MINITES = 10 * 1000;//10s提醒一次

    private static final int ALARM_REPEAT_TIME_FIVE_MINITES = 2 * 60 * 1000;//5min提醒一次


    /***************************************************
     * 方法描述 ：定时获取相机连接状态
     * 方法名  :  initNotifyAlarmManager
     **************************************************/
    public static  void initCameraConnAlarmManager(Context context) {
        Intent intent = new Intent();
        intent.setAction(AlarManagerReceiver.ACTION_ALARMANAGER_CAMERA_CONNEC_NOTIFY);
        PendingIntent sendIntent = PendingIntent.getBroadcast(context, AlarManagerReceiver.REQUEST_CODE_CAMERA_CONNEC_NOTIFY, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC, System.currentTimeMillis()+ALARM_REPEAT_TIME_ONE_MINITES,sendIntent);
        }else {
            am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), ALARM_REPEAT_TIME_ONE_MINITES, sendIntent);
        }
    }

    /***************************************************
     * 方法描述 ：5min定时上传没有上传成功的图片
     * 方法名  :  initOSSAlarmManager
     **************************************************/
    public static  void initOSSAlarmManager(Context context) {
        Intent intent = new Intent();
        intent.setAction(AlarManagerReceiver.ACTION_ALARMANAGER_OSS_NOTIFY);
        PendingIntent sendIntent = PendingIntent.getBroadcast(context, AlarManagerReceiver.REQUEST_CODE_OSS_NOTIFY, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC, System.currentTimeMillis()+ALARM_REPEAT_TIME_FIVE_MINITES,sendIntent);
        }else {
            am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), ALARM_REPEAT_TIME_FIVE_MINITES, sendIntent);
        }
    }

    /**
     * 取消OSS定时监听
     * @param context
     */
    public static void cancleOSSAlarmManager(Context context) {
        cancleAlarmManager(context, AlarManagerReceiver.ACTION_ALARMANAGER_OSS_NOTIFY,AlarManagerReceiver.REQUEST_CODE_OSS_NOTIFY);
    }

    /**
     * 取消相机定时监听
     * @param context
     */
    public static void cancleCameraConnAlarmManager(Context context) {
        cancleAlarmManager(context, AlarManagerReceiver.ACTION_ALARMANAGER_CAMERA_CONNEC_NOTIFY,AlarManagerReceiver.REQUEST_CODE_CAMERA_CONNEC_NOTIFY);
    }

    /**
     * 取消定时监听
     * @param context
     * @param action
     * @param request
     */
    private static void cancleAlarmManager(Context context, String action , int request ) {
        Intent intent = new Intent();
        intent.setAction(action);
        PendingIntent sendIntent = PendingIntent.getBroadcast(context, request, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sendIntent);
    }




}
