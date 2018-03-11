/*
 * Copyright © 2017 Yan Zhenjie.
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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;
import com.yanzhenjie.andserver.filter.HttpCacheFilter;
import com.yanzhenjie.andserver.sample.handler.FileHandler;
import com.yanzhenjie.andserver.sample.handler.ImageHandler;
import com.yanzhenjie.andserver.sample.handler.LoginHandler;
import com.yanzhenjie.andserver.sample.handler.UploadHandler;
import com.yanzhenjie.andserver.sample.util.NetUtils;
import com.yanzhenjie.andserver.website.AssetsWebsite;

import java.util.concurrent.TimeUnit;

/**
 * <p>Server service.</p>
 * Created by Yan Zhenjie on 2017/3/16.
 */
public class CoreService extends Service {

    /**
     * AndServer.
     */
    private Server mServer;

    @Override
    public void onCreate() {
        // More usage documentation: http://yanzhenjie.github.io/AndServer
        mServer = AndServer.serverBuilder()
                .inetAddress(NetUtils.getLocalIPAddress()) // Bind IP address.
                .port(8080)
                .timeout(10, TimeUnit.SECONDS)
                .website(new AssetsWebsite(getAssets(), "web"))
                .registerHandler("/download", new FileHandler())
                .registerHandler("/login", new LoginHandler())
                .registerHandler("/upload", new UploadHandler())
                .registerHandler("/image", new ImageHandler())
                .filter(new HttpCacheFilter())
                .listener(mListener)
                .build();
    }

    /**
     * Server listener.
     */
    private Server.ServerListener mListener = new Server.ServerListener() {
        @Override
        public void onStarted() {
            String hostAddress = mServer.getInetAddress().getHostAddress();
            ServerManager.serverStart(CoreService.this, hostAddress);
        }

        @Override
        public void onStopped() {
            ServerManager.serverStop(CoreService.this);
        }

        @Override
        public void onError(Exception e) {
            ServerManager.serverError(CoreService.this, e.getMessage());
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopServer(); // Stop server.
    }

    /**
     * Start server.
     */
    private void startServer() {
        if (mServer != null) {
            if (mServer.isRunning()) {
                String hostAddress = mServer.getInetAddress().getHostAddress();
                ServerManager.serverStart(CoreService.this, hostAddress);
            } else {
                mServer.startup();
            }
        }
    }

    /**
     * Stop server.
     */
    private void stopServer() {
        if (mServer != null && mServer.isRunning()) {
            mServer.shutdown();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
