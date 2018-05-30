package com.cwgj.imgupload.oss;

/**
 * +----------------------------------------------------------------------
 * |  说明     ：
 * +----------------------------------------------------------------------
 * | 创建者   :  ldw
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/5/10 10:55
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/
public class OSSConfigParam {

    public String accessKeyId ;
    public String accessKeySecret;
    public String stsToken;
    public String region;
    public String bucket;

    public OSSConfigParam(String accessKeyId, String accessKeySecret, String stsToken, String region, String bucket) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.stsToken = stsToken;
        this.region = region;
        this.bucket = bucket;
    }





}
