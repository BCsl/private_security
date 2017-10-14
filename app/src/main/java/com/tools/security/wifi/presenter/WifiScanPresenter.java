package com.tools.security.wifi.presenter;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.avl.engine.AVLEngine;
import com.avl.engine.AVLScanWifiListener;
import com.tools.security.R;
import com.tools.security.bean.WifiProblem;
import com.tools.security.utils.NetworkUtil;
import com.tools.security.utils.networkconnection.ConnectionClassManager;
import com.tools.security.utils.networkconnection.DeviceBandwidthSampler;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * description:
 * author: xiaodifu
 * date: 2017/1/13.
 */

public class WifiScanPresenter implements WifiScanContract.Presenter {

    private WifiScanAsyncTask wifiScanAsyncTask;
    private DownloadImage speedTestAsyncTask1;
    private DownloadImage speedTestAsyncTask2;
    private WifiScanContract.View view;
    private Context context;
    private Runnable updateItemRunnable;
    private Handler handler;
    private WifiInfo wifiInfo;
    private static final int NOT_FINISHED = -1000;
    private static final int SEND_MSG_DURATION = 1000;
    private static final int MSG_UPDATE_SCANNING_ITEM = 2;
    //下载图片最多两次，再求平均值
    private static final int MAX_SPEED_TEST_TIME = 2;
    //滚动到测速栏目时，最大等待次数
    private static final int MAX_SPEED_WAIT_TIMES=5;

    private int connectResult = NOT_FINISHED;
    private int captiveResult = NOT_FINISHED;
    private int arpResult = NOT_FINISHED;
    private int mitmResult = NOT_FINISHED;
    private int devicesResult = NOT_FINISHED;
    private int encryptionResult = NOT_FINISHED;
    private int realEncryptionResult = NOT_FINISHED;
    private int speedtestResult = NOT_FINISHED;

    private List<WifiProblem> resultList = new ArrayList<>();

    private ConnectionClassManager mConnectionClassManager;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;
    private String mURL = "http://connectionclass.parseapp.com/m100_hubble_4060.jpg";
    private int mTries = 0;
    private int index = 0;
    private double totalSpeed = 0d;
    private int speedWaitIndex=0;

