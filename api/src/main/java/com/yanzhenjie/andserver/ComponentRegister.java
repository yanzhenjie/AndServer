/*
 * Copyright 2018 Yan Zhenjie
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
package com.yanzhenjie.andserver;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.Log;

import com.yanzhenjie.andserver.register.OnRegister;
import com.yanzhenjie.andserver.register.Register;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dalvik.system.DexFile;

/**
 * <p> Load all the components in the dex file. </p>
 *
 * Created by YanZhenjie on 2018/9/23.
 */
public class ComponentRegister {

    private static final String COMPONENT_PACKAGE_NAME = "com.yanzhenjie.andserver.register";
    private static final String COMPONENT_INTERFACE_NAME = OnRegister.class.getName();

    private static final String CODE_CACHE_SECONDARY_DIRECTORY = "code_cache/secondary-dexes";
    private static final String EXTRACTED_NAME_EXT = ".classes";
    private static final String EXTRACTED_SUFFIX = ".zip";

    private static final String PREFS_FILE = "multidex.version";
    private static final String KEY_DEX_NUMBER = "dex.number";

    private Context mContext;

    public ComponentRegister(Context context) {
        this.mContext = context;
    }

    public void register(Register register, String group) {
        List<String> paths = getDexFilePaths(mContext);

        for (final String path : paths) {
            DexFile dexfile = null;

            try {
                if (path.endsWith(EXTRACTED_SUFFIX)) {
                    dexfile = DexFile.loadDex(path, path + ".tmp", 0);
                } else {
                    dexfile = new DexFile(path);
                }

                Enumeration<String> dexEntries = dexfile.entries();
                while (dexEntries.hasMoreElements()) {
                    String className = dexEntries.nextElement();
                    if (className.startsWith(COMPONENT_PACKAGE_NAME)) {
                        registerClass(register, group, className);
                    }
                }
            } catch (Throwable e) {
                Log.w(AndServer.TAG, "An exception occurred while registering components.", e);
            } finally {
                if (dexfile != null) {
                    try {
                        dexfile.close();
                    } catch (Throwable ignore) {
                    }
                }
            }
        }
    }

    private void registerClass(Register register, String group, String className) throws Exception {
        Class clazz = Class.forName(className);
        if (clazz.isInterface()) return;

        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (COMPONENT_INTERFACE_NAME.equals(anInterface.getName())) {
                Object obj = clazz.newInstance();
                if (obj instanceof OnRegister) {
                    Log.i(AndServer.TAG, String.format("Loading %s.", className));
                    OnRegister onRegister = (OnRegister)obj;
                    onRegister.onRegister(group, register);
                }
                break;
            }
        }
    }

    private static SharedPreferences getMultiDexPreferences(Context context) {
        return context.getSharedPreferences(PREFS_FILE,
            Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? Context.MODE_PRIVATE : Context.MODE_MULTI_PROCESS);
    }

    /**
     * Obtain all the dex path.
     *
     * @see com.android.support.MultiDex#loadExistingExtractions(Context, String)
     * @see com.android.support.MultiDex#clearOldDexDir(Context)
     */
    public static List<String> getDexFilePaths(Context context) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        File sourceApk = new File(appInfo.sourceDir);

        List<String> sourcePaths = new ArrayList<>();
        sourcePaths.add(appInfo.sourceDir);

        if (!isVMMultidexCapable()) {
            String extractedFilePrefix = sourceApk.getName() + EXTRACTED_NAME_EXT;
            int totalDexNumber = getMultiDexPreferences(context).getInt(KEY_DEX_NUMBER, 1);
            File dexDir = new File(appInfo.dataDir, CODE_CACHE_SECONDARY_DIRECTORY);

            for (int secondaryNumber = 2; secondaryNumber <= totalDexNumber; secondaryNumber++) {
                String fileName = extractedFilePrefix + secondaryNumber + EXTRACTED_SUFFIX;
                File extractedFile = new File(dexDir, fileName);
                if (extractedFile.isFile()) {
                    sourcePaths.add(extractedFile.getAbsolutePath());
                }
            }
        }

        if (AndServer.isDebug()) {
            sourcePaths.addAll(loadInstantRunDexFile(appInfo));
        }
        return sourcePaths;
    }

    /**
     * Identifies if the current VM has a native support for multidex.
     *
     * @return true, otherwise is false.
     *
     * @see android.support.multidex.MultiDexExtractor#isVMMultidexCapable(String)
     */
    private static boolean isVMMultidexCapable() {
        boolean isMultidexCapable = false;
        String vmVersion = System.getProperty("java.vm.version");
        try {
            Matcher matcher = Pattern.compile("(\\d+)\\.(\\d+)(\\.\\d+)?").matcher(vmVersion);
            if (matcher.matches()) {
                int major = Integer.parseInt(matcher.group(1));
                int minor = Integer.parseInt(matcher.group(2));
                isMultidexCapable = (major > 2) || ((major == 2) && (minor >= 1));
            }
        } catch (Exception ignore) {
        }
        String multidex = isMultidexCapable ? "has Multidex support" : "does not have Multidex support";
        Log.i(AndServer.TAG, String.format("VM with version %s %s.", vmVersion, multidex));
        return false;
    }

    /**
     * Get instant run dex path, used to catch the branch usingApkSplits=false.
     */
    private static List<String> loadInstantRunDexFile(ApplicationInfo appInfo) {
        List<String> instantRunDexPaths = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && appInfo.splitSourceDirs != null) {
            instantRunDexPaths.addAll(Arrays.asList(appInfo.splitSourceDirs));
            Log.i(AndServer.TAG, "InstantRun support was found.");
        } else {
            try {
                // Reflect instant run sdk to find where is the dex file.
                Class pathsByInstantRun = Class.forName("com.android.tools.fd.runtime.Paths");
                Method getDexFileDirectory = pathsByInstantRun.getMethod("getDexFileDirectory", String.class);
                String dexDirectory = (String)getDexFileDirectory.invoke(null, appInfo.packageName);

                File dexFolder = new File(dexDirectory);
                if (dexFolder.exists() && dexFolder.isDirectory()) {
                    File[] dexFiles = dexFolder.listFiles();
                    for (File file : dexFiles) {
                        if (file.exists() && file.isFile() && file.getName().endsWith(".dex")) {
                            instantRunDexPaths.add(file.getAbsolutePath());
                        }
                    }
                    Log.i(AndServer.TAG, "InstantRun support was found.");
                }

            } catch (ClassNotFoundException e) {
                Log.i(AndServer.TAG, "InstantRun support was not found.");
            } catch (Exception e) {
                Log.w(AndServer.TAG, "Finding InstantRun failed.", e);
            }
        }

        return instantRunDexPaths;
    }
}