package com.cwgj.imgupload.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cwgj.imgupload.bean.PicBean;
import com.cwgj.imgupload.databases.PicDao;
import com.cwgj.imgupload.oss.OSSUploadHelper;
import com.cwgj.imgupload.service.CleanPicService;
import com.cwgj.imgupload.service.UploadPicService;

import java.util.List;

/**
 * +----------------------------------------------------------------------
 * |  说明     ： 图片上传管理类
 * +----------------------------------------------------------------------
 * | 创建者   :  ldw
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/5/30 14:09
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/
public class UploadPicManager {

    private static UploadPicManager sUploadPicManager;

    private Context mContext;

    private PicDao mPicDao;

    private UploadPicManager(){
    }

    public static UploadPicManager getInstance(){
        if(sUploadPicManager == null){
            synchronized (UploadPicManager.class){
                if(sUploadPicManager == null){
                    sUploadPicManager = new UploadPicManager();
                }
            }
        }
        return sUploadPicManager;
    }


    public void initConfig(Context context){
        this.mContext = context;
        this.mPicDao = new PicDao(context);

    }


    /**
     *  开启异步图片上传
     */
    public void uploadPicsAsynSilently() {
        //首先stop存在的service,否则图片list 操作可能混乱
        mContext.stopService(new Intent(mContext, UploadPicService.class));
        Intent intent = new Intent(mContext, UploadPicService.class);
        mContext.startService(intent);
    }


    /**
     * 上传单张图片
     * @param bean
     */
    public void uploadSinglePic(final PicBean bean){

        final String str = String.format("saas/10010/20180530/inpark/%s.jpg", System.currentTimeMillis());

        OSSUploadHelper.getInstance().upLoadAn("", bean.getPicPath(), new OSSUploadHelper.UploadCallBack() {
            @Override
            public void onSuccess() {
                Log.d("xxxxxxxxx", "onSuccess: 单张图片上传成功" + str + bean.getPicPath());

            }

            @Override
            public void onFail(String msg) {
                Log.d("xxxxxxxxx", "onFail: 单张图片上传失败" + msg + bean.getPicPath());
                //单张上传失败把他加入到数据库,等待队列上传
                addPic(bean);
            }
        });
    }

    public  synchronized  List<PicBean> queryAllPic(){
        return mPicDao.queryForAll();
    }

    public  synchronized void addPic(PicBean bean){
        mPicDao.add(bean);
    }

    public synchronized void deletePic(PicBean bean){
        mPicDao.delete(bean);
    }


    //清理多余的图片
    public void cleanPic(){
        //当sd卡可用的容量少于20% 开始按时间顺序删除图片
        if(FileUtils.getAvailablePercent() < 0.9){
            mContext.stopService(new Intent(mContext, CleanPicService.class));
            mContext.startService(new Intent(mContext, CleanPicService.class));
        }
    }

}
