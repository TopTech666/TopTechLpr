package com.example.cwgj.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.cwgj.toptechvideolpr.MainActivity;

/**
 * +----------------------------------------------------------------------
 * |  说明     ： 接收开机广播，自启动App
 * +----------------------------------------------------------------------
 * | 创建者   :  ldw
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/5/23 15:21
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/
public class BootBroadcastReceiver extends BroadcastReceiver {

    public static final String START_SELF_APP =  "com.cwgj.start.aipark_app";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(Intent.ACTION_BOOT_COMPLETED.equals(action)
                || START_SELF_APP.equals(action)
                || Intent.ACTION_INSTALL_PACKAGE.equals(action)
                || Intent.ACTION_PACKAGE_ADDED.equals(action)
                || Intent.ACTION_PACKAGE_REPLACED.equals(action)){
            Toast.makeText(context, "收到开机广播", Toast.LENGTH_SHORT).show();
            Intent boot =  new Intent(context, MainActivity.class);
            boot.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(boot);
        }
    }
}