    public WifiScanPresenter(WifiScanContract.View view, final Context context, final Handler handler, WifiInfo wifiInfo) {
        this.view = view;
        this.context = context;
        this.handler = handler;
        this.wifiInfo=wifiInfo;
        DataSupport.deleteAll(WifiProblem.class);
        updateItemRunnable = new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = MSG_UPDATE_SCANNING_ITEM;
                message.arg2 = index + 1;
                switch (index) {
                    case 0:
                        if (connectResult != NOT_FINISHED ) {
                            message.arg1 = connectResult;
                            index++;
                            if (connectResult != 0)
                                resultList.add(new WifiProblem(connectResult, context.getString(R.string.wi_fi_is_connected), WifiProblem.TYPE_CONNECT));
                            handler.sendMessage(message);
                            handler.postDelayed(this, SEND_MSG_DURATION);
                        } else {
                            message.arg1 = 0;
                            handler.sendMessage(message);
                            handler.postDelayed(this, SEND_MSG_DURATION);
                        }
                        break;
                    case 1:
                        if (captiveResult != NOT_FINISHED ) {
                            message.arg1 = captiveResult;
                            index++;
                            if (captiveResult != 0)
                                resultList.add(new WifiProblem(captiveResult, context.getString(R.string.no_captive_portal), WifiProblem.TYPE_CAPTIVE));
                            handler.sendMessage(message);
                            handler.postDelayed(this, SEND_MSG_DURATION);
                        } else {
                            handler.postDelayed(this, SEND_MSG_DURATION);
                        }
                        break;
                    case 2:
                        if (arpResult != NOT_FINISHED ) {
                            message.arg1 = arpResult;
                            index++;
                            if (arpResult != 0)
                                resultList.add(new WifiProblem(arpResult, context.getString(R.string.arp_detection), WifiProblem.TYPE_ARP));
                            handler.sendMessage(message);
                            handler.postDelayed(this, SEND_MSG_DURATION);
                        } else {
                            handler.postDelayed(this, SEND_MSG_DURATION);
                        }
                        break;
                    case 3:
                        if (mitmResult != NOT_FINISHED ) {
                            message.arg1 = mitmResult;
                            index++;
                            if (mitmResult != 0)
                                resultList.add(new WifiProblem(mitmResult, context.getString(R.string.mitm_attack_detection), WifiProblem.TYPE_MITM));
                            handler.sendMessage(message);
                            handler.postDelayed(this, SEND_MSG_DURATION);
                        } else {
                            handler.postDelayed(this, SEND_MSG_DURATION);
                        }
                        break;
                    case 4:
                        if (devicesResult != NOT_FINISHED ) {
                            message.arg1 = devicesResult;
                            index++;
                            if (devicesResult != 0)
                                resultList.add(new WifiProblem(devicesResult, context.getString(R.string.malicious_device_detection), WifiProblem.TYPE_DEVICE));
                            handler.sendMessage(message);
                            handler.postDelayed(this, SEND_MSG_DURATION);
                        } else {
                            handler.postDelayed(this, SEND_MSG_DURATION);
                        }
                        break;
                    case 5:
                        if (encryptionResult != NOT_FINISHED ) {
                            message.arg1 = encryptionResult;
                            index++;
                            if (encryptionResult != 0)
                                resultList.add(new WifiProblem(realEncryptionResult, context.getString(R.string.encryption_status), WifiProblem.TYPE_ENCRITION));
                            handler.sendMessage(message);
                            handler.postDelayed(this, SEND_MSG_DURATION);
                        } else {
                            handler.postDelayed(this, SEND_MSG_DURATION);
                        }
                        break;
                    case 6:
                        if (speedtestResult != NOT_FINISHED  || !NetworkUtil.isWifiConnected(context)) {
                            if (!NetworkUtil.isWifiConnected(context)) {
                                speedtestResult = -1;
                            }
                            message.arg1 = speedtestResult;
                            DataSupport.saveAll(resultList);
                            handler.sendMessage(message);
                        } else {
                            if (speedWaitIndex<MAX_SPEED_WAIT_TIMES){
                                handler.postDelayed(this, SEND_MSG_DURATION);
                            }else {
                                speedtestResult = -1;
                                message.arg1 = speedtestResult;
                                DataSupport.saveAll(resultList);
                                handler.sendMessage(message);
                            }
                            speedWaitIndex++;
                        }
                        break;
                    default:
                        handler.removeCallbacks(this);
                }
            }
        };
    }


    @Override
    public void scan() {
        Message message = new Message();
        message.what = MSG_UPDATE_SCANNING_ITEM;
        message.arg1 = 0;
        message.arg2 = 0;
        handler.sendMessage(message);
        handler.postDelayed(updateItemRunnable, SEND_MSG_DURATION);
        wifiScanAsyncTask = new WifiScanAsyncTask();
        wifiScanAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
        mConnectionClassManager = ConnectionClassManager.getInstance();
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();
        speedTestAsyncTask1 = new DownloadImage();
        speedTestAsyncTask1.execute(mURL);
    }

    @Override
    public void onDestory() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if (wifiScanAsyncTask != null) {
            if (wifiScanAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
                wifiScanAsyncTask.cancel(true);
            }
            wifiScanAsyncTask = null;
        }
        if (speedTestAsyncTask1 != null) {
            if (speedTestAsyncTask1.getStatus() != AsyncTask.Status.FINISHED) {
                speedTestAsyncTask1.cancel(true);
            }
            speedTestAsyncTask1 = null;
        }
        if (speedTestAsyncTask2 != null) {
            if (speedTestAsyncTask2.getStatus() != AsyncTask.Status.FINISHED) {
                speedTestAsyncTask2.cancel(true);
            }
            speedTestAsyncTask2 = null;
        }
        AVLEngine.stopWifiScan();
        if (mDeviceBandwidthSampler != null) {
            if (mDeviceBandwidthSampler.isSampling()) {
                mDeviceBandwidthSampler.stopSampling();
            }
            mDeviceBandwidthSampler = null;
        }
        if (mConnectionClassManager != null) {
            mConnectionClassManager = null;
        }
    }

    class WifiScanAsyncTask extends AsyncTask<Context, Void, Void> {
        @Override
        protected Void doInBackground(Context... params) {
            int wifiResult = AVLEngine.scanWifi(params[0], new AVLScanWifiListener() {
                @Override
                public void onScanStart() {
                    //0
                    Log.e("TAG","onScanStart");
                }

                @Override
                public void onWifiStateResult(boolean isWifiON,
                                              boolean isNetworkOnline,
                                              int securityType) {
                    Log.e("TAG","onWifiStateResult");
                    connectResult = isWifiON ? 0 : -1;
                    captiveResult = isNetworkOnline ? 0 : -1;
                    realEncryptionResult = securityType;
                    encryptionResult = (securityType == SECURITY_NONE ? 4 : 0);
                }

                @Override
                public void onARPResult(int i) {
                    Log.e("TAG","onARPResult:"+i);
                    arpResult = i;
                }

                @Override
                public void onMITMAttackpResult(int i) {
                    Log.e("TAG","onMITMAttackpResult:"+i);
                    mitmResult = i;
                }

                @Override
                public void onEvilDeviceResult(int i) {
                    Log.e("TAG","onEvilDeviceResult:"+i);
                    devicesResult = i;
                }

                @Override
                public void onScanFinished() {
                    Log.e("TAG","onScanFinished");
                }

                @Override
                public void onScanStop() {
                    Log.e("TAG","onScanStop");
                }
            });
            if (wifiResult!=0){
                connectResult=0;
                captiveResult=0;
                realEncryptionResult=0;
                encryptionResult=0;
                arpResult=0;
                mitmResult=0;
                devicesResult=0;
            }
            Log.e("TAG","wifi result:"+wifiResult);
            return null;
        }
    }

    /**
     * AsyncTask for handling downloading and making calls to the timer.
     */
    private class DownloadImage extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            mDeviceBandwidthSampler.startSampling();
        }

        @Override
        protected Void doInBackground(String... url) {
            String imageURL = url[0];
            try {
                // Open a stream to download the image from our URL.
                URLConnection connection = new URL(imageURL).openConnection();
                connection.setUseCaches(false);
                connection.connect();
                InputStream input = connection.getInputStream();
                try {
                    byte[] buffer = new byte[1024];

                    // Do some busy waiting while the stream is open.
                    while (input.read(buffer) != -1) {
                    }
                } finally {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("TAG", "Error while downloading image.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            mDeviceBandwidthSampler.stopSampling();
            // Retry for up to 10 times until we find a ConnectionClass.
            if (mTries < MAX_SPEED_TEST_TIME) {
                mTries++;
                totalSpeed = totalSpeed + mConnectionClassManager.getDownloadKBitsPerSecond();
                speedTestAsyncTask2 = new DownloadImage();
                speedTestAsyncTask2.execute(mURL);
            } else {
                speedtestResult = (int) (totalSpeed / MAX_SPEED_TEST_TIME);
                Log.e("TAG", "speedtestResult=" + speedtestResult);
            }
        }
    }
}
