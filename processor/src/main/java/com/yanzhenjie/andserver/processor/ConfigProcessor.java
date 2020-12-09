/*
 * Copyright © 2019 Zhenjie Yan.
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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.yanzhenjie.andserver.annotation.AppInfo;
import com.yanzhenjie.andserver.annotation.Config;
import com.yanzhenjie.andserver.processor.util.Constants;
import com.yanzhenjie.andserver.processor.util.Logger;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * Created by Zhenjie Yan on 2019-06-29.
 * <pre>
 * <code>@Config</code>
 * public class AppConfig implements WebConfig {
 *
 *     <code>@Override</code>
 *     public void onConfig(Context context, Delegate delegate) {
 *         Website website = ...;
 *         delegate.addWebsite(website);
 *
 *         Multipart multipart = Multipart.newBuilder()...build();
 *         delegate.setMultipart(multipart);
 *     }
 * }
 * </pre>
 */
public class ConfigProcessor extends BaseProcessor {

    private Filer mFiler;
    private Elements mElements;
    private Logger mLog;

    private TypeName mContext;
    private TypeName mOnRegisterType;
    private TypeName mRegisterType;

    private TypeName mConfig;
    private TypeName mDelegate;
    private TypeName mWebsite;
    private TypeName mMultipart;

    private TypeName mString;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        mFiler = processingEnv.getFiler();
        mElements = processingEnv.getElementUtils();
        mLog = new Logger(processingEnv.getMessager());

        mContext = TypeName.get(mElements.getTypeElement(Constants.CONTEXT_TYPE).asType());
        mOnRegisterType = TypeName.get(mElements.getTypeElement(Constants.ON_REGISTER_TYPE).asType());
        mRegisterType = TypeName.get(mElements.getTypeElement(Constants.REGISTER_TYPE).asType());

        mConfig = TypeName.get(mElements.getTypeElement(Constants.CONFIG_TYPE).asType());
        mDelegate = TypeName.get(mElements.getTypeElement(Constants.CONFIG_DELEGATE_TYPE).asType());
        mWebsite = TypeName.get(mElements.getTypeElement(Constants.WEBSITE_TYPE).asType());
        mMultipart = TypeName.get(mElements.getTypeElement(Constants.CONFIG_MULTIPART_TYPE).asType());

        mString = TypeName.get(String.class);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        if (CollectionUtils.isEmpty(set)) {
            return false;
        }

        Set<? extends Element> appSet = roundEnv.getElementsAnnotatedWith(AppInfo.class);
        String registerPackageName = getRegisterPackageName(appSet);

        Map<String, TypeElement> configMap = findAnnotation(roundEnv);
        createRegister(registerPackageName, configMap);
        return true;
    }

    private Map<String, TypeElement> findAnnotation(RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Config.class);
        Map<String, TypeElement> configMap = new HashMap<>();

        for (Element element: set) {
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                Set<Modifier> modifiers = typeElement.getModifiers();
                Validate.isTrue(modifiers.contains(Modifier.PUBLIC), "The modifier public is missing on %s.",
                    typeElement.getQualifiedName());

                List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
                if (CollectionUtils.isEmpty(interfaces)) {
                    mLog.w(String.format("The annotation Converter must be used in a subclass of [WebConfig] on %s.",
                        typeElement.getQualifiedName()));
                    continue;
                }
                for (TypeMirror typeMirror: interfaces) {
                    if (mConfig.equals(TypeName.get(typeMirror))) {
                        configMap.put(getGroup(typeElement), typeElement);
                        break;
                    } else {
                        mLog.w(String.format("The annotation Config must be used in a subclass of [WebConfig] on %s.",
                            typeElement.getQualifiedName()));
                    }
                }
            }
        }
        return configMap;
    }

    private void createRegister(String registerPackageName, Map<String, TypeElement> configMap) {
        TypeName typeName = ParameterizedTypeName.get(ClassName.get(Map.class), mString, mConfig);
        FieldSpec mapField = FieldSpec.builder(typeName, "mMap", Modifier.PRIVATE).build();

        CodeBlock.Builder rootCode = CodeBlock.builder().addStatement("this.mMap = new $T<>()", HashMap.class);
        for (Map.Entry<String, TypeElement> entry: configMap.entrySet()) {
            String group = entry.getKey();
            TypeElement config = entry.getValue();
            mLog.i(String.format("------ Processing %s ------", config.getSimpleName()));
            rootCode.addStatement("this.mMap.put($S, new $T())", group, config);
        }
        MethodSpec rootMethod = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addCode(rootCode.build())
            .build();

        TypeName listType = ParameterizedTypeName.get(ClassName.get(List.class), mWebsite);

        MethodSpec registerMethod = MethodSpec.methodBuilder("onRegister")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(mContext, "context")
            .addParameter(mString, "group")
            .addParameter(mRegisterType, "register")
            .addStatement("$T config = mMap.get(group)", mConfig)
            .beginControlFlow("if(config == null)")
            .addStatement("config = mMap.get($S)", "default")
            .endControlFlow()
            .beginControlFlow("if(config != null)")
            .addStatement("$T delegate = $T.newInstance()", mDelegate, mDelegate)
            .addStatement("config.onConfig(context, delegate)")
            .addStatement("$T list = delegate.getWebsites()", listType)
            .beginControlFlow("if(list != null && !list.isEmpty())")
            .beginControlFlow("for ($T website : list)", mWebsite)
            .addStatement("register.addAdapter(website)")
            .endControlFlow()
            .endControlFlow()
            .addStatement("$T multipart = delegate.getMultipart()", mMultipart)
            .addStatement("register.setMultipart(multipart)")
            .endControlFlow()
            .build();

        TypeSpec adapterClass = TypeSpec.classBuilder("ConfigRegister")
            .addJavadoc(Constants.DOC_EDIT_WARN)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addSuperinterface(mOnRegisterType)
            .addField(mapField)
            .addMethod(rootMethod)
            .addMethod(registerMethod)
            .build();

        JavaFile javaFile = JavaFile.builder(registerPackageName, adapterClass).build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getGroup(TypeElement type) {
        Config config = type.getAnnotation(Config.class);
        if (config != null) {
            return config.value();
        }
        throw new IllegalStateException(String.format("The type is not a Config: %1$s.", type));
    }

    @Override
    protected void addAnnotation(Set<Class<? extends Annotation>> classSet) {
        classSet.add(Config.class);
        classSet.add(AppInfo.class);
    }
}
