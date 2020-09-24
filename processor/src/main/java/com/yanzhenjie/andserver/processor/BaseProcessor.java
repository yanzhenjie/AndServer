/*
 * Copyright 2018 Zhenjie Yan.
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
package com.yanzhenjie.andserver.processor;

import com.squareup.javapoet.TypeName;
import com.yanzhenjie.andserver.annotation.AppInfo;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Created by Zhenjie Yan on 2018/6/8.
 */
public abstract class BaseProcessor extends AbstractProcessor {

    @Override
    public final SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public final Set<String> getSupportedAnnotationTypes() {
        Set<Class<? extends Annotation>> classSet = new HashSet<>();
        addAnnotation(classSet);

        Set<String> nameSet = new HashSet<>();
        for (Class<? extends Annotation> clazz : classSet) {
            nameSet.add(clazz.getCanonicalName());
        }
        return nameSet;
    }

    protected abstract void addAnnotation(Set<Class<? extends Annotation>> classSet);

    protected boolean isAcceptType(TypeElement element, TypeName type) {
        TypeMirror mirror = element.getSuperclass();
        if (mirror == null || mirror.getKind() != TypeKind.DECLARED) {
            return false;
        }
        if (type.equals(TypeName.get(mirror))) {
            return true;
        }

        if (mirror instanceof DeclaredType) {
            Element parent = ((DeclaredType) mirror).asElement();
            if (parent instanceof TypeElement) {
                return isAcceptType((TypeElement) parent, type);
            }
        }
        return false;
    }

    protected boolean isAcceptInterface(TypeElement element, TypeName type) {
        List<? extends TypeMirror> mirrors = element.getInterfaces();

        for (TypeMirror mirror : mirrors) {
            if (type.equals(TypeName.get(mirror))) {
                return true;
            }
        }

        TypeMirror mirror = element.getSuperclass();
        if (mirror == null || mirror.getKind() != TypeKind.DECLARED) {
            return false;
        }

        if (mirror instanceof DeclaredType) {
            Element parent = ((DeclaredType) mirror).asElement();
            if (parent instanceof TypeElement) {
                return isAcceptInterface((TypeElement) parent, type);
            }
        }
        return false;
    }

    protected String getRegisterPackageName(Set<? extends Element> appSet) {
        List<String> list = appSet.stream()
            .map((Function<Element, String>) element -> {
                AppInfo appInfo = element.getAnnotation(AppInfo.class);
                return appInfo == null ? null : appInfo.value();
            })
            .collect(Collectors.toList());
        if (list.size() <= 0) {
            throw new RuntimeException(PLUGIN_MESSAGE);
        }
        String rootPackage = list.get(0);
        return String.format("%s.%s", rootPackage, "andserver.processor.generator");
    }

    private static final String PLUGIN_MESSAGE = "\nAdd the plugin to your project build script :"
        + "\nbuildscript {"
        + "\n    repositories {"
        + "\n       mavenCentral()"
        + "\n       google()"
        + "\n    }"
        + "\n    dependencies {"
        + "\n        classpath 'com.yanzhenjie.andserver:plugin:{version}'"
        + "\n        ..."
        + "\n    }"
        + "\n}\n"
        + "\nAnd then apply it in your module:"
        + "\napply plugin: 'com.yanzhenjie.andserver'";
}