/*
 * Copyright 2020 Zhenjie Yan.
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
package com.yanzhenjie.andserver.plugin;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.build.gradle.FeatureExtension;
import com.android.build.gradle.FeaturePlugin;
import com.android.build.gradle.LibraryExtension;
import com.android.build.gradle.LibraryPlugin;
import com.android.build.gradle.api.BaseVariant;
import com.yanzhenjie.andserver.plugin.util.Log;

import org.apache.commons.io.FileUtils;
import org.gradle.api.Action;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Created by Zhenjie Yan on 4/10/20.
 */
public class AndServerPlugin implements Plugin<Project> {

    @Override
    public void apply(@Nonnull Project project) {
        Log.inject(project);
        project.getPlugins().all(it -> {
            if (it instanceof AppPlugin) {
                AppExtension extension = project.getExtensions().getByType(AppExtension.class);
                configGenerator(project, extension.getApplicationVariants());
            } else if (it instanceof LibraryPlugin) {
                LibraryExtension extension = project.getExtensions().getByType(LibraryExtension.class);
                configGenerator(project, extension.getLibraryVariants());
            } else if (it instanceof FeaturePlugin) {
                FeatureExtension extension = project.getExtensions().getByType(FeatureExtension.class);
                configGenerator(project, extension.getFeatureVariants());
            }
        });
    }

    private void configGenerator(Project project, DomainObjectSet<? extends BaseVariant> variants) {
        variants.all(it -> {
            configTask(it);

            File outputDir = new File(project.getBuildDir(), "generated/source/andServer/" + it.getDirName());
            String taskName = String.format("generate%sAppInfo", capitalize(it.getName()));
            Task generate = project.getTasks().create(taskName, AppInfoGenerator.class, generator -> {
                generator.setOutputDir(outputDir);
                String appId = it.getApplicationId();
                generator.setAppId(appId);
                String packageName = String.format("%s.andserver.plugin.generator", appId);
                generator.setPackageName(packageName);
            });
            it.registerJavaGeneratingTask(generate, outputDir);
        });
    }

    private void configTask(BaseVariant variant) {
        Action<Task> action = task -> {
            String filename = String.format("%s.andserver", variant.getApplicationId());
            File file = new File(variant.getMergeAssetsProvider().get().getOutputDir().get().getAsFile(), filename);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    FileUtils.write(file, filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        variant.getMergeAssetsProvider().get().doLast(action);
    }

    public static String capitalize(String text) {
        if (text != null && text.length() > 0) {
            char[] chars = text.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            return new String(chars);
        }
        return text;
    }
}