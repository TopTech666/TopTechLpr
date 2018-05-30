package com.cwgj.imgupload.databases;

import android.content.Context;
import android.util.Log;

import com.cwgj.imgupload.bean.PicBean;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * +----------------------------------------------------------------------
 * |  说明     ：
 * +----------------------------------------------------------------------
 * | 创建者   :  ldw
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/5/9 17:31
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/
public class PicDao extends AbLprImgDao {

    private static final String TAG = "PicDao";

    private Dao<PicBean, Integer> lprImgDao;

    private DatabaseHelp dbHelp;

    public PicDao(Context context){
        dbHelp = DatabaseHelp.getHelp(context);
        try {
            lprImgDao = dbHelp.getLprImgDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public  synchronized void  add(PicBean bean) {
        try {
            lprImgDao.create(bean);
            Log.d(TAG, "add succ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void delete(PicBean bean) {
        try {
            lprImgDao.delete(bean);
            Log.d(TAG, "delete succ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void update(PicBean bean) {
        try {
            lprImgDao.update(bean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized PicBean queryForId(int id) {
        PicBean bean = null;
        try {
            bean = lprImgDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Override
    public synchronized List<PicBean> queryForAll() {
        List<PicBean> list = null;
        try {
            list = lprImgDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


}
