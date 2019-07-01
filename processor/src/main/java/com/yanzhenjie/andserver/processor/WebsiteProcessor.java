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

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import com.yanzhenjie.andserver.annotation.Website;
import com.yanzhenjie.andserver.processor.util.Constants;
import com.yanzhenjie.andserver.processor.util.Logger;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Created by Zhenjie Yan on 2018/9/17.
 *
 * @deprecated use {@link ConfigProcessor} instead.
 */
@Deprecated
@AutoService(Processor.class)
public class WebsiteProcessor extends BaseProcessor {

    private Filer mFiler;
    private Elements mElements;
    private Types mTypes;
    private Logger mLog;

    private TypeName mContext;
    private TypeName mOnRegisterType;
    private TypeName mRegisterType;

    private TypeMirror mWebsiteMirror;
    private TypeName mWebsite;

    private TypeName mString;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        mFiler = processingEnv.getFiler();
        mElements = processingEnv.getElementUtils();
        mTypes = processingEnv.getTypeUtils();
        mLog = new Logger(processingEnv.getMessager());

        mContext = TypeName.get(mElements.getTypeElement(Constants.CONTEXT_TYPE).asType());
        mOnRegisterType = TypeName.get(mElements.getTypeElement(Constants.ON_REGISTER_TYPE).asType());
        mRegisterType = TypeName.get(mElements.getTypeElement(Constants.REGISTER_TYPE).asType());

        mWebsiteMirror = mElements.getTypeElement(Constants.WEBSITE_TYPE).asType();
        mWebsite = TypeName.get(mWebsiteMirror);

        mString = TypeName.get(String.class);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        if (CollectionUtils.isEmpty(set)) return false;

        StringBuilder builder = new StringBuilder();
        builder.append("The annotation [@Website] has been deprecated, please use [@Config] instead.")
            .append("\n")
            .append("@Config")
            .append("\n")
            .append("public class AppConfig implements WebConfig")
            .append("\n")
            .append("\n")
            .append("    @Override")
            .append("\n")
            .append("    public void onConfig(Context context, Delegate delegate) {")
            .append("\n")
            .append("        Website website = ...;")
            .append("\n")
            .append("        delegate.addWebsite(website);")
            .append("\n")
            .append("\n")
            .append("        Multipart multipart = Multipart.newBuilder...build();")
            .append("\n")
            .append("        delegate.setMultipart(multipart);")
            .append("\n")
            .append("    }")
            .append("\n")
            .append("}");

        mLog.w(builder.toString());

        Map<String, List<TypeElement>> websiteMap = findAnnotation(roundEnv);
        if (!websiteMap.isEmpty()) createRegister(websiteMap);
        return true;
    }

    private Map<String, List<TypeElement>> findAnnotation(RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Website.class);
        Map<String, List<TypeElement>> websiteMap = new HashMap<>();

        for (Element element : set) {
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;

                Set<Modifier> modifiers = typeElement.getModifiers();
                Validate.isTrue(modifiers.contains(Modifier.PUBLIC), "The modifier public is missing on %s.",
                    typeElement.getQualifiedName());

                TypeMirror superclass = typeElement.getSuperclass();
                if (superclass == null) {
                    mLog.w(String.format("The annotation Website must be used in a subclass of [Website] on %s.",
                        typeElement.getQualifiedName()));
                    continue;
                }

                if (mTypes.isSubtype(superclass, mWebsiteMirror)) {
                    String group = getGroup(typeElement);
                    List<TypeElement> elementList = websiteMap.get(group);
                    if (CollectionUtils.isEmpty(elementList)) {
                        elementList = new ArrayList<>();
                        websiteMap.put(group, elementList);
                    }
                    elementList.add(typeElement);
                } else {
                    mLog.w(String.format("The annotation Website must be used in a subclass of [Website] on %s.",
                        typeElement.getQualifiedName()));
                }
            }
        }
        return websiteMap;
    }

    private void createRegister(Map<String, List<TypeElement>> websiteMap) {
        TypeName listType = ParameterizedTypeName.get(ClassName.get(List.class), mWebsite);
        TypeName mapType = ParameterizedTypeName.get(ClassName.get(Map.class), mString, listType);
        FieldSpec mapField = FieldSpec.builder(mapType, "mMap", Modifier.PRIVATE).build();

        CodeBlock.Builder rootCode = CodeBlock.builder().addStatement("this.mMap = new $T<>()", HashMap.class);
        for (Map.Entry<String, List<TypeElement>> entry : websiteMap.entrySet()) {
            String group = entry.getKey();
            List<TypeElement> interceptorList = entry.getValue();

            CodeBlock.Builder groupCode = CodeBlock.builder()
                .addStatement("List<$T> $LList = new $T<>()", mWebsite, group, ArrayList.class);
            for (TypeElement type : interceptorList) {
                mLog.i(String.format("------ Processing %s ------", type.getSimpleName()));
                groupCode.addStatement("$LList.add(new $T())", group, type);
            }

            rootCode.add(groupCode.build());
            rootCode.addStatement("this.mMap.put($S, $LList)", group, group);
        }

        MethodSpec rootMethod = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addCode(rootCode.build())
            .build();

        MethodSpec registerMethod = MethodSpec.methodBuilder("onRegister")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(mContext, "context")
            .addParameter(mString, "group")
            .addParameter(mRegisterType, "register")
            .addStatement("List<$T> list = mMap.get(group)", mWebsite)
            .beginControlFlow("if(list != null && !list.isEmpty())")
            .beginControlFlow("for ($T website : list)", mWebsite)
            .addStatement("register.addAdapter(website)")
            .endControlFlow()
            .endControlFlow()
            .build();

        String packageName = Constants.REGISTER_PACKAGE;
        TypeSpec adapterClass = TypeSpec.classBuilder("WebsiteRegister")
            .addJavadoc(Constants.DOC_EDIT_WARN)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addSuperinterface(mOnRegisterType)
            .addField(mapField)
            .addMethod(rootMethod)
            .addMethod(registerMethod)
            .build();

        JavaFile javaFile = JavaFile.builder(packageName, adapterClass).build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getGroup(TypeElement type) {
        Website website = type.getAnnotation(Website.class);
        if (website != null) {
            return website.value();
        }
        throw new IllegalStateException(String.format("The type is not a Website: %1$s.", type));
    }

    @Override
    protected void addAnnotation(Set<Class<? extends Annotation>> classSet) {
        classSet.add(Website.class);
    }
}