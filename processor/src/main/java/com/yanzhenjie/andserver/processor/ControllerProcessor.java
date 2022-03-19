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

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.yanzhenjie.andserver.annotation.Addition;
import com.yanzhenjie.andserver.annotation.AppInfo;
import com.yanzhenjie.andserver.annotation.Controller;
import com.yanzhenjie.andserver.annotation.CookieValue;
import com.yanzhenjie.andserver.annotation.CrossOrigin;
import com.yanzhenjie.andserver.annotation.DeleteMapping;
import com.yanzhenjie.andserver.annotation.FormPart;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PatchMapping;
import com.yanzhenjie.andserver.annotation.PathVariable;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.PutMapping;
import com.yanzhenjie.andserver.annotation.QueryParam;
import com.yanzhenjie.andserver.annotation.RequestBody;
import com.yanzhenjie.andserver.annotation.RequestHeader;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.ResponseBody;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.processor.cross.CrossOriginImpl;
import com.yanzhenjie.andserver.processor.cross.MergeCrossOrigin;
import com.yanzhenjie.andserver.processor.mapping.Any;
import com.yanzhenjie.andserver.processor.mapping.Delete;
import com.yanzhenjie.andserver.processor.mapping.Get;
import com.yanzhenjie.andserver.processor.mapping.Mapping;
import com.yanzhenjie.andserver.processor.mapping.Merge;
import com.yanzhenjie.andserver.processor.mapping.Null;
import com.yanzhenjie.andserver.processor.mapping.Patch;
import com.yanzhenjie.andserver.processor.mapping.Post;
import com.yanzhenjie.andserver.processor.mapping.Put;
import com.yanzhenjie.andserver.processor.util.Constants;
import com.yanzhenjie.andserver.processor.util.Logger;
import com.yanzhenjie.andserver.processor.util.Patterns;
import com.yanzhenjie.andserver.processor.util.Utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * Created by Zhenjie Yan on 2018/6/8.
 */
public class ControllerProcessor extends BaseProcessor implements Patterns {

    private Filer mFiler;
    private Elements mElements;
    private Logger mLog;

    private TypeName mContext;
    private TypeName mTextUtils;
    private ClassName mTypeWrapper;
    private TypeName mMediaType;
    private TypeName mOnRegisterType;
    private TypeName mRegisterType;

    private TypeName mBodyMissing;
    private TypeName mCookieMissing;
    private TypeName mParamMissing;
    private TypeName mHeaderMissing;
    private TypeName mPathMissing;
    private TypeName mParamError;

    private TypeName mAdapter;
    private TypeName mMappingAdapter;
    private TypeName mHandler;
    private TypeName mMappingHandler;
    private TypeName mView;
    private TypeName mViewObject;
    private TypeName mConverter;

    private TypeName mRequest;
    private TypeName mMultipartRequest;
    private TypeName mResponse;
    private TypeName mHttpMethod;
    private TypeName mHttpHeaders;
    private TypeName mSession;
    private TypeName mRequestBody;
    private TypeName mMultipartFile;
    private TypeName mMultipartFileArray;
    private TypeName mMultipartFileList;

    private TypeName mAddition;
    private TypeName mCrossOrigin;
    private TypeName mMapping;
    private TypeName mMimeTypeMapping;
    private TypeName mMethodMapping;
    private TypeName mPairMapping;
    private TypeName mPathMapping;
    private TypeName mMappingList;

    private TypeName mString = TypeName.get(String.class);
    private ArrayTypeName mStringArray = ArrayTypeName.of(String.class);
    private ArrayTypeName mIntArray = ArrayTypeName.of(int.class);
    private ArrayTypeName mLongArray = ArrayTypeName.of(long.class);
    private ArrayTypeName mFloatArray = ArrayTypeName.of(float.class);
    private ArrayTypeName mDoubleArray = ArrayTypeName.of(double.class);
    private ArrayTypeName mBooleanArray = ArrayTypeName.of(boolean.class);

    private TypeName mStringList = ParameterizedTypeName.get(ClassName.get(List.class), mString);

    private List<Integer> mHashCodes = new ArrayList<>();

    private Pattern mBlurredPathPattern = Pattern.compile(PATH_BLURRED_INCLUDE);

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElements = processingEnv.getElementUtils();
        mLog = new Logger(processingEnv.getMessager());

        mContext = TypeName.get(mElements.getTypeElement(Constants.CONTEXT_TYPE).asType());
        mTextUtils = TypeName.get(mElements.getTypeElement(Constants.TEXT_UTILS_TYPE).asType());
        mTypeWrapper = ClassName.get(mElements.getTypeElement(Constants.TYPE_WRAPPER_TYPE));
        mMediaType = TypeName.get(mElements.getTypeElement(Constants.MEDIA_TYPE).asType());
        mOnRegisterType = TypeName.get(mElements.getTypeElement(Constants.ON_REGISTER_TYPE).asType());
        mRegisterType = TypeName.get(mElements.getTypeElement(Constants.REGISTER_TYPE).asType());

        mBodyMissing = TypeName.get(mElements.getTypeElement(Constants.BODY_MISSING).asType());
        mCookieMissing = TypeName.get(mElements.getTypeElement(Constants.COOKIE_MISSING).asType());
        mParamMissing = TypeName.get(mElements.getTypeElement(Constants.PARAM_MISSING).asType());
        mHeaderMissing = TypeName.get(mElements.getTypeElement(Constants.HEADER_MISSING).asType());
        mPathMissing = TypeName.get(mElements.getTypeElement(Constants.PATH_MISSING).asType());
        mParamError = TypeName.get(mElements.getTypeElement(Constants.PARAM_ERROR).asType());

        mConverter = TypeName.get(mElements.getTypeElement(Constants.CONVERTER_TYPE).asType());
        mAdapter = TypeName.get(mElements.getTypeElement(Constants.ADAPTER_TYPE).asType());
        mMappingAdapter = TypeName.get(mElements.getTypeElement(Constants.MAPPING_ADAPTER_TYPE).asType());
        mHandler = TypeName.get(mElements.getTypeElement(Constants.HANDLER_TYPE).asType());
        mMappingHandler = TypeName.get(mElements.getTypeElement(Constants.MAPPING_HANDLER_TYPE).asType());
        mView = TypeName.get(mElements.getTypeElement(Constants.VIEW_TYPE).asType());
        mViewObject = TypeName.get(mElements.getTypeElement(Constants.VIEW_TYPE_OBJECT).asType());

        mRequest = TypeName.get(mElements.getTypeElement(Constants.REQUEST_TYPE).asType());
        mMultipartRequest = TypeName.get(mElements.getTypeElement(Constants.MULTIPART_REQUEST_TYPE).asType());
        mResponse = TypeName.get(mElements.getTypeElement(Constants.RESPONSE_TYPE).asType());
        mHttpMethod = TypeName.get(mElements.getTypeElement(Constants.HTTP_METHOD_TYPE).asType());
        mHttpHeaders = TypeName.get(mElements.getTypeElement(Constants.HTTP_HEADERS_TYPE).asType());
        mSession = TypeName.get(mElements.getTypeElement(Constants.SESSION_TYPE).asType());
        mRequestBody = TypeName.get(mElements.getTypeElement(Constants.REQUEST_BODY_TYPE).asType());
        mMultipartFile = TypeName.get(mElements.getTypeElement(Constants.MULTIPART_FILE_TYPE).asType());
        mMultipartFileArray = ArrayTypeName.of(mMultipartFile);
        mMultipartFileList = ParameterizedTypeName.get(ClassName.get(List.class), mMultipartFile);

