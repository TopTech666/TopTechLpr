package com.cwgj.basiclib;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;

/**
 * +----------------------------------------------------------------------
 * |  说明     ：规定时间内连续按键 N 次
 * +----------------------------------------------------------------------
 * | 创建者   :  ldw
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/5/26 16:55
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/
public abstract class  MultiOnClickListener implements View.OnClickListener {
//
    final static int COUNTS = 5;
    final static long DURATION = 5 * 1000;

    private int counts ; //点击次数
    private long duration; //规定有效时间
    private long[] mHits;


    public MultiOnClickListener(){
        this(COUNTS, DURATION);
    }

    public MultiOnClickListener(int counts, long duration){
        this.counts = counts;
        this.duration = duration;
        mHits = new long[counts];
    }

    @Override
    public void onClick(View v) {
        /**
         * 实现双击方法
         * src 拷贝的源数组
         * srcPos 从源数组的那个位置开始拷贝.
         * dst 目标数组
         * dstPos 从目标数组的那个位子开始写数据
         * length 拷贝的元素的个数
         */
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        //实现左移，然后最后一个位置更新距离开机的时间，如果最后一个时间和最开始时间小于DURATION，即连续5次点击
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - duration)) {
            String tips = "您已在[" + duration + "]ms内连续点击【" + mHits.length + "】次了！！！";
            //有效按键后清空数组
            mHits = new long[counts];
            Log.d("1111111111", tips);
            multiOnClick();
        }
    }

    //连击有效回调
    public abstract void multiOnClick();


}
