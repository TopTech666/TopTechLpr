package com.example.cwgj.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * +----------------------------------------------------------------------
 * |  说明     ：app升级服务
 * +----------------------------------------------------------------------
 * | 创建者   :  kim_tony
 * +----------------------------------------------------------------------
 * | 时　　间 ：2017/9/9 13:08
 * +----------------------------------------------------------------------
 * | 版权所有: 北京市车位管家科技有限公司
 * +----------------------------------------------------------------------
 **/

public class AppUpdateService extends IntentService {
    private static final String ACTION_UPDATE = "com.hundun.yanxishe.service.action.update";
    private static final String EXTRA_URL = "com.hundun.yanxishe.service.extra.url";
    private static final String EXTRA_FILE_NAME = "com.hundun.yanxishe.service.extra.file.name";
    private boolean isRunning = false;
    private static OnProgressListener mProgressListener;

    public interface OnProgressListener {

        void onProgress(int progress);

        void onSuccess(boolean isSuccess);
    }

    public AppUpdateService() {
        super("AppUpdateService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE.equals(action)) {
                final String url = intent.getStringExtra(EXTRA_URL);
                final String fileName = intent.getStringExtra(EXTRA_FILE_NAME);
                startDownLoad(url, fileName);
            }
        }
    }

    @Override
    public void onDestroy() {
        mProgressListener = null;
        super.onDestroy();
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startUpdate(Context context, String url, String fileName, OnProgressListener progressListener) {
        mProgressListener = progressListener;
        Intent intent = new Intent(context, AppUpdateService.class);
        intent.setAction(ACTION_UPDATE);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_FILE_NAME, fileName);
        context.startService(intent);
    }

    private void startDownLoad(String url, String fileName) {
        if (isRunning) {
            return;
        }
        isRunning = true;
        try {
            boolean isSuccess = downloadUpdateFile(url, fileName);
            if (mProgressListener != null) {
                mProgressListener.onSuccess(isSuccess);
            }
            if (isSuccess) {
                installApk(new File(fileName));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 安装apk文件
     *
     * @param apkFile 安装包所在目录
     */
    private void installApk(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            String[] command = {"chmod", "777", apkFile.toString()};
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (IOException ignored) {
        }
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 下载文件
     *
     * @param downloadUrl
     * @param filepath
     * @return
     * @throws Exception
     */
    private boolean downloadUpdateFile(String downloadUrl, String filepath) {
        try {
            int currentSize = 0;
            long totalSize = 0;
            int updateTotalSize = 0;
            boolean result = false;
            HttpURLConnection httpConnection = null;
            InputStream is = null;
            FileOutputStream fos = null;
            File temp = new File(filepath + ".tmp");
            if (temp.getParentFile().isDirectory()) {
                temp.getParentFile().mkdirs();
            }
            try {
                URL url = new URL(downloadUrl);
                httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
                if (currentSize > 0) {
                    httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
                }
                httpConnection.setConnectTimeout(20000);
                httpConnection.setReadTimeout(120000);
                updateTotalSize = httpConnection.getContentLength();
                if (httpConnection.getResponseCode() == 404) {
                    throw new Exception("fail!");
                }
                is = httpConnection.getInputStream();
                fos = new FileOutputStream(temp, false);
                byte buffer[] = new byte[4096];
                int readsize = 0;
                while ((readsize = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, readsize);
                    totalSize += readsize;
                }
                temp.renameTo(new File(filepath));
                temp.delete();
            } finally {
                if (httpConnection != null) {
                    httpConnection.disconnect();
                }
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
                result = updateTotalSize > 0 && updateTotalSize == totalSize;
                if (!result) { //下载失败或者为下载完成
                    new File(filepath).delete();
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