        mAddition = TypeName.get(mElements.getTypeElement(Constants.ADDITION_TYPE).asType());
        mCrossOrigin = TypeName.get(mElements.getTypeElement(Constants.CROSS_ORIGIN_TYPE).asType());
        mMapping = TypeName.get(mElements.getTypeElement(Constants.MAPPING_TYPE).asType());
        mMimeTypeMapping = TypeName.get(mElements.getTypeElement(Constants.MIME_MAPPING_TYPE).asType());
        mMethodMapping = TypeName.get(mElements.getTypeElement(Constants.METHOD_MAPPING_TYPE).asType());
        mPairMapping = TypeName.get(mElements.getTypeElement(Constants.PAIR_MAPPING_TYPE).asType());
        mPathMapping = TypeName.get(mElements.getTypeElement(Constants.PATH_MAPPING_TYPE).asType());
        mMappingList = ParameterizedTypeName.get(ClassName.get(Map.class), mMapping, mHandler);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        if (CollectionUtils.isEmpty(set)) {
            return false;
        }

        Set<? extends Element> appSet = roundEnv.getElementsAnnotatedWith(AppInfo.class);
        String registerPackageName = getRegisterPackageName(appSet);

        Map<TypeElement, List<ExecutableElement>> controllers = new HashMap<>();
        findMapping(roundEnv.getElementsAnnotatedWith(RequestMapping.class), controllers);
        findMapping(roundEnv.getElementsAnnotatedWith(GetMapping.class), controllers);
        findMapping(roundEnv.getElementsAnnotatedWith(PostMapping.class), controllers);
        findMapping(roundEnv.getElementsAnnotatedWith(PutMapping.class), controllers);
        findMapping(roundEnv.getElementsAnnotatedWith(PatchMapping.class), controllers);
        findMapping(roundEnv.getElementsAnnotatedWith(DeleteMapping.class), controllers);

