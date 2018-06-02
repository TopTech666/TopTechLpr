package com.cwgj.imgupload.oss;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

/**
 * +----------------------------------------------------------------------
 * |  说明     ：
 * +----------------------------------------------------------------------
 * | 创建者   :  kim_tony
 * +----------------------------------------------------------------------
 * | 时　　间 ：2018/1/24 19:02
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/

public class OSSUploadHelper {

    String TAG = "OSSUploadHelper";

    private OSSClient mOSSClient;

    private static OSSUploadHelper uploadHelper ;

    private Context mContext;

    //oss配置参数
    private OSSConfigParam mOSSConfigParam;

    private boolean bResetOSSparam = false;

    private String getEndpoint(){
        return String.format("http://%s.aliyuncs.com", mOSSConfigParam.region);
    }

    public void initConfig(Context context, OSSConfigParam param){
        this.mContext = context;
        this.mOSSConfigParam  = param;
    }


    /**
     * 重置oss , 重新获取key ，secret 参数
     * @param param
     */
    public synchronized void resetOSSClient(OSSConfigParam param){
//        this.mOSSClient = null;
        bResetOSSparam = true;
        this.mOSSConfigParam = param;
    }



    private OSSUploadHelper() {
    }

    public static OSSUploadHelper getInstance() {
        if(uploadHelper == null){
            synchronized (OSSUploadHelper.class){
                if(uploadHelper == null){
                    uploadHelper = new OSSUploadHelper();
                }
            }
        }
        return uploadHelper;
    }

    public synchronized OSSClient getOSSClient() {
        if(mOSSConfigParam == null)
            return null;
        if (mOSSClient == null || bResetOSSparam) {
            //重新赋值key
            bResetOSSparam = false;
            OSSLog.enableLog();  //调用此方法即可开启日志
            // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考访问控制章节
// 也可查看sample 中 sts 使用方式了解更多(https://github.com/aliyun/aliyun-oss-android-sdk/tree/master/app/src/main/java/com/alibaba/sdk/android/oss/app)
            OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(mOSSConfigParam.accessKeyId, mOSSConfigParam.accessKeySecret, mOSSConfigParam.stsToken);
//该配置类如果不设置，会有默认配置，具体可看该类
            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectionTimeout(300 * 1000); // 连接超时，默认300秒
            conf.setSocketTimeout(300 * 1000); // socket超时，默认300秒
            conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
            conf.setMaxErrorRetry(5); // 失败后最大重试次数，默认5次
            return mOSSClient = new OSSClient(mContext, getEndpoint(), credentialProvider);
            }
        return mOSSClient;
    }


    /****************************************
     方法描述：同步上传图片
     @param
     @return
     ****************************************/
    public boolean upLoadSy(String objectKey, String picPath) {
        if(mOSSConfigParam ==null)
            return false;
        PutObjectRequest put = new PutObjectRequest(mOSSConfigParam.bucket, objectKey, picPath);
        // 文件元信息的设置是可选的
        // ObjectMetadata metadata = new ObjectMetadata();
        // metadata.setContentType("application/octet-stream"); // 设置content-type
        // metadata.setContentMD5(BinaryUtil.calculateBase64Md5(uploadFilePath)); // 校验MD5
        // put.setMetadata(metadata);
        try {
            PutObjectResult putResult = getOSSClient().putObject(put);

            Log.d(TAG, "UploadSuccess");
            Log.d(TAG, "ETag=" + putResult.getETag());
            Log.d(TAG, "RequestId=" + putResult.getRequestId());
            return true;
        } catch (ClientException e) {
            // 本地异常如网络异常等
            e.printStackTrace();
            Log.e(TAG, "ClientException=" + e.getCause() + "" + e.getMessage());
        } catch (ServiceException e) {
            // 服务异常
            Log.e(TAG, "RequestId=" + e.getRequestId());
            Log.e(TAG, "ErrorCode=" + e.getErrorCode());
            Log.e(TAG, "HostId=" + e.getHostId());
            Log.e(TAG, "RawMessage=" + e.getRawMessage());
        } catch (Exception e) {
        }
        return false;

    }

    /****************************************
     方法描述：异步上传图片
     @param  objectKey 服务器上的保存key
     @param  picPath 图片路径
     @param  callBack 回调函数
     @return
     ****************************************/
    public void upLoadAn(String objectKey, String picPath, final UploadCallBack callBack) {
        if(mOSSConfigParam ==null)
            return;
        PutObjectRequest put = new PutObjectRequest(mOSSConfigParam.bucket, objectKey, picPath);
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d(TAG, "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });
        try {
            OSSAsyncTask task = getOSSClient().asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    Log.d(TAG, "UploadSuccess");
                    Log.d(TAG, "ETag=" + result.getETag());
                    Log.d(TAG, "RequestId=" + result.getRequestId());
                    callBack.onSuccess();
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {

                    // 请求异常
                    if (clientExcepion != null) {
                        // 本地异常如网络异常等
                        clientExcepion.printStackTrace();
                        callBack.onFail(clientExcepion.getMessage());

                    }
                    if (serviceException != null) {
                        callBack.onFail(serviceException.getRawMessage());

                        // 服务异常
                        Log.e(TAG, "ErrorCode=" + serviceException.getErrorCode());
                        Log.e(TAG, "RequestId=" + serviceException.getRequestId());
                        Log.e(TAG, "HostId=" + serviceException.getHostId());
                        Log.e(TAG, "RawMessage=" + serviceException.getRawMessage());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //上传图片的回调
    public interface UploadCallBack {

        void onSuccess();

        void onFail(String msg);

    }

}
