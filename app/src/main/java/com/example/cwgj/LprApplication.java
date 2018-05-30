package com.example.cwgj;

import android.app.Application;

import com.cwgj.imgupload.utils.UploadPicManager;


/**
 * +----------------------------------------------------------------------
 * |  说明     ：
 * +----------------------------------------------------------------------
 * | 创建者   :  ldw
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/5/3 16:04
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/
public class LprApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UploadPicManager.getInstance().initConfig(this);
    }


}
