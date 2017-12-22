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
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yanzhenjie.loading.dialog.LoadingDialog;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Yan Zhenjie on 2016/6/13.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ServerManager mServerManager;
    private TextView mTvMessage;

    private LoadingDialog mDialog;
    private List<String> mAddressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_browse).setOnClickListener(this);

        mTvMessage = (TextView) findViewById(R.id.tv_message);

        // AndServer run in the service.
        mServerManager = new ServerManager(this);
        mServerManager.register();

        // startServer;
        findViewById(R.id.btn_start).performClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mServerManager.unRegister();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_start: {
                showDialog();
                mServerManager.startService();
                break;
            }
            case R.id.btn_stop: {
                mServerManager.stopService();
                break;
            }
            case R.id.btn_browse: {
                if (mAddressList != null) {
                    String address = mAddressList.get(1);
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(address);
                    intent.setData(content_url);
                    startActivity(intent);
                }
                break;
            }
        }
    }

    /**
     * Start notify.
     */
    public void serverStart(String ip) {
        closeDialog();
        if (!TextUtils.isEmpty(ip)) {
            mAddressList = new LinkedList<>();
            mAddressList.add(getString(R.string.server_start_succeed));
            mAddressList.add("http://" + ip + ":8080/");
            mAddressList.add("http://" + ip + ":8080/login.html");
            mAddressList.add("http://" + ip + ":8080/image");
            mAddressList.add("http://" + ip + ":8080/download");
            mAddressList.add("http://" + ip + ":8080/upload");
        }
        mTvMessage.setText(TextUtils.join(",\n", mAddressList));
    }

    /**
     * Error notify.
     */
    public void serverError(String message) {
        closeDialog();
        mTvMessage.setText(message);
    }

    /**
     * Started notify.
     */
    public void serverHasStarted(String ip) {
        serverStart(ip);
    }

    /**
     * Stop notify.
     */
    public void serverStop() {
        closeDialog();
        mAddressList = null;
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
