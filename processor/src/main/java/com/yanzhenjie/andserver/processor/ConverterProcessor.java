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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.yanzhenjie.andserver.annotation.AppInfo;
import com.yanzhenjie.andserver.annotation.Converter;
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
 * Created by Zhenjie Yan on 2018/9/11.
 */
public class ConverterProcessor extends BaseProcessor {

    private Filer mFiler;
    private Elements mElements;
    private Logger mLog;

    private TypeName mContext;
    private TypeName mOnRegisterType;
    private TypeName mRegisterType;

    private TypeName mConverter;
    private TypeName mString;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        mFiler = processingEnv.getFiler();
        mElements = processingEnv.getElementUtils();
        mLog = new Logger(processingEnv.getMessager());

        mContext = TypeName.get(mElements.getTypeElement(Constants.CONTEXT_TYPE).asType());
        mOnRegisterType = TypeName.get(mElements.getTypeElement(Constants.ON_REGISTER_TYPE).asType());
        mRegisterType = TypeName.get(mElements.getTypeElement(Constants.REGISTER_TYPE).asType());

        mConverter = TypeName.get(mElements.getTypeElement(Constants.CONVERTER_TYPE).asType());

        mString = TypeName.get(String.class);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        if (CollectionUtils.isEmpty(set)) {
            return false;
        }

        Set<? extends Element> appSet = roundEnv.getElementsAnnotatedWith(AppInfo.class);
        String registerPackageName = getRegisterPackageName(appSet);

        Map<String, TypeElement> converterMap = findAnnotation(roundEnv);
        if (!converterMap.isEmpty()) {
            createRegister(registerPackageName, converterMap);
        }
        return true;
    }

    private Map<String, TypeElement> findAnnotation(RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Converter.class);
        Map<String, TypeElement> converterMap = new HashMap<>();

        for (Element element: set) {
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                Set<Modifier> modifiers = typeElement.getModifiers();
                Validate.isTrue(modifiers.contains(Modifier.PUBLIC), "The modifier public is missing on %s.",
                    typeElement.getQualifiedName());

                List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
                if (CollectionUtils.isEmpty(interfaces)) {
                    mLog.w(String.format(
                        "The annotation Converter must be used in a subclass of [MessageConverter] on %s.",
                        typeElement.getQualifiedName()));
                    continue;
                }
                for (TypeMirror typeMirror: interfaces) {
                    if (mConverter.equals(TypeName.get(typeMirror))) {
                        converterMap.put(getGroup(typeElement), typeElement);
                        break;
                    } else {
                        mLog.w(String.format(
                            "The annotation Converter must be used in a subclass of [MessageConverter] on %s.",
                            typeElement.getQualifiedName()));
                    }
                }
            }
        }
        return converterMap;
    }

    private void createRegister(String registerPackageName, Map<String, TypeElement> converterMap) {
        TypeName typeName = ParameterizedTypeName.get(ClassName.get(Map.class), mString, mConverter);
        FieldSpec mapField = FieldSpec.builder(typeName, "mMap", Modifier.PRIVATE).build();

        CodeBlock.Builder rootCode = CodeBlock.builder().addStatement("this.mMap = new $T<>()", HashMap.class);
        for (Map.Entry<String, TypeElement> entry: converterMap.entrySet()) {
            String group = entry.getKey();
            TypeElement converter = entry.getValue();
            mLog.i(String.format("------ Processing %s ------", converter.getSimpleName()));
            rootCode.addStatement("this.mMap.put($S, new $T())", group, converter);
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
            .addStatement("$T converter = mMap.get(group)", mConverter)
            .beginControlFlow("if(converter == null)")
            .addStatement("converter = mMap.get($S)", "default")
            .endControlFlow()
            .beginControlFlow("if(converter != null)")
            .addStatement("register.setConverter(converter)")
            .endControlFlow()
            .build();

        TypeSpec adapterClass = TypeSpec.classBuilder("ConverterRegister")
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
        Converter converter = type.getAnnotation(Converter.class);
        if (converter != null) {
            return converter.value();
        }
        throw new IllegalStateException(String.format("The type is not a Converter: %1$s.", type));
    }

    @Override
    protected void addAnnotation(Set<Class<? extends Annotation>> classSet) {
        classSet.add(Converter.class);
        classSet.add(AppInfo.class);
    }
}