package com.tools.security.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.orhanobut.logger.Logger;
import com.tools.security.R;
import com.tools.security.common.AppConstants;
import com.tools.security.utils.ToastUtil;

import java.util.List;

/**
 * description:辅助功能Service
 * author: xiaodifu
 * date: 2017/1/7.
 */

public class MyAccessibilityService extends AccessibilityService {

    public static int INVOKE_TYPE = 0;
    public static final int TYPE_KILL_APP = 1;
    public static final int TYPE_INSTALL_APP = 2;
    public static final int TYPE_UNINSTALL_APP = 3;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        this.processAccessibilityEnvent(event);
    }

    public static void reset(){
        INVOKE_TYPE = 0;
    }

    private void processAccessibilityEnvent(AccessibilityEvent event) {

        Logger.d("test", event.eventTypeToString(event.getEventType()));
        if (event.getSource() == null) {
            Logger.d("test", "the source = null");
        } else {
            Logger.d("test", "event = " + event.toString());
            switch (INVOKE_TYPE) {
                case TYPE_KILL_APP:
                    processKillApplication(event);
                    break;
                case TYPE_INSTALL_APP:
                    processinstallApplication(event);
                    break;
                case TYPE_UNINSTALL_APP:
                    processUninstallApplication(event);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 自动安装
     * @param event
     */
    private void processinstallApplication(AccessibilityEvent event) {

        if (event.getSource() != null) {
            if (event.getPackageName().equals("com.android.packageinstaller")) {
                List<AccessibilityNodeInfo> unintall_nodes = event.getSource().findAccessibilityNodeInfosByText("安装");
                if (unintall_nodes!=null && !unintall_nodes.isEmpty()) {
                    AccessibilityNodeInfo node;
                    for(int i=0; i<unintall_nodes.size(); i++){
                        node = unintall_nodes.get(i);
                        if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }

                List<AccessibilityNodeInfo> next_nodes = event.getSource().findAccessibilityNodeInfosByText("下一步");
                if (next_nodes!=null && !next_nodes.isEmpty()) {
                    AccessibilityNodeInfo node;
                    for(int i=0; i<next_nodes.size(); i++){
                        node = next_nodes.get(i);
                        if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }

                List<AccessibilityNodeInfo> ok_nodes = event.getSource().findAccessibilityNodeInfosByText("打开");
                if (ok_nodes!=null && !ok_nodes.isEmpty()) {
                    AccessibilityNodeInfo node;
                    for(int i=0; i<ok_nodes.size(); i++){
                        node = ok_nodes.get(i);
                        if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }

    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        sendBroadcast(new Intent(AppConstants.ACCESSIBILITY_SERVICE_CONNECTED));
    }

    @Override
    public void onInterrupt() {
        Logger.e("TAG", "MyAccessibilityService:onInterrupt");
    }

    /**
     * 强行停止
     * @param event
     */
    private void processKillApplication(AccessibilityEvent event) {

        if (event.getSource() != null) {
            if (event.getPackageName().equals("com.android.settings")) {
                List<AccessibilityNodeInfo> stop_nodes = event.getSource().findAccessibilityNodeInfosByText(getString(R.string.force_stop));
                if (stop_nodes!=null && !stop_nodes.isEmpty()) {
                    AccessibilityNodeInfo node;
                    for(int i=0; i<stop_nodes.size(); i++){
                        node = stop_nodes.get(i);
                        if (node.getClassName().equals("android.widget.Button")) {
                            if(node.isEnabled()){
                                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            }
                        }
                        node.recycle();
                    }
                }

                List<AccessibilityNodeInfo> ok_nodes = event.getSource().findAccessibilityNodeInfosByText(getString(R.string.force_stop_OK));
                if (ok_nodes!=null && !ok_nodes.isEmpty()) {
                    AccessibilityNodeInfo node;
                    for(int i=0; i<ok_nodes.size(); i++){
                        node = ok_nodes.get(i);
                        if (node.getClassName().equals("android.widget.Button")) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            Logger.d("action", "click ok");
                        }
                        node.recycle();
                    }
                }
//                AccessibilityNodeInfo.(GLOBAL_ACTION_BACK);
            }
        }
    }

    /**
     * 卸载
     * @param event
     */
    private void processUninstallApplication(AccessibilityEvent event) {

        if (event.getSource() != null) {
            if (event.getPackageName().equals("com.android.packageinstaller")) {
                List<AccessibilityNodeInfo> ok_nodes = event.getSource().findAccessibilityNodeInfosByText("确定");
                if (ok_nodes!=null && !ok_nodes.isEmpty()) {
                    AccessibilityNodeInfo node;
                    for(int i=0; i<ok_nodes.size(); i++){
                        node = ok_nodes.get(i);
                        if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }

                }
            }
        }
    }

    private void simulationClick(AccessibilityEvent event, String text){
        List<AccessibilityNodeInfo> nodeInfoList = event.getSource().findAccessibilityNodeInfosByText(text);
        for (AccessibilityNodeInfo node : nodeInfoList) {
            if (node.isClickable() && node.isEnabled()) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

}
