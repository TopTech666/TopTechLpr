package com.cwgj.imgupload.databases;

import com.cwgj.imgupload.bean.PicBean;

import java.util.List;

/**
 * +----------------------------------------------------------------------
 * |  说明     ：
 * +----------------------------------------------------------------------
 * | 创建者   :  ldw
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/5/9 17:21
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/
public abstract class AbLprImgDao {

    public abstract void add(PicBean bean);

    public abstract void delete(PicBean bean);

    public abstract void update(PicBean bean);

    public abstract PicBean queryForId(int id);

    public abstract List<PicBean> queryForAll();


}
