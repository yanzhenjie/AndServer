/*
 * Copyright © Yan Zhenjie. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.andserver.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.loading.dialog.LoadingDialog;
import com.yanzhenjie.nohttp.tools.NetUtil;

/**
 * Created by Yan Zhenjie on 2016/6/13.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent mService;
    /**
     * Accept and server status.
     */
    private ServerStatusReceiver mReceiver;

    /**
     * Show message
     */
    private TextView mTvMessage;

    private LoadingDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);

        mTvMessage = (TextView) findViewById(R.id.tv_message);

        // AndServer run in the service.
        mService = new Intent(this, CoreService.class);
        mReceiver = new ServerStatusReceiver(this);
        mReceiver.register();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReceiver.unRegister();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_start: {
                showDialog();
                startService(mService);
                break;
            }
            case R.id.btn_stop: {
                stopService(mService);
            }
        }
    }

    /**
     * Start notify.
     */
    public void serverStart() {
        closeDialog();
        String message = getString(R.string.server_start_succeed);

        String ip = NetUtil.getLocalIPAddress();
        if (!TextUtils.isEmpty(ip)) {
            message += ("\nhttp://" + ip + ":8080/\n"
                    + "http://" + ip + ":8080/login\n"
                    + "http://" + ip + ":8080/upload\n"
                    + "http://" + ip + ":8080/web/index.html\n"
                    + "http://" + ip + ":8080/web/error.html\n"
                    + "http://" + ip + ":8080/web/login.html\n"
                    + "http://" + ip + ":8080/web/image/image.jpg");
        }
        mTvMessage.setText(message);
    }

    /**
     * Started notify.
     */
    public void serverHasStarted() {
        closeDialog();
        Toast.makeText(this, R.string.server_started, Toast.LENGTH_SHORT).show();
    }

    /**
     * Stop notify.
     */
    public void serverStop() {
        closeDialog();
        mTvMessage.setText(R.string.server_stop_succeed);
    }

    private void showDialog() {
        if (mDialog == null)
            mDialog = new LoadingDialog(this);
        if (!mDialog.isShowing()) mDialog.show();
    }

    private void closeDialog() {
        if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
    }


}
