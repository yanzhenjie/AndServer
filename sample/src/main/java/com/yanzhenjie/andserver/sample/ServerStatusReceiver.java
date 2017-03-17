package com.yanzhenjie.andserver.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by Yan Zhenjie on 2017/3/17.
 */
public class ServerStatusReceiver extends BroadcastReceiver {

    private static final String ACTION = "com.yanzhenjie.andserver.receiver";

    private static final String CMD_KEY = "CMD_KEY";

    private static final int CMD_VALUE_START = 1;
    private static final int CMD_VALUE_STARTED = 2;
    private static final int CMD_VALUE_STOP = 3;

    /**
     * Notify serverStart.
     *
     * @param context context.
     */
    public static void serverStart(Context context) {
        sendBroadcast(context, CMD_VALUE_START);
    }

    /**
     * Notify serverHasStarted.
     *
     * @param context context.
     */
    public static void serverHasStarted(Context context) {
        sendBroadcast(context, CMD_VALUE_STARTED);
    }

    /**
     * Notify serverStop.
     *
     * @param context context.
     */
    public static void serverStop(Context context) {
        sendBroadcast(context, CMD_VALUE_STOP);
    }

    private static void sendBroadcast(Context context, int cmd) {
        Intent broadcast = new Intent(ACTION);
        broadcast.putExtra(CMD_KEY, cmd);
        context.sendBroadcast(broadcast);
    }

    private MainActivity mActivity;

    public ServerStatusReceiver(MainActivity mMainActivity) {
        this.mActivity = mMainActivity;
    }

    /**
     * Register broadcast.
     */
    public void register() {
        IntentFilter filter = new IntentFilter(ACTION);
        mActivity.registerReceiver(this, filter);
    }

    /**
     * UnRegister broadcast.
     */
    public void unRegister() {
        mActivity.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION.equals(action)) {
            int cmd = intent.getIntExtra(CMD_KEY, 0);
            switch (cmd) {
                case CMD_VALUE_START: {
                    mActivity.serverStart();
                    break;
                }
                case CMD_VALUE_STARTED: {
                    mActivity.serverHasStarted();
                    break;
                }
                case CMD_VALUE_STOP: {
                    mActivity.serverStop();
                    break;
                }
            }
        }
    }

}
