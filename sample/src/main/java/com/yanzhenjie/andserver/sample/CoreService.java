/*
 * Copyright (C) 2018 Zhenjie Yan
 *               2022 ISNing
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

import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.AsyncServer;
import com.yanzhenjie.andserver.delegate.IOReactorConfigDelegate;
import com.yanzhenjie.andserver.http.URIScheme;
import com.yanzhenjie.andserver.sample.util.NetUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Zhenjie Yan on 2018/6/9.
 */
public class CoreService extends Service {
    private static final String TAG = "CoreService";

    private AsyncServer mServer;

    @Override
    public void onCreate() {
        InetAddress address = NetUtils.getLocalIPAddress();
        String hostAddress = address == null ? null : address.getHostAddress();
        mServer = AndServer.webServerAsync(this)
                .setCanonicalHostName(hostAddress)
                .setIOReactorConfig(IOReactorConfigDelegate.custom()
                        .setSoReuseAddress(true)
                        .setTcpNoDelay(true)
                        .build())
                .setListener(new AsyncServer.ServerListener() {
                    @Override
                    public void onStarted() {
                        Future<?> future = mServer.listen(new InetSocketAddress(8080), URIScheme.HTTP);
                        try {
                            future.get();
                        } catch (ExecutionException | InterruptedException e) {
                            stopServer();
                            ServerManager.onServerError(CoreService.this, e.getMessage());
                        }
                        ServerManager.onServerStart(CoreService.this, hostAddress);
                    }

                    @Override
                    public void onStopped() {
                        ServerManager.onServerStop(CoreService.this);
                    }

                    @Override
                    public void onException(Exception e) {
                        e.printStackTrace();
                    }
                })
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopServer();
        super.onDestroy();
    }

    /**
     * Start server.
     */
    private void startServer() {
        mServer.startup();
    }

    /**
     * Stop server.
     */
    private void stopServer() {
        mServer.shutdown();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}