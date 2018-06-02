package com.cwgj.imgupload.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cwgj.imgupload.bean.PicBean;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * +----------------------------------------------------------------------
 * |  说明     ：
 * +----------------------------------------------------------------------
 * | 创建者   :  ldw
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/5/9 17:23
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/
public class DatabaseHelp extends OrmLiteSqliteOpenHelper{

    private static final String DB_NAME="sqlite-test.db";

    private static final int DB_VERSION = 1;

    private static DatabaseHelp instance;

    private DatabaseHelp(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    public static synchronized DatabaseHelp getHelp(Context context){
        if(instance == null){
            synchronized (DatabaseHelp.class){
                if(instance == null){
                    instance = new DatabaseHelp(context);
                }
            }
        }
        return instance;
    }

    @Override
    public void close() {
        super.close();
        imgDao = null;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource,PicBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            TableUtils.dropTable(connectionSource,PicBean.class,true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------------------------------------------------
    //userDao 每一张表对应一个
    private Dao<PicBean, Integer> imgDao;

    public Dao<PicBean, Integer> getLprImgDao() throws SQLException{
        if(imgDao == null){
            imgDao = getDao(PicBean.class);
        }
        return imgDao;
    }




}