        if (!controllers.isEmpty()) {
            createHandlerAdapter(registerPackageName, controllers);
        }
        return true;
    }

    private void findMapping(Set<? extends Element> set, Map<TypeElement, List<ExecutableElement>> controllerMap) {
        for (Element element: set) {
            if (element instanceof ExecutableElement) {
                ExecutableElement execute = (ExecutableElement) element;
                Element enclosing = element.getEnclosingElement();
                if (!(enclosing instanceof TypeElement)) {
                    continue;
                }

                TypeElement type = (TypeElement) enclosing;
                Annotation restController = type.getAnnotation(RestController.class);
                Annotation controller = type.getAnnotation(Controller.class);
                if (restController == null && controller == null) {
                    mLog.w(String.format("Controller/RestController annotations may be missing on %s.",
                        type.getQualifiedName()));
                    continue;
                }

                String host = type.getQualifiedName() + "#" + execute.getSimpleName() + "()";

                Set<Modifier> modifiers = execute.getModifiers();
                Validate.isTrue(!modifiers.contains(Modifier.PRIVATE), "The modifier private is redundant on %s.",
                    host);

                if (modifiers.contains(Modifier.STATIC)) {
                    mLog.w(String.format("The modifier static is redundant on %s.", host));
                }

                List<ExecutableElement> elementList = controllerMap.get(type);
                if (CollectionUtils.isEmpty(elementList)) {
                    elementList = new ArrayList<>();
                    controllerMap.put(type, elementList);
                }
                elementList.add(execute);
            }
        }
    }

    private void createHandlerAdapter(String registerPackageName,
                                      Map<TypeElement, List<ExecutableElement>> controllers) {
        Map<String, List<String>> adapterMap = new HashMap<>();
        for (Map.Entry<TypeElement, List<ExecutableElement>> entry: controllers.entrySet()) {
            TypeElement type = entry.getKey();
            List<ExecutableElement> executes = entry.getValue();
            mLog.i(String.format("------ Processing %s ------", type.getSimpleName()));

            String typeName = type.getQualifiedName().toString();
            Mapping typeMapping = getTypeMapping(type);
            validateMapping(typeMapping, typeName);

            CrossOrigin typeCrossOrigin = type.getAnnotation(CrossOrigin.class);

            TypeName controllerType = TypeName.get(type.asType());
            FieldSpec hostField = FieldSpec.builder(controllerType, "mHost", Modifier.PRIVATE).build();
            FieldSpec mappingField = FieldSpec.builder(mMappingList, "mMappingMap", Modifier.PRIVATE).build();

            CodeBlock.Builder rootCode = CodeBlock.builder()
                .addStatement("this.mHost = new $T()", type)
                .addStatement("this.mMappingMap = new $T<>()", LinkedHashMap.class);
            for (ExecutableElement execute: executes) {
                Mapping mapping = getExecuteMapping(execute);
                validateExecuteMapping(mapping, typeName + "#" + execute.getSimpleName().toString() + "()");

                mapping = new Merge(typeMapping, mapping);
                rootCode.beginControlFlow("\n").addStatement("$T mapping = new $T()", mMapping, mMapping);
                addMapping(rootCode, mapping);

                Addition addition = execute.getAnnotation(Addition.class);
                rootCode.add("\n").addStatement("$T addition = new $T()", mAddition, mAddition);
                addAddition(rootCode, addition);

                CrossOrigin executeCrossOrigin = execute.getAnnotation(CrossOrigin.class);
                if (typeCrossOrigin == null && executeCrossOrigin == null) {
                    rootCode.add("\n").addStatement("$T crossOrigin = null", mCrossOrigin);
                } else {
                    rootCode.add("\n").addStatement("$T crossOrigin = new $T()", mCrossOrigin, mCrossOrigin);
                    MergeCrossOrigin crossOrigin = new MergeCrossOrigin(new CrossOriginImpl(typeCrossOrigin),
                        new CrossOriginImpl(executeCrossOrigin));
                    addCrossOrigin(rootCode, crossOrigin);
                }

                String handlerName = createHandler(type, execute, mapping.path(), mapping.isRest());
                rootCode.addStatement("$L handler = new $L(mHost, mapping, addition, crossOrigin)", handlerName,
                    handlerName).addStatement("mMappingMap.put(mapping, handler)").endControlFlow();
            }
            MethodSpec rootMethod = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addCode(rootCode.build())
                .build();

            MethodSpec mappingMethod = MethodSpec.methodBuilder("getMappingMap")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .returns(mMappingList)
                .addStatement("return mMappingMap")
                .build();

            MethodSpec hostMethod = MethodSpec.methodBuilder("getHost")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .returns(controllerType)
                .addStatement("return mHost")
                .build();

            String adapterPackageName = getPackageName(type).getQualifiedName().toString();
            String className = String.format("%sAdapter", type.getSimpleName());
            TypeSpec adapterClass = TypeSpec.classBuilder(className)
                .addJavadoc(Constants.DOC_EDIT_WARN)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(mMappingAdapter)
                .addField(hostField)
                .addField(mappingField)
                .addMethod(rootMethod)
                .addMethod(mappingMethod)
                .addMethod(hostMethod)
                .build();

            JavaFile javaFile = JavaFile.builder(adapterPackageName, adapterClass).build();
            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String group = getGroup(type);
            List<String> adapterList = adapterMap.get(group);
            if (CollectionUtils.isEmpty(adapterList)) {
                adapterList = new ArrayList<>();
                adapterMap.put(group, adapterList);
            }
            adapterList.add(adapterPackageName + "." + className);
        }

        if (!adapterMap.isEmpty()) {
            createRegister(registerPackageName, adapterMap);
        }
    }

    private Mapping getTypeMapping(TypeElement type) {
        Mapping mapping = null;
        boolean isRest = type.getAnnotation(ResponseBody.class) != null
            || type.getAnnotation(RestController.class) != null;
        RequestMapping requestMapping = type.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            mapping = new Any(requestMapping, isRest);
        }

        if (mapping == null) {
            GetMapping getMapping = type.getAnnotation(GetMapping.class);
            if (getMapping != null) {
                mapping = new Get(getMapping, isRest);
            }
        }
        if (mapping == null) {
            PostMapping postMapping = type.getAnnotation(PostMapping.class);
            if (postMapping != null) {
                mapping = new Post(postMapping, isRest);
            }
        }
        if (mapping == null) {
            PutMapping putMapping = type.getAnnotation(PutMapping.class);
            if (putMapping != null) {
                mapping = new Put(putMapping, isRest);
            }
        }
        if (mapping == null) {
            PatchMapping patchMapping = type.getAnnotation(PatchMapping.class);
            if (patchMapping != null) {
                mapping = new Patch(patchMapping, isRest);
            }
        }
        if (mapping == null) {
            DeleteMapping deleteMapping = type.getAnnotation(DeleteMapping.class);
            if (deleteMapping != null) {
                mapping = new Delete(deleteMapping, isRest);
            }
        }
        if (mapping == null) {
            mapping = new Null(isRest);
        }
        return mapping;
    }

    private void validateMapping(Mapping mapping, String host) {
        String[] paths = mapping.path();
        if (ArrayUtils.isEmpty(paths)) {
            paths = mapping.value();
        }
        if (ArrayUtils.isNotEmpty(paths)) {
            for (String path: paths) {
                boolean valid = path.matches(PATH_STRICT) || path.matches(PATH_BLURRED_MAYBE);
                Validate.isTrue(valid, "The format of path [%s] is wrong on %s.", path, host);
            }
        }

        String[] params = mapping.params();
        if (ArrayUtils.isNotEmpty(params)) {
            for (String param: params) {
                boolean valid = param.matches(PAIR_KEY);
                valid = valid || param.matches(PAIR_KEY_VALUE);
                valid = valid || param.matches(PAIR_NO_KEY);
                valid = valid || param.matches(PAIR_NO_VALUE);
                Validate.isTrue(valid, "The format of param [%s] is wrong on %s.", param, host);
            }
        }

        String[] headers = mapping.headers();
        if (ArrayUtils.isNotEmpty(headers)) {
            for (String head: headers) {
                boolean valid = head.matches(PAIR_KEY);
                valid = valid || head.matches(PAIR_KEY_VALUE);
                valid = valid || head.matches(PAIR_NO_KEY);
                valid = valid || head.matches(PAIR_NO_VALUE);
                Validate.isTrue(valid, "The format of header [%s] is wrong on %s.", head, host);
            }
        }

        String[] consumes = mapping.consumes();
        if (ArrayUtils.isNotEmpty(consumes)) {
            for (String consume: consumes) {
                try {
                    Utils.parseMimeType(consume);
                } catch (RuntimeException e) {
                    throw new IllegalArgumentException(
                        String.format("The format of consume [%s] is wrong on %s.", consume, host));
                }
            }
        }

        String[] produces = mapping.produces();
        if (ArrayUtils.isNotEmpty(produces)) {
            for (String produce: produces) {
                try {
                    Utils.parseMimeType(produce);
                } catch (RuntimeException e) {
                    throw new IllegalArgumentException(
                        String.format("The format of produce [%s] is wrong on %s.", produce, host));
                }
            }
        }
    }

    private Mapping getExecuteMapping(ExecutableElement execute) {
        Mapping mapping = null;
        RequestMapping requestMapping = execute.getAnnotation(RequestMapping.class);
        boolean isRest = execute.getAnnotation(ResponseBody.class) != null;
        if (requestMapping != null) {
            mapping = new Any(requestMapping, isRest);
        }

        if (mapping == null) {
            GetMapping getMapping = execute.getAnnotation(GetMapping.class);
            if (getMapping != null) {
                mapping = new Get(getMapping, isRest);
            }
        }
        if (mapping == null) {
            PostMapping postMapping = execute.getAnnotation(PostMapping.class);
            if (postMapping != null) {
                mapping = new Post(postMapping, isRest);
            }
        }
        if (mapping == null) {
            PutMapping putMapping = execute.getAnnotation(PutMapping.class);
            if (putMapping != null) {
                mapping = new Put(putMapping, isRest);
            }
        }
        if (mapping == null) {
            PatchMapping patchMapping = execute.getAnnotation(PatchMapping.class);
            if (patchMapping != null) {
                mapping = new Patch(patchMapping, isRest);
            }
        }
        if (mapping == null) {
            DeleteMapping deleteMapping = execute.getAnnotation(DeleteMapping.class);
            if (deleteMapping != null) {
                mapping = new Delete(deleteMapping, isRest);
            }
        }
        return mapping;
    }

    private void validateExecuteMapping(Mapping mapping, String host) {
        String[] paths = mapping.path();
        if (ArrayUtils.isEmpty(paths)) {
            paths = mapping.value();
        }
        Validate.notEmpty(paths, String.format("The path value of method cannot be empty on %s.", host));
        validateMapping(mapping, host);
    }

    private void addMapping(CodeBlock.Builder builder, Mapping mapping) {
        String[] pathArray = mapping.path();
        builder.add("\n").addStatement("$T path = new $T()", mPathMapping, mPathMapping);
        for (String path: pathArray) {
            builder.addStatement("path.addRule($S)", path);
        }
        builder.addStatement("mapping.setPath(path)");

        String[] methodArray = mapping.method();
        builder.add("\n").addStatement("$T method = new $T()", mMethodMapping, mMethodMapping);
        for (String method: methodArray) {
            builder.addStatement("method.addRule($S)", method);
        }
        builder.addStatement("mapping.setMethod(method)");

        String[] paramArray = mapping.params();
        if (ArrayUtils.isNotEmpty(paramArray)) {
            builder.add("\n").addStatement("$T param = new $T()", mPairMapping, mPairMapping);
            for (String param: paramArray) {
                builder.addStatement("param.addRule($S)", param);
            }
            builder.addStatement("mapping.setParam(param)");
        }

        String[] headerArray = mapping.headers();
        if (ArrayUtils.isNotEmpty(headerArray)) {
            builder.add("\n").addStatement("$T header = new $T()", mPairMapping, mPairMapping);
            for (String header: headerArray) {
                builder.addStatement("header.addRule($S)", header);
            }
            builder.addStatement("mapping.setHeader(header)");
        }

        String[] consumeArray = mapping.consumes();
        if (ArrayUtils.isNotEmpty(consumeArray)) {
            builder.add("\n").addStatement("$T consume = new $T()", mMimeTypeMapping, mMimeTypeMapping);
            for (String consume: consumeArray) {
                builder.addStatement("consume.addRule($S)", consume);
            }
            builder.addStatement("mapping.setConsume(consume)");
        }

        String[] produceArray = mapping.produces();
        if (ArrayUtils.isNotEmpty(produceArray)) {
            builder.add("\n").addStatement("$T produce = new $T()", mMimeTypeMapping, mMimeTypeMapping);
            for (String produce: produceArray) {
                builder.addStatement("produce.addRule($S)", produce);
            }
            builder.addStatement("mapping.setProduce(produce)");
        }
    }

    private void addAddition(CodeBlock.Builder builder, Addition addition) {
        if (addition == null) {
            return;
        }

        String[] stringType = addition.stringType();
        if (ArrayUtils.isEmpty(stringType)) {
            stringType = addition.value();
        }
        StringBuilder stringArray = new StringBuilder();
        for (String type: stringType) {
            if (stringArray.length() > 0) {
                stringArray.append(", ");
            }
            stringArray.append("\"").append(type).append("\"");
        }
        builder.add("\n")
            .addStatement("String[] stringType = new String[]{$L}", stringArray)
            .addStatement("addition.setStringType(stringType)");

        boolean[] booleanType = addition.booleanType();
        StringBuilder booleanArray = new StringBuilder();
        for (boolean type: booleanType) {
            if (booleanArray.length() > 0) {
                booleanArray.append(", ");
            }
            booleanArray.append(type);
        }
        builder.add("\n")
            .addStatement("boolean[] booleanType = new boolean[]{$L}", booleanArray)
            .addStatement("addition.setBooleanType(booleanType)");

        int[] intType = addition.intTypeType();
        StringBuilder intArray = new StringBuilder();
        for (int type: intType) {
            if (intArray.length() > 0) {
                intArray.append(", ");
            }
            intArray.append(type);
        }
        builder.add("\n")
            .addStatement("int[] intType = new int[]{$L}", intArray)
            .addStatement("addition.setIntType(intType)");

        long[] longType = addition.longType();
        StringBuilder longArray = new StringBuilder();
        for (long type: longType) {
            if (longArray.length() > 0) {
                longArray.append(", ");
            }
            longArray.append(type).append("L");
        }
        builder.add("\n")
            .addStatement("long[] longType = new long[]{$L}", longArray)
            .addStatement("addition.setLongType(longType)");

        short[] shortType = addition.shortType();
        StringBuilder shortArray = new StringBuilder();
        for (short type: shortType) {
            if (shortArray.length() > 0) {
                shortArray.append(", ");
            }
            shortArray.append(type);
        }
        builder.add("\n")
            .addStatement("short[] shortType = new short[]{$L}", shortArray)
            .addStatement("addition.setShortType(shortType)");

        float[] floatType = addition.floatType();
        StringBuilder floatArray = new StringBuilder();
        for (float type: floatType) {
            if (floatArray.length() > 0) {
                floatArray.append(", ");
            }
            floatArray.append(type).append("F");
        }
        builder.add("\n")
            .addStatement("float[] floatType = new float[]{$L}", floatArray)
            .addStatement("addition.setFloatType(floatType)");

        double[] doubleType = addition.doubleType();
        StringBuilder doubleArray = new StringBuilder();
        for (double type: doubleType) {
            if (doubleArray.length() > 0) {
                doubleArray.append(", ");
            }
            doubleArray.append(type).append("D");
        }
        builder.add("\n")
            .addStatement("double[] doubleType = new double[]{$L}", doubleArray)
            .addStatement("addition.setDoubleType(doubleType)");

        byte[] byteType = addition.byteType();
        StringBuilder byteArray = new StringBuilder();
        for (byte type: byteType) {
            if (byteArray.length() > 0) {
                byteArray.append(", ");
            }
            byteArray.append(type);
        }
        builder.add("\n")
            .addStatement("byte[] byteType = new byte[]{$L}", byteArray)
            .addStatement("addition.setByteType(byteType)");

        char[] charType = addition.charType();
        StringBuilder charArray = new StringBuilder();
        for (char type: charType) {
            if (charArray.length() > 0) {
                charArray.append(", ");
            }
            charArray.append("'").append(type).append("'");
        }
        builder.add("\n")
            .addStatement("char[] charType = new char[]{$L}", charArray)
            .addStatement("addition.setCharType(charType)");
    }

    private void addCrossOrigin(CodeBlock.Builder builder, MergeCrossOrigin crossOrigin) {
        String[] origins = crossOrigin.origins();
        StringBuilder originsArray = new StringBuilder();
        for (String origin: origins) {
            if (originsArray.length() > 0) {
                originsArray.append(", ");
            }
            originsArray.append("\"").append(origin).append("\"");
        }
        builder.add("\n")
            .addStatement("String[] origins = new String[]{$L}", originsArray)
            .addStatement("crossOrigin.setOrigins(origins)");

        String[] allowedHeaders = crossOrigin.allowedHeaders();
        StringBuilder allowedHeadersArray = new StringBuilder();
        for (String header: allowedHeaders) {
            if (allowedHeadersArray.length() > 0) {
                allowedHeadersArray.append(", ");
            }
            allowedHeadersArray.append("\"").append(header).append("\"");
        }
        builder.add("\n")
            .addStatement("String[] allowedHeaders = new String[]{$L}", allowedHeadersArray)
            .addStatement("crossOrigin.setAllowedHeaders(allowedHeaders)");

        String[] exposedHeaders = crossOrigin.exposedHeaders();
        StringBuilder exposedHeadersArray = new StringBuilder();
        for (String header: exposedHeaders) {
            if (exposedHeadersArray.length() > 0) {
                exposedHeadersArray.append(", ");
            }
            exposedHeadersArray.append("\"").append(header).append("\"");
        }
        builder.add("\n")
            .addStatement("String[] exposedHeaders = new String[]{$L}", exposedHeadersArray)
            .addStatement("crossOrigin.setExposedHeaders(exposedHeaders)");

        String[] methods = crossOrigin.methods();
        StringBuilder methodsArray = new StringBuilder();
        for (String method: methods) {
            if (methodsArray.length() > 0) {
                methodsArray.append(", ");
            }
            methodsArray.append("HttpMethod.").append(method);
        }
        builder.add("\n")
            .addStatement("$T[] methods = new $T[]{$L}", mHttpMethod, mHttpMethod, methodsArray)
            .addStatement("crossOrigin.setMethods(methods)");

        boolean allowCredentials = crossOrigin.allowCredentials();
        builder.add("\n").addStatement("crossOrigin.setAllowCredentials($L)", allowCredentials);

        long maxAge = crossOrigin.maxAge();
        builder.addStatement("crossOrigin.setMaxAge($L)", maxAge);
    }

    /**
     * Create a handler class and return the simple name of the handler.
     *
     * @return the simple name, such as the simple name of the class {@code com.example.User} is {@code User}.
     */
    private String createHandler(TypeElement type, ExecutableElement execute, String[] paths, boolean isRest) {
        FieldSpec hostField = FieldSpec.builder(Object.class, "mHost").addModifiers(Modifier.PRIVATE).build();

        MethodSpec rootMethod = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(Object.class, "host")
            .addParameter(mMapping, "mapping")
            .addParameter(mAddition, "addition")
            .addParameter(mCrossOrigin, "crossOrigin")
            .addStatement("super(host, mapping, addition, crossOrigin)")
            .addStatement("this.mHost = host")
            .build();

        CodeBlock.Builder handleCode = CodeBlock.builder()
            .addStatement("$T context = ($T)request.getAttribute($T.ANDROID_CONTEXT)", mContext, mContext, mRequest)
            .addStatement("String httpPath = request.getPath()")
            .addStatement("$T httpMethod = request.getMethod()", mHttpMethod)
            .add("\n")
            .addStatement("Object converterObj = request.getAttribute($T.HTTP_MESSAGE_CONVERTER)", mRequest)
            .addStatement("$T converter = null", mConverter)
            .beginControlFlow("if (converterObj != null && converterObj instanceof $T)", mConverter)
            .addStatement("converter = ($T)converterObj", mConverter)
            .endControlFlow()
            .add("\n")
            .addStatement("$T multiRequest = null", mMultipartRequest)
            .beginControlFlow("if (request instanceof $T)", mMultipartRequest)
            .addStatement("multiRequest = ($T) request", mMultipartRequest)
            .endControlFlow()
            .add("\n")
            .addStatement("$T requestBody = null", mRequestBody)
            .beginControlFlow("if (httpMethod.allowBody())")
            .addStatement("requestBody = request.getBody()")
            .endControlFlow()
            .add("\n")
            .addStatement("$T<String, String> pathMap = getPathVariable(httpPath)", Map.class)
            .add("\n")
            .add("/** ---------- Building Parameters ---------- **/ ")
            .add("\n");

        String host = type.getQualifiedName().toString() + "#" + execute.getSimpleName().toString() + "()";
        StringBuilder paramBuild = new StringBuilder();
        List<? extends VariableElement> parameters = execute.getParameters();
        if (!parameters.isEmpty()) {
            for (int i = 0; i < parameters.size(); i++) {
                VariableElement parameter = parameters.get(i);

                TypeName typeName = TypeName.get(parameter.asType());
                if (mContext.equals(typeName)) {
                    if (paramBuild.length() > 0) {
                        paramBuild.append(", ");
                    }
                    paramBuild.append("context");
                    continue;
                }

                if (mRequest.equals(typeName)) {
                    if (paramBuild.length() > 0) {
                        paramBuild.append(", ");
                    }
                    paramBuild.append("request");
                    continue;
                }

                if (mResponse.equals(typeName)) {
                    if (paramBuild.length() > 0) {
                        paramBuild.append(", ");
                    }
                    paramBuild.append("response");
                    continue;
                }

                if (mSession.equals(typeName)) {
                    handleCode.add("\n").addStatement("$T session$L = request.getValidSession()", mSession, i);
                    if (paramBuild.length() > 0) {
                        paramBuild.append(", ");
                    }
                    paramBuild.append(String.format("session%s", i));
                    continue;
                }

                if (mRequestBody.equals(typeName)) {
                    if (paramBuild.length() > 0) {
                        paramBuild.append(", ");
                    }
                    paramBuild.append("requestBody");
                    continue;
                }

                RequestHeader requestHeader = parameter.getAnnotation(RequestHeader.class);
                if (requestHeader != null) {
                    Validate.isTrue(isBasicType(typeName),
                        "The RequestHeader annotation only supports [String, int, long, float, double, boolean] on %s.",
                        host);

                    String name = requestHeader.name();
                    if (StringUtils.isEmpty(name)) {
                        name = requestHeader.value();
                    }
                    Validate.isTrue(!StringUtils.isEmpty(name), "The name of param is null on %s.", host);

                    String defaultValue = requestHeader.defaultValue();

                    handleCode.add("\n").addStatement("String header$LStr = request.getHeader($S)", i, name);
                    if (requestHeader.required() && StringUtils.isEmpty(defaultValue)) {
                        handleCode.beginControlFlow("if ($T.isEmpty(header$LStr))", mTextUtils, i)
                            .addStatement("throw new $T($S)", mHeaderMissing, name)
                            .endControlFlow();
                    } else {
                        handleCode.beginControlFlow("if ($T.isEmpty(header$LStr))", mTextUtils, i)
                            .addStatement("header$LStr = $S", i, defaultValue)
                            .endControlFlow();
                    }

                    createBasicParameter(handleCode, typeName, "header", i);
                    assignmentBasicParameter(handleCode, typeName, "header", i);

                    if (paramBuild.length() > 0) {
                        paramBuild.append(", ");
                    }
                    paramBuild.append(String.format(Locale.getDefault(), "header%d", i));
                    continue;
                }

                CookieValue cookieValue = parameter.getAnnotation(CookieValue.class);
                if (cookieValue != null) {
                    Validate.isTrue(mString.equals(typeName),
                        "CookieValue can only be used with [String] on %s.", host);

                    String name = cookieValue.name();
                    if (StringUtils.isEmpty(name)) {
                        name = cookieValue.value();
                    }
                    Validate.notEmpty(name, "The name of cookie is null on %s.", host);

                    String defaultValue = cookieValue.defaultValue();

                    handleCode.add("\n").addStatement("String cookie$L = request.getCookieValue($S)", i, name);
                    if (cookieValue.required() && StringUtils.isEmpty(defaultValue)) {
                        handleCode.beginControlFlow("if ($T.isEmpty(cookie$L))", mTextUtils, i)
                            .addStatement("throw new $T($S)", mCookieMissing, name)
                            .endControlFlow();
                    } else {
                        handleCode.beginControlFlow("if ($T.isEmpty(cookie$L))", mTextUtils, i)
                            .addStatement("cookie$L = $S;", i, defaultValue)
                            .endControlFlow();
                    }

                    if (paramBuild.length() > 0) {
                        paramBuild.append(", ");
                    }
                    paramBuild.append(String.format(Locale.getDefault(), "cookie%d", i));
                    continue;
                }

                PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
                if (pathVariable != null) {
                    Validate.isTrue(isBasicType(typeName), "The PathVariable annotation only supports " +
                        "[String, int, long, float, double, boolean] on %s.", host);

                    String name = pathVariable.name();
                    if (StringUtils.isEmpty(name)) {
                        name = pathVariable.value();
                    }
                    Validate.isTrue(!StringUtils.isEmpty(name), "The name of path is null on %s.", host);

                    String defaultValue = pathVariable.defaultValue();

                    boolean isBlurred = false;
                    for (String path: paths) {
                        if (path.matches(PATH_BLURRED_MAYBE) && mBlurredPathPattern.matcher(path).find()) {
                            isBlurred = true;
                        }
                    }
                    Validate.isTrue(isBlurred, "The PathVariable annotation must have a blurred path, " +
                        "for example [/project/{name}]. The error occurred on %s.", host);

                    handleCode.add("\n").addStatement("String path$LStr = pathMap.get($S)", i, name);

                    if (pathVariable.required() && StringUtils.isEmpty(defaultValue)) {
                        handleCode.beginControlFlow("if ($T.isEmpty(path$LStr))", mTextUtils, i)
                            .addStatement("throw new $T($S)", mPathMissing, name)
                            .endControlFlow();
                    } else {
                        handleCode.beginControlFlow("if ($T.isEmpty(path$LStr))", mTextUtils, i)
                            .addStatement("path$LStr = $S;", i, defaultValue)
                            .endControlFlow();
                    }

                    createBasicParameter(handleCode, typeName, "path", i);
                    assignmentBasicParameter(handleCode, typeName, "path", i);

                    if (paramBuild.length() > 0) {
                        paramBuild.append(", ");
                    }
                    paramBuild.append(String.format(Locale.getDefault(), "path%d", i));
                    continue;
                }

                QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
                if (queryParam != null) {
                    boolean isBasicType = isBasicType(typeName);
                    boolean isBasicArrayType = isBasicArrayType(typeName);
                    Validate.isTrue(isBasicType || isBasicArrayType, "The QueryParam annotation " +
                        "only supports [String, int, long, float, double, boolean] on %s.", host);

                    String name = queryParam.name();
                    if (StringUtils.isEmpty(name)) {
                        name = queryParam.value();
                    }
                    Validate.isTrue(!StringUtils.isEmpty(name), "The name of param is null on %s.", host);

                    String defaultValue = queryParam.defaultValue();

                    if (isBasicType) {
                        handleCode.add("\n").addStatement("String param$LStr = request.getQuery($S)", i, name);
                        if (queryParam.required() && StringUtils.isEmpty(defaultValue)) {
                            handleCode.beginControlFlow("if ($T.isEmpty(param$LStr))", mTextUtils, i)
                                .addStatement("throw new $T($S)", mParamMissing, name)
                                .endControlFlow();
                        } else {
                            handleCode.beginControlFlow("if ($T.isEmpty(param$LStr))", mTextUtils, i)
                                .addStatement("param$LStr = $S", i, defaultValue)
                                .endControlFlow();
                        }

                        createBasicParameter(handleCode, typeName, "param", i);
                        assignmentBasicParameter(handleCode, typeName, "param", i);
                    } else {
                        handleCode.add("\n")
                            .addStatement("$T param$LList = request.getQueries($S)", mStringList, i, name);
                        if (queryParam.required() && StringUtils.isEmpty(defaultValue)) {
                            handleCode.beginControlFlow("if (param$LList == null || param$LList.isEmpty())", i, i)
                                .addStatement("throw new $T($S)", mParamMissing, name)
                                .endControlFlow();
                        } else {
                            handleCode.beginControlFlow("if (param$LList = null || param$LList.isEmpty())", i, i)
                                .addStatement("param$LList = new $T<>()", i, TypeName.get(ArrayList.class))
                                .addStatement("param$LList.add($S)", i, defaultValue)
                                .endControlFlow();
                        }

                        createBasicArrayParameter(handleCode, typeName, i);
                        assignmentBasicArrayParameter(handleCode, typeName, i);
                    }

                    if (paramBuild.length() > 0) {
                        paramBuild.append(", ");
                    }
                    paramBuild.append(String.format(Locale.getDefault(), "param%d", i));
                    continue;
                }

                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                if (requestParam != null) {
                    boolean isFile = mMultipartFile.equals(typeName) || mMultipartFileArray.equals(typeName);
                    boolean isBasicType = isBasicType(typeName);
                    boolean isBasicArrayType = isBasicArrayType(typeName);

                    String name = requestParam.name();
                    if (StringUtils.isEmpty(name)) {
                        name = requestParam.value();
                    }
                    Validate.isTrue(!StringUtils.isEmpty(name), "The name of param is null on %s.", host);

                    String defaultValue = requestParam.defaultValue();

                    handleCode.add("\n");
                    if (isFile) {
                        if (mMultipartFile.equals(typeName)) {
                            handleCode.addStatement("$T param$L = null", mMultipartFile, i)
                                .beginControlFlow("if (multiRequest != null)")
                                .addStatement("param$L = multiRequest.getFile($S)", i, name)
                                .endControlFlow();
                            if (requestParam.required()) {
                                handleCode.beginControlFlow("if (param$L == null)", i)
                                    .addStatement("throw new $T($S)", mParamMissing, name)
                                    .endControlFlow();
                            }
                        } else {
                            handleCode.addStatement("$T param$LList = null", mMultipartFileList, i)
                                .beginControlFlow("if (multiRequest != null)")
                                .addStatement("param$LList = multiRequest.getFiles($S)", i, name)
                                .endControlFlow();
                            if (requestParam.required()) {
                                handleCode.beginControlFlow("if (param$LList == null || param$LList.isEmpty())", i, i)
                                    .addStatement("throw new $T($S)", mParamMissing, name)
                                    .endControlFlow();
                            }

                            handleCode.addStatement("int param$LListSize = param$LList.size()", i, i)
                                .addStatement("$T[] param$L = new $T[param$LListSize]", mMultipartFile, i,
                                    mMultipartFile,
                                    i)
                                .beginControlFlow("if(param$LListSize > 0)", i)
                                .addStatement("param$LList.toArray(param$L)", i, i)
                                .endControlFlow();
                        }
                    } else if (isBasicType) {
                        handleCode.addStatement("String param$LStr = request.getParameter($S)", i, name);

                        if (requestParam.required() && StringUtils.isEmpty(defaultValue)) {
                            handleCode.beginControlFlow("if ($T.isEmpty(param$LStr))", mTextUtils, i)
                                .addStatement("throw new $T($S)", mParamMissing, name)
                                .endControlFlow();
                        } else {
                            handleCode.beginControlFlow("if ($T.isEmpty(param$LStr))", mTextUtils, i)
                                .addStatement("param$LStr = $S", i, defaultValue)
                                .endControlFlow();
                        }

                        createBasicParameter(handleCode, typeName, "param", i);
                        assignmentBasicParameter(handleCode, typeName, "param", i);
                    } else if (isBasicArrayType) {
                        handleCode.addStatement("$T param$LList = request.getParameters($S)", mStringList, i, name);

                        if (requestParam.required() && StringUtils.isEmpty(defaultValue)) {
                            handleCode.beginControlFlow("if (param$LList == null || param$LList.isEmpty())", i, i)
                                .addStatement("throw new $T($S)", mParamMissing, name)
                                .endControlFlow();
                        } else {
                            handleCode.beginControlFlow("if (param$LList == null || param$LList.isEmpty())", i, i)
                                .addStatement("param$LList = new $T<>()", i, TypeName.get(ArrayList.class))
                                .addStatement("param$LList.add($S)", i, defaultValue)
                                .endControlFlow();
                        }

                        createBasicArrayParameter(handleCode, typeName, i);
                        assignmentBasicArrayParameter(handleCode, typeName, i);
                    } else {
                        handleCode.addStatement("String param$LStr = request.getParameter($S)", i, name);

                        if (requestParam.required() && StringUtils.isEmpty(defaultValue)) {
                            handleCode.beginControlFlow("if ($T.isEmpty(param$LStr))", mTextUtils, i)
                                .addStatement("throw new $T($S)", mParamMissing, name)
                                .endControlFlow();
                        } else {
                            handleCode.beginControlFlow("if ($T.isEmpty(param$LStr))", mTextUtils, i)
                                .addStatement("param$LStr = $S", i, requestParam.defaultValue())
                                .endControlFlow();
                        }

                        TypeName wrapperType = ParameterizedTypeName.get(mTypeWrapper, typeName);
                        handleCode.addStatement("$T param$L = null", typeName, i)
                            .beginControlFlow("if (converter != null && !$T.isEmpty(param$LStr))", mTextUtils, i)
                            .addStatement("byte[] data = param$LStr.getBytes()", i)
                            .addStatement("$T stream = new $T(data)", InputStream.class, ByteArrayInputStream.class)
                            .addStatement("$T mimeType = $T.TEXT_PLAIN", mMediaType, mMediaType)
                            .addStatement("$T type = new $T(){}.getType()", Type.class, wrapperType)
                            .addStatement("param$L = converter.convert(stream, mimeType, type)", i)
                            .endControlFlow();
                    }

                    if (paramBuild.length() > 0) {
                        paramBuild.append(", ");
                    }
                    paramBuild.append(String.format(Locale.getDefault(), "param%d", i));
                    continue;
                }

                FormPart formPart = parameter.getAnnotation(FormPart.class);
                if (formPart != null) {
                    String name = formPart.name();
                    if (StringUtils.isEmpty(name)) {
                        name = formPart.value();
                    }
                    Validate.isTrue(!StringUtils.isEmpty(name), "The name of param is null on %s.", host);

                    handleCode.add("\n");
                    if (mMultipartFile.equals(typeName)) {
                        handleCode.addStatement("$T param$L = null", mMultipartFile, i)
                            .beginControlFlow("if (multiRequest != null)")
                            .addStatement("param$L = multiRequest.getFile($S)", i, name)
                            .endControlFlow();
                        if (formPart.required()) {
                            handleCode.beginControlFlow("if (param$L == null)", i)
                                .addStatement("throw new $T($S)", mParamMissing, name)
                                .endControlFlow();
                        }
                    } else if (mMultipartFileArray.equals(typeName)) {
                        handleCode.addStatement("$T param$LList = null", mMultipartFileList, i)
                            .beginControlFlow("if (multiRequest != null)")
                            .addStatement("param$LList = multiRequest.getFiles($S)", i, name)
                            .endControlFlow();
                        if (formPart.required()) {
                            handleCode.beginControlFlow("if (param$LList == null || param$LList.isEmpty())", i, i)
                                .addStatement("throw new $T($S)", mParamMissing, name)
                                .endControlFlow();
                        }

                        handleCode.addStatement("int param$LListSize = param$LList.size()", i, i)
                            .addStatement("$T[] param$L = new $T[param$LListSize]",
                                mMultipartFile, i, mMultipartFile, i)
                            .beginControlFlow("if(param$LListSize > 0)", i)
                            .addStatement("param$LList.toArray(param$L)", i, i)
                            .endControlFlow();
                    } else {
                        TypeName wrapperType = ParameterizedTypeName.get(mTypeWrapper, typeName);
                        handleCode.addStatement("$T param$L = null", typeName, i)
                            .beginControlFlow("if (converter != null && multiRequest != null)")
                            .addStatement("$T param$LType = new $T(){}.getType()", Type.class, i, wrapperType)
                            .addStatement("$T param$LFile = multiRequest.getFile($S)", mMultipartFile, i, name)
                            .beginControlFlow("if (param$LFile != null)", i)
                            .addStatement("$T stream = param$LFile.getStream()", InputStream.class, i)
                            .addStatement("$T mimeType = param$LFile.getContentType()", mMediaType, i)
                            .addStatement("param$L = converter.convert(stream, mimeType, param$LType)", i, i)
                            .endControlFlow()
                            .beginControlFlow("if (param$L == null)", i)
                            .addStatement("String param$LStr = multiRequest.getParameter($S)", i, name)
                            .beginControlFlow("if (!$T.isEmpty(param$LStr))", mTextUtils, i)
                            .addStatement("byte[] data = param$LStr.getBytes()", i)
                            .addStatement("$T stream = new $T(data)", InputStream.class, ByteArrayInputStream.class)
                            .addStatement("$T mimeType = $T.TEXT_PLAIN", mMediaType, mMediaType)
                            .addStatement("param$L = converter.convert(stream, mimeType, param$LType)", i, i)
                            .endControlFlow()
                            .endControlFlow()
                            .endControlFlow();

                        if (formPart.required()) {
                            handleCode.beginControlFlow("if (param$L == null)", i)
                                .addStatement("throw new $T($S)", mParamMissing, name)
                                .endControlFlow();
                        }
                    }
                    if (paramBuild.length() > 0) {
                        paramBuild.append(", ");
                    }
                    paramBuild.append(String.format(Locale.getDefault(), "param%d", i));
                    continue;
                }

                RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
                if (requestBody != null) {
                    handleCode.add("\n");

                    if (mString.equals(typeName)) {
                        handleCode.addStatement("String body$L = requestBody.string()", i);
                    } else {
                        TypeName wrapperType = ParameterizedTypeName.get(mTypeWrapper, typeName);
                        handleCode.addStatement("$T body$L = null", typeName, i)
                            .beginControlFlow("if (converter != null && requestBody != null)")
                            .addStatement("$T body$LType = new $T(){}.getType()", Type.class, i, wrapperType)
                            .addStatement("$T stream = requestBody.stream()", InputStream.class)
                            .addStatement("$T mimeType = requestBody.contentType()", mMediaType)
                            .addStatement("body$L = converter.convert(stream, mimeType, body$LType)", i, i)
                            .endControlFlow();
                    }

                    if (requestBody.required()) {
                        handleCode.beginControlFlow("if (body$L == null)", i)
                            .addStatement("throw new $T()", mBodyMissing)
                            .endControlFlow();
                    }

                    if (paramBuild.length() > 0) {
                        paramBuild.append(", ");
                    }
                    paramBuild.append(String.format(Locale.getDefault(), "body%d", i));
                    continue;
                }

                throw new IllegalStateException(
                    String.format("The parameter type [%s] is not supported on %s.", typeName, host));
            }
        }

        String executeName = execute.getSimpleName().toString();
        TypeMirror returnMirror = execute.getReturnType();
        boolean isVoid = TypeKind.VOID.equals(returnMirror.getKind());
        if (isVoid) {
            handleCode.addStatement("(($T)mHost).$L($L)", type, executeName, paramBuild.toString());
        } else {
            handleCode.addStatement("Object o = (($T)mHost).$L($L)", type, executeName, paramBuild.toString());
        }
        handleCode.addStatement("return new $T($L, $L)", mViewObject, isRest, isVoid ? null : "o");

        MethodSpec handleMethod = MethodSpec.methodBuilder("onHandle")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PROTECTED)
            .returns(mView)
            .addParameter(mRequest, "request")
            .addParameter(mResponse, "response")
            .addException(Throwable.class)
            .addCode(handleCode.build())
            .build();


        String packageName = getPackageName(type).getQualifiedName().toString();
        executeName = StringUtils.capitalize(executeName);
        String className = String.format("%s%sHandler%s", type.getSimpleName(), executeName, "");
        int i = 0;
        while (mHashCodes.contains(className.hashCode())) {
            i++;
            className = String.format("%s%sHandler%s", type.getSimpleName(), executeName, i);
        }
        mHashCodes.add(className.hashCode());

        TypeSpec handlerClass = TypeSpec.classBuilder(className)
            .addJavadoc(Constants.DOC_EDIT_WARN)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .superclass(mMappingHandler)
            .addField(hostField)
            .addMethod(rootMethod)
            .addMethod(handleMethod)
            .build();

        JavaFile javaFile = JavaFile.builder(packageName, handlerClass).build();
        try {
            javaFile.writeTo(mFiler);
            return className;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isBasicType(TypeName typeName) {
        return mString.equals(typeName) || TypeName.INT.equals(typeName) || TypeName.LONG.equals(typeName) ||
            TypeName.FLOAT.equals(typeName) || TypeName.DOUBLE.equals(typeName) || TypeName.BOOLEAN.equals(typeName);
    }

    private void createBasicParameter(CodeBlock.Builder builder, TypeName type, String name, int index) {
        TypeName box = type.isBoxedPrimitive() ? type : type.box();
        builder.addStatement("$T $L$L = null", box, name, index);
    }

    private void assignmentBasicParameter(CodeBlock.Builder builder, TypeName type, String name, int index) {
        builder.beginControlFlow("try");
        TypeName box = type.isBoxedPrimitive() ? type : type.box();
        builder.addStatement("$L$L = $T.valueOf($L$LStr)", name, index, box, name, index);
        builder.nextControlFlow("catch (Throwable e)").addStatement("throw new $T(e)", mParamError).endControlFlow();
    }

    private boolean isBasicArrayType(TypeName typeName) {
        return mStringArray.equals(typeName) || mIntArray.equals(typeName) || mLongArray.equals(typeName) ||
            mFloatArray.equals(typeName) || mDoubleArray.equals(typeName) || mBooleanArray.equals(typeName);
    }

    private void createBasicArrayParameter(CodeBlock.Builder builder, TypeName type, int index) {
        TypeName component = ((ArrayTypeName) type).componentType;
        builder.addStatement("$T[] param$L = new $T[param$LList.size()]", component, index, component, index);
    }

    private void assignmentBasicArrayParameter(CodeBlock.Builder builder, TypeName type, int index) {
        builder.beginControlFlow("try");
        TypeName component = ((ArrayTypeName) type).componentType;
        TypeName box = component.isBoxedPrimitive() ? component : component.box();
        builder.beginControlFlow("for(int i = 0; i < param$LList.size(); i++)", index)
            .addStatement("param$L[i] = $T.valueOf(param$LList.get(i))", index, box, index)
            .endControlFlow();
        builder.nextControlFlow("catch (Throwable e)").addStatement("throw new $T(e)", mParamError).endControlFlow();
    }

    private void createRegister(String packageName, Map<String, List<String>> adapterMap) {
        TypeName listTypeName = ParameterizedTypeName.get(ClassName.get(List.class), mAdapter);
        TypeName typeName = ParameterizedTypeName.get(ClassName.get(Map.class), mString, listTypeName);
        FieldSpec mapField = FieldSpec.builder(typeName, "mMap", Modifier.PRIVATE).build();

        CodeBlock.Builder rootCode = CodeBlock.builder().addStatement("this.mMap = new $T<>()", HashMap.class);
        for (Map.Entry<String, List<String>> entry: adapterMap.entrySet()) {
            String group = entry.getKey();
            List<String> adapterList = entry.getValue();

            CodeBlock.Builder groupCode = CodeBlock.builder()
                .addStatement("List<$T> $LList = new $T<>()", mAdapter, group, ArrayList.class);
            for (String adapterName: adapterList) {
                ClassName className = ClassName.bestGuess(adapterName);
                groupCode.addStatement("$LList.add(new $T())", group, className);
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
            .addStatement("List<$T> list = mMap.get(group)", mAdapter)
            .beginControlFlow("if(list == null)")
            .addStatement("list = new $T<>()", ArrayList.class)
            .endControlFlow()
            .addStatement("List<$T> defaultList = mMap.get($S)", mAdapter, "default")
            .beginControlFlow("if(defaultList != null && !defaultList.isEmpty())")
            .addStatement("list.addAll(defaultList)")
            .endControlFlow()
            .beginControlFlow("if(list != null && !list.isEmpty())")
            .beginControlFlow("for ($T adapter : list)", mAdapter)
            .addStatement("register.addAdapter(adapter)")
            .endControlFlow()
            .endControlFlow()
            .build();

        String className = "AdapterRegister";
        TypeSpec handlerClass = TypeSpec.classBuilder(className)
            .addJavadoc(Constants.DOC_EDIT_WARN)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addSuperinterface(mOnRegisterType)
            .addField(mapField)
            .addMethod(rootMethod)
            .addMethod(registerMethod)
            .build();

        JavaFile javaFile = JavaFile.builder(packageName, handlerClass).build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getGroup(TypeElement type) {
        Controller controller = type.getAnnotation(Controller.class);
        if (controller != null) {
            return controller.value();
        }
        RestController restController = type.getAnnotation(RestController.class);
        if (restController != null) {
            return restController.value();
        }
        throw new IllegalStateException(String.format("The type is not a Controller: %1$s.", type));
    }

    private PackageElement getPackageName(Element element) {
        while (element.getKind() != ElementKind.PACKAGE) {
            element = element.getEnclosingElement();
        }
        return (PackageElement) element;
    }

    @Override
    protected void addAnnotation(Set<Class<? extends Annotation>> classSet) {
        classSet.add(RequestMapping.class);
        classSet.add(GetMapping.class);
        classSet.add(PostMapping.class);
        classSet.add(PutMapping.class);
        classSet.add(PatchMapping.class);
        classSet.add(DeleteMapping.class);
        classSet.add(AppInfo.class);
    }
}