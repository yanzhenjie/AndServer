/*
 * Copyright 2018 Yan Zhenjie.
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

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.yanzhenjie.andserver.annotation.Addition;
import com.yanzhenjie.andserver.annotation.Controller;
import com.yanzhenjie.andserver.annotation.CookieValue;
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

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * Created by YanZhenjie on 2018/6/8.
 */
@AutoService(Processor.class)
public class ControllerProcessor extends BaseProcessor implements Patterns {

    private Filer mFiler;
    private Elements mElements;
    private Logger mLog;

    private TypeName mContext;
    private TypeName mAndServer;
    private TypeName mStringUtils;
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
    private TypeName mRequestMultipart;
    private TypeName mResponse;
    private TypeName mHttpMethod;
    private TypeName mSession;
    private TypeName mRequestBody;
    private TypeName mMultipart;

    private TypeName mAddition;
    private TypeName mMapping;
    private TypeName mMappingUnmodifiable;
    private TypeName mMimeType;
    private TypeName mMimeTypeUnmodifiable;
    private TypeName mMethod;
    private TypeName mMethodUnmodifiable;
    private TypeName mPair;
    private TypeName mPairUnmodifiable;
    private TypeName mPath;
    private TypeName mPathUnmodifiable;
    private TypeName mMappings;

    private TypeName mString;
    private TypeName mInt;
    private TypeName mLong;
    private TypeName mFloat;
    private TypeName mDouble;
    private TypeName mBoolean;

    private List<Integer> mHashCodes = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElements = processingEnv.getElementUtils();
        mLog = new Logger(processingEnv.getMessager());

        mContext = TypeName.get(mElements.getTypeElement(Constants.CONTEXT_TYPE).asType());
        mAndServer = TypeName.get(mElements.getTypeElement(Constants.ANDSERVER_TYPE).asType());
        mStringUtils = TypeName.get(mElements.getTypeElement(Constants.STRING_UTIL_TYPE).asType());
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
        mRequestMultipart = TypeName.get(mElements.getTypeElement(Constants.REQUEST_TYPE_MULTIPART).asType());
        mResponse = TypeName.get(mElements.getTypeElement(Constants.RESPONSE_TYPE).asType());
        mHttpMethod = TypeName.get(mElements.getTypeElement(Constants.HTTP_METHOD_TYPE).asType());
        mSession = TypeName.get(mElements.getTypeElement(Constants.SESSION_TYPE).asType());
        mRequestBody = TypeName.get(mElements.getTypeElement(Constants.REQUEST_BODY_TYPE).asType());
        mMultipart = TypeName.get(mElements.getTypeElement(Constants.MULTIPART_TYPE).asType());

        mAddition = TypeName.get(mElements.getTypeElement(Constants.ADDITION_TYPE).asType());
        mMapping = TypeName.get(mElements.getTypeElement(Constants.MAPPING_TYPE).asType());
        mMappingUnmodifiable = TypeName.get(mElements.getTypeElement(Constants.MAPPING_TYPE_UNMODIFIABLE).asType());
        mMimeType = TypeName.get(mElements.getTypeElement(Constants.MIME_TYPE).asType());
        mMimeTypeUnmodifiable = TypeName.get(mElements.getTypeElement(Constants.MIME_TYPE_UNMODIFIABLE).asType());
        mMethod = TypeName.get(mElements.getTypeElement(Constants.METHOD_TYPE).asType());
        mMethodUnmodifiable = TypeName.get(mElements.getTypeElement(Constants.METHOD_TYPE_UNMODIFIABLE).asType());
        mPair = TypeName.get(mElements.getTypeElement(Constants.PAIR_TYPE).asType());
        mPairUnmodifiable = TypeName.get(mElements.getTypeElement(Constants.PAIR_TYPE_UNMODIFIABLE).asType());
        mPath = TypeName.get(mElements.getTypeElement(Constants.PATH_TYPE).asType());
        mPathUnmodifiable = TypeName.get(mElements.getTypeElement(Constants.PATH_TYPE_UNMODIFIABLE).asType());
        mMappings = ParameterizedTypeName.get(ClassName.get(Map.class), mMapping, mHandler);

        mString = TypeName.get(String.class);
        mInt = TypeName.get(int.class);
        mLong = TypeName.get(long.class);
        mFloat = TypeName.get(float.class);
        mDouble = TypeName.get(double.class);
        mBoolean = TypeName.get(boolean.class);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        if (CollectionUtils.isEmpty(set)) return false;

        Map<TypeElement, List<ExecutableElement>> controllers = new HashMap<>();
        findMapping(roundEnv.getElementsAnnotatedWith(RequestMapping.class), controllers);
        findMapping(roundEnv.getElementsAnnotatedWith(GetMapping.class), controllers);
        findMapping(roundEnv.getElementsAnnotatedWith(PostMapping.class), controllers);
        findMapping(roundEnv.getElementsAnnotatedWith(PutMapping.class), controllers);
        findMapping(roundEnv.getElementsAnnotatedWith(PatchMapping.class), controllers);
        findMapping(roundEnv.getElementsAnnotatedWith(DeleteMapping.class), controllers);

        if (!controllers.isEmpty()) createHandlerAdapter(controllers);
        return true;
    }

    private void findMapping(Set<? extends Element> set, Map<TypeElement, List<ExecutableElement>> controllers) {
        for (Element element : set) {
            if (element instanceof ExecutableElement) {
                ExecutableElement execute = (ExecutableElement)element;
                Element enclosing = element.getEnclosingElement();
                if (!(enclosing instanceof TypeElement)) continue;

                TypeElement type = (TypeElement)enclosing;
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

                List<ExecutableElement> elementList = controllers.get(type);
                if (CollectionUtils.isEmpty(elementList)) {
                    elementList = new ArrayList<>();
                    controllers.put(type, elementList);
                }
                elementList.add(execute);
            }
        }
    }

    private void createHandlerAdapter(Map<TypeElement, List<ExecutableElement>> controllers) {
        List<String> adapterList = new ArrayList<>();
        for (Map.Entry<TypeElement, List<ExecutableElement>> entry : controllers.entrySet()) {
            TypeElement type = entry.getKey();
            List<ExecutableElement> executes = entry.getValue();
            mLog.i(String.format("------ Processing %s ------", type.getSimpleName()));

            String typeName = type.getQualifiedName().toString();
            Mapping typeMapping = getTypeMapping(type);
            if (typeMapping != null) {
                validateMapping(typeMapping, typeName);
            } else {
                typeMapping = new Null();
            }

            TypeName controllerType = TypeName.get(type.asType());
            FieldSpec hostField = FieldSpec.builder(controllerType, "mHost", Modifier.PRIVATE).build();
            FieldSpec mappingField = FieldSpec.builder(mMappings, "mMappingMap", Modifier.PRIVATE).build();

            CodeBlock.Builder rootCode = CodeBlock.builder()
                .addStatement("this.mHost = new $T()", type)
                .addStatement("this.mMappingMap = new $T<>()", LinkedHashMap.class);
            for (ExecutableElement execute : executes) {
                Mapping mapping = getExecuteMapping(execute);
                validateExecuteMapping(mapping, typeName + "#" + execute.getSimpleName().toString() + "()");

                mapping = new Merge(typeMapping, mapping);
                rootCode.beginControlFlow("\n").addStatement("$T mapping = new $T()", mMapping, mMapping);
                addMapping(rootCode, mapping);
                rootCode.addStatement("mapping = new $T(mapping)", mMappingUnmodifiable);

                Addition addition = execute.getAnnotation(Addition.class);
                rootCode.add("\n").addStatement("$T addition = new $T()", mAddition, mAddition);
                addAddition(rootCode, addition);

                String handlerName = createHandler(type, execute, mapping.path(), mapping.isRest());
                rootCode.addStatement("$L handler = new $L(mHost, mapping, addition, $L)", handlerName, handlerName,
                    mapping.isRest()).addStatement("mMappingMap.put(mapping, handler)").endControlFlow();
            }
            MethodSpec rootMethod = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addCode(rootCode.build())
                .build();

            MethodSpec mappingMethod = MethodSpec.methodBuilder("getMappingMap")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .returns(mMappings)
                .addStatement("return mMappingMap")
                .build();

            MethodSpec hostMethod = MethodSpec.methodBuilder("getHost")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .returns(controllerType)
                .addStatement("return mHost")
                .build();

            String packageName = MoreElements.getPackage(type).getQualifiedName().toString();
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

            JavaFile javaFile = JavaFile.builder(packageName, adapterClass).build();
            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            adapterList.add(packageName + "." + className);
        }

        if (!adapterList.isEmpty()) createRegister(adapterList);
    }

    private Mapping getTypeMapping(TypeElement type) {
        Mapping mapping = null;
        boolean isRest = type.getAnnotation(ResponseBody.class) != null;
        isRest = isRest || type.getAnnotation(RestController.class) != null;
        RequestMapping requestMapping = type.getAnnotation(RequestMapping.class);
        if (requestMapping != null) mapping = new Any(requestMapping, isRest);

        if (mapping == null) {
            GetMapping getMapping = type.getAnnotation(GetMapping.class);
            if (getMapping != null) mapping = new Get(getMapping, isRest);
        }
        if (mapping == null) {
            PostMapping postMapping = type.getAnnotation(PostMapping.class);
            if (postMapping != null) mapping = new Post(postMapping, isRest);
        }
        if (mapping == null) {
            PutMapping putMapping = type.getAnnotation(PutMapping.class);
            if (putMapping != null) mapping = new Put(putMapping, isRest);
        }
        if (mapping == null) {
            PatchMapping patchMapping = type.getAnnotation(PatchMapping.class);
            if (patchMapping != null) mapping = new Patch(patchMapping, isRest);
        }
        if (mapping == null) {
            DeleteMapping deleteMapping = type.getAnnotation(DeleteMapping.class);
            if (deleteMapping != null) mapping = new Delete(deleteMapping, isRest);
        }
        return mapping;
    }

    private void validateMapping(Mapping mapping, String host) {
        String[] paths = mapping.path();
        if (ArrayUtils.isEmpty(paths)) {
            paths = mapping.value();
        }
        if (ArrayUtils.isNotEmpty(paths)) {
            for (String path : paths) {
                boolean valid = path.matches(PATH_BLURRED);
                valid = valid || path.matches(PATH);
                Validate.isTrue(valid, "The format of path [%s] is wrong on %s.", path, host);
            }
        }

        String[] params = mapping.params();
        if (ArrayUtils.isNotEmpty(params)) {
            for (String param : params) {
                boolean valid = param.matches(PAIR_KEY);
                valid = valid || param.matches(PAIR_KEY_VALUE);
                valid = valid || param.matches(PAIR_NO_KEY);
                valid = valid || param.matches(PAIR_NO_VALUE);
                Validate.isTrue(valid, "The format of param [%s] is wrong on %s.", param, host);
            }
        }

        String[] headers = mapping.headers();
        if (ArrayUtils.isNotEmpty(headers)) {
            for (String head : headers) {
                boolean valid = head.matches(PAIR_KEY);
                valid = valid || head.matches(PAIR_KEY_VALUE);
                valid = valid || head.matches(PAIR_NO_KEY);
                valid = valid || head.matches(PAIR_NO_VALUE);
                Validate.isTrue(valid, "The format of header [%s] is wrong on %s.", head, host);
            }
        }

        String[] consumes = mapping.consumes();
        if (ArrayUtils.isNotEmpty(consumes)) {
            for (String consume : consumes) {
                try {
                    new MimeType(consume);
                } catch (MimeTypeParseException e) {
                    throw new IllegalArgumentException(
                        String.format("The format of consume [%s] is wrong on %s.", consume, host));
                }
            }
        }

        String[] produces = mapping.produces();
        if (ArrayUtils.isNotEmpty(produces)) {
            for (String produce : produces) {
                try {
                    new MimeType(produce);
                } catch (MimeTypeParseException e) {
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
        if (requestMapping != null) mapping = new Any(requestMapping, isRest);

        if (mapping == null) {
            GetMapping getMapping = execute.getAnnotation(GetMapping.class);
            if (getMapping != null) mapping = new Get(getMapping, isRest);
        }
        if (mapping == null) {
            PostMapping postMapping = execute.getAnnotation(PostMapping.class);
            if (postMapping != null) mapping = new Post(postMapping, isRest);
        }
        if (mapping == null) {
            PutMapping putMapping = execute.getAnnotation(PutMapping.class);
            if (putMapping != null) mapping = new Put(putMapping, isRest);
        }
        if (mapping == null) {
            PatchMapping patchMapping = execute.getAnnotation(PatchMapping.class);
            if (patchMapping != null) mapping = new Patch(patchMapping, isRest);
        }
        if (mapping == null) {
            DeleteMapping deleteMapping = execute.getAnnotation(DeleteMapping.class);
            if (deleteMapping != null) mapping = new Delete(deleteMapping, isRest);
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
        builder.add("\n").addStatement("$T path = new $T()", mPath, mPath);
        for (String path : pathArray) {
            builder.addStatement("path.addRule($S)", path);
        }
        builder.addStatement("mapping.setPath(new $T(path))", mPathUnmodifiable);

        String[] methodArray = mapping.method();
        builder.add("\n").addStatement("$T method = new $T()", mMethod, mMethod);
        for (String method : methodArray) {
            builder.addStatement("method.addRule($S)", method);
        }
        builder.addStatement("mapping.setMethod(new $T(method))", mMethodUnmodifiable);

        String[] paramArray = mapping.params();
        if (ArrayUtils.isNotEmpty(paramArray)) {
            builder.add("\n").addStatement("$T param = new $T()", mPair, mPair);
            for (String param : paramArray) {
                builder.addStatement("param.addRule($S)", param);
            }
            builder.addStatement("mapping.setParam(new $T(param))", mPairUnmodifiable);
        }

        String[] headerArray = mapping.headers();
        if (ArrayUtils.isNotEmpty(headerArray)) {
            builder.add("\n").addStatement("$T header = new $T()", mPair, mPair);
            for (String header : headerArray) {
                builder.addStatement("header.addRule($S)", header);
            }
            builder.addStatement("mapping.setHeader(new $T(header))", mPairUnmodifiable);
        }

        String[] consumeArray = mapping.consumes();
        if (ArrayUtils.isNotEmpty(consumeArray)) {
            builder.add("\n").addStatement("$T consume = new $T()", mMimeType, mMimeType);
            for (String consume : consumeArray) {
                builder.addStatement("consume.addRule($S)", consume);
            }
            builder.addStatement("mapping.setConsume(new $T(consume))", mMimeTypeUnmodifiable);
        }

        String[] produceArray = mapping.produces();
        if (ArrayUtils.isNotEmpty(produceArray)) {
            builder.add("\n").addStatement("$T produce = new $T()", mMimeType, mMimeType);
            for (String produce : produceArray) {
                builder.addStatement("produce.addRule($S)", produce);
            }
            builder.addStatement("mapping.setProduce(new $T(produce))", mMimeTypeUnmodifiable);
        }
    }

    private void addAddition(CodeBlock.Builder builder, Addition addition) {
        if (addition == null) return;

        String[] stringType = addition.stringType();
        if (ArrayUtils.isEmpty(stringType)) {
            stringType = addition.value();
        }
        if (ArrayUtils.isNotEmpty(stringType)) {
            StringBuilder array = new StringBuilder();
            for (String type : stringType) {
                if (array.length() > 0) array.append(", ");
                array.append("\"").append(type).append("\"");
            }
            builder.add("\n")
                .addStatement("String[] stringType = new String[]{$L}", array)
                .addStatement("addition.setStringType(stringType)");
        }

        boolean[] booleanType = addition.booleanType();
        if (ArrayUtils.isNotEmpty(booleanType)) {
            StringBuilder array = new StringBuilder();
            for (boolean type : booleanType) {
                if (array.length() > 0) array.append(", ");
                array.append(type);
            }
            builder.add("\n")
                .addStatement("boolean[] booleanType = new boolean[]{$L}", array)
                .addStatement("addition.setBooleanType(booleanType)");
        }

        int[] intType = addition.intTypeType();
        if (ArrayUtils.isNotEmpty(intType)) {
            StringBuilder array = new StringBuilder();
            for (int type : intType) {
                if (array.length() > 0) array.append(", ");
                array.append(type);
            }
            builder.add("\n")
                .addStatement("int[] intType = new int[]{$L}", array)
                .addStatement("addition.setIntType(intType)");
        }

        long[] longType = addition.longType();
        if (ArrayUtils.isNotEmpty(longType)) {
            StringBuilder array = new StringBuilder();
            for (long type : longType) {
                if (array.length() > 0) array.append(", ");
                array.append(type).append("L");
            }
            builder.add("\n")
                .addStatement("long[] longType = new long[]{$L}", array)
                .addStatement("addition.setLongType(longType)");
        }

        short[] shortType = addition.shortType();
        if (ArrayUtils.isNotEmpty(shortType)) {
            StringBuilder array = new StringBuilder();
            for (short type : shortType) {
                if (array.length() > 0) array.append(", ");
                array.append(type);
            }
            builder.add("\n")
                .addStatement("short[] shortType = new short[]{$L}", array)
                .addStatement("addition.setShortType(shortType)");
        }

        float[] floatType = addition.floatType();
        if (ArrayUtils.isNotEmpty(floatType)) {
            StringBuilder array = new StringBuilder();
            for (float type : floatType) {
                if (array.length() > 0) array.append(", ");
                array.append(type).append("F");
            }
            builder.add("\n")
                .addStatement("float[] floatType = new float[]{$L}", array)
                .addStatement("addition.setFloatType(floatType)");
        }

        double[] doubleType = addition.doubleType();
        if (ArrayUtils.isNotEmpty(doubleType)) {
            StringBuilder array = new StringBuilder();
            for (double type : doubleType) {
                if (array.length() > 0) array.append(", ");
                array.append(type).append("D");
            }
            builder.add("\n")
                .addStatement("double[] doubleType = new double[]{$L}", array)
                .addStatement("addition.setDoubleType(doubleType)");
        }

        byte[] byteType = addition.byteType();
        if (ArrayUtils.isNotEmpty(byteType)) {
            StringBuilder array = new StringBuilder();
            for (byte type : byteType) {
                if (array.length() > 0) array.append(", ");
                array.append(type);
            }
            builder.add("\n")
                .addStatement("byte[] byteType = new byte[]{$L}", array)
                .addStatement("addition.setByteType(byteType)");
        }

        char[] charType = addition.charType();
        if (ArrayUtils.isNotEmpty(charType)) {
            StringBuilder array = new StringBuilder();
            for (char type : charType) {
                if (array.length() > 0) array.append(", ");
                array.append("'").append(type).append("'");
            }
            builder.add("\n")
                .addStatement("char[] charType = new char[]{$L}", array)
                .addStatement("addition.setCharType(charType)");
        }
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
            .addParameter(boolean.class, "isRest")
            .addStatement("super(host, mapping, addition, isRest)")
            .addStatement("this.mHost = host")
            .build();

        CodeBlock.Builder handleCode = CodeBlock.builder()
            .addStatement("$T context = $T.getContext()", mContext, mAndServer)
            .addStatement("String httpPath = request.getPath()")
            .addStatement("$T httpMethod = request.getMethod()", mHttpMethod)
            .add("\n")
            .addStatement("Object converterObj = request.getAttribute($T.HTTP_MESSAGE_CONVERTER)", mRequest)
            .addStatement("$T converter = null", mConverter)
            .beginControlFlow("if (converterObj != null && converterObj instanceof $T)", mConverter)
            .addStatement("converter = ($T)converterObj", mConverter)
            .endControlFlow()
            .add("\n")
            .addStatement("$T multiRequest = null", mRequestMultipart)
            .beginControlFlow("if (request instanceof $T)", mRequestMultipart)
            .addStatement("multiRequest = ($T) request", mRequestMultipart)
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
                    if (paramBuild.length() > 0) paramBuild.append(", ");
                    paramBuild.append("context");
                    continue;
                }

                if (mRequest.equals(typeName)) {
                    if (paramBuild.length() > 0) paramBuild.append(", ");
                    paramBuild.append("request");
                    continue;
                }

                if (mResponse.equals(typeName)) {
                    if (paramBuild.length() > 0) paramBuild.append(", ");
                    paramBuild.append("response");
                    continue;
                }

                if (mSession.equals(typeName)) {
                    handleCode.add("\n").addStatement("$T session$L = request.getValidSession()", mSession, i);
                    if (paramBuild.length() > 0) paramBuild.append(", ");
                    paramBuild.append(String.format("session%s", i));
                    continue;
                }

                if (mRequestBody.equals(typeName)) {
                    if (paramBuild.length() > 0) paramBuild.append(", ");
                    paramBuild.append("requestBody");
                    continue;
                }

                RequestHeader requestHeader = parameter.getAnnotation(RequestHeader.class);
                if (requestHeader != null) {
                    Validate.isTrue(isBasicType(typeName),
                        "The RequestHeader annotation only supports [String, int, long, float, double, boolean] on %s.",
                        host);

                    String name = requestHeader.name();
                    if (StringUtils.isEmpty(name)) name = requestHeader.value();
                    Validate.isTrue(!StringUtils.isEmpty(name), "The name of param is null on %s.", host);

                    handleCode.add("\n").addStatement("String header$LStr = request.getHeader($S)", i, name);
                    if (requestHeader.required()) {
                        handleCode.beginControlFlow("if ($T.isEmpty(header$LStr))", mStringUtils, i)
                            .addStatement("throw new $T($S)", mHeaderMissing, name)
                            .endControlFlow();
                    } else {
                        handleCode.beginControlFlow("if ($T.isEmpty(header$LStr))", mStringUtils, i)
                            .addStatement("header$LStr = $S", i, requestHeader.defaultValue())
                            .endControlFlow();
                    }

                    createParameter(handleCode, typeName, "header", i);
                    assignmentParameter(handleCode, typeName, "header", i, "header", i);

                    if (paramBuild.length() > 0) paramBuild.append(", ");
                    paramBuild.append(String.format(Locale.getDefault(), "header%d", i));
                    continue;
                }

                CookieValue cookieValue = parameter.getAnnotation(CookieValue.class);
                if (cookieValue != null) {
                    Validate.isTrue(mString.equals(typeName), "CookieValue can only be used with [String] on %s.",
                        host);

                    String name = cookieValue.name();
                    if (StringUtils.isEmpty(name)) name = cookieValue.value();
                    Validate.notEmpty(name, "The name of cookie is null on %s.", host);

                    handleCode.add("\n").addStatement("String cookie$L = request.getCookieValue($S)", i, name);
                    if (cookieValue.required()) {
                        handleCode.beginControlFlow("if ($T.isEmpty(cookie$L))", mStringUtils, i)
                            .addStatement("throw new $T($S)", mCookieMissing, name)
                            .endControlFlow();
                    }

                    if (paramBuild.length() > 0) paramBuild.append(", ");
                    paramBuild.append(String.format(Locale.getDefault(), "cookie%d", i));
                    continue;
                }

                PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
                if (pathVariable != null) {
                    Validate.isTrue(isBasicType(typeName),
                        "The PathVariable annotation only supports [String, int, long, float, double, boolean] on %s.",
                        host);

                    String name = pathVariable.name();
                    if (StringUtils.isEmpty(name)) name = pathVariable.value();
                    Validate.isTrue(!StringUtils.isEmpty(name), "The name of path is null on %s.", host);

                    boolean isBlurred = false;
                    for (String path : paths) {
                        if (path.matches(PATH_BLURRED) && !path.matches(PATH)) {
                            isBlurred = true;
                        }
                    }
                    Validate.isTrue(isBlurred,
                        "The PathVariable annotation must have a blurred path, for example [/project/{name}]. The " +
                            "error occurred on %s.", host);

                    handleCode.add("\n").addStatement("String path$LStr = pathMap.get($S)", i, name);

                    if (pathVariable.required()) {
                        handleCode.beginControlFlow("if ($T.isEmpty(path$LStr))", mStringUtils, i)
                            .addStatement("throw new $T($S)", mPathMissing, name)
                            .endControlFlow();
                    } else {
                        handleCode.beginControlFlow("if ($T.isEmpty(path$LStr))", mStringUtils, i)
                            .addStatement("path$LStr = $S;", i, pathVariable.defaultValue())
                            .endControlFlow();
                    }

                    createParameter(handleCode, typeName, "path", i);
                    assignmentParameter(handleCode, typeName, "path", i, "path", i);

                    if (paramBuild.length() > 0) paramBuild.append(", ");
                    paramBuild.append(String.format(Locale.getDefault(), "path%d", i));
                    continue;
                }

                QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
                if (queryParam != null) {
                    Validate.isTrue(isBasicType(typeName),
                        "The QueryParam annotation only supports [String, int, long, float, double, " +
                            "boolean] on %s.", host);

                    String name = queryParam.name();
                    if (StringUtils.isEmpty(name)) name = queryParam.value();
                    Validate.isTrue(!StringUtils.isEmpty(name), "The name of param is null on %s.", host);

                    handleCode.add("\n").addStatement("String param$LStr = request.getQuery($S)", i, name);
                    if (queryParam.required()) {
                        handleCode.beginControlFlow("if ($T.isEmpty(param$LStr))", mStringUtils, i)
                            .addStatement("throw new $T($S)", mParamMissing, name)
                            .endControlFlow();
                    } else {
                        handleCode.beginControlFlow("if ($T.isEmpty(param$LStr))", mStringUtils, i)
                            .addStatement("param$LStr = $S", i, queryParam.defaultValue())
                            .endControlFlow();
                    }

                    createParameter(handleCode, typeName, "param", i);
                    assignmentParameter(handleCode, typeName, "param", i, "param", i);

                    if (paramBuild.length() > 0) paramBuild.append(", ");
                    paramBuild.append(String.format(Locale.getDefault(), "param%d", i));
                    continue;
                }

                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                if (requestParam != null) {
                    boolean valid = mMultipart.equals(typeName) || isBasicType(typeName);
                    Validate.isTrue(valid,
                        "The RequestParam annotation only supports [MultipartFile, String, int, long, float, double, " +
                            "boolean] on %s.", host);

                    String name = requestParam.name();
                    if (StringUtils.isEmpty(name)) name = requestParam.value();
                    Validate.isTrue(!StringUtils.isEmpty(name), "The name of param is null on %s.", host);

                    handleCode.add("\n");
                    if (mMultipart.equals(typeName)) {
                        handleCode.addStatement("$T param$L = null", mMultipart, i)
                            .beginControlFlow("if (multiRequest != null)")
                            .addStatement("param$L = multiRequest.getFile($S)", i, name)
                            .endControlFlow();
                        if (requestParam.required()) {
                            handleCode.beginControlFlow("if (param$L == null)", i)
                                .addStatement("throw new $T($S)", mParamMissing, name)
                                .endControlFlow();
                        }
                    } else {
                        handleCode.addStatement("String param$LStr = request.getParameter($S)", i, name);

                        if (requestParam.required()) {
                            handleCode.beginControlFlow("if ($T.isEmpty(param$LStr))", mStringUtils, i)
                                .addStatement("throw new $T($S)", mParamMissing, name)
                                .endControlFlow();
                        } else {
                            handleCode.beginControlFlow("if ($T.isEmpty(param$LStr))", mStringUtils, i)
                                .addStatement("param$LStr = $S", i, requestParam.defaultValue())
                                .endControlFlow();
                        }

                        createParameter(handleCode, typeName, "param", i);
                        assignmentParameter(handleCode, typeName, "param", i, "param", i);
                    }

                    if (paramBuild.length() > 0) paramBuild.append(", ");
                    paramBuild.append(String.format(Locale.getDefault(), "param%d", i));
                    continue;
                }

                FormPart formPart = parameter.getAnnotation(FormPart.class);
                if (formPart != null) {
                    String name = formPart.name();
                    if (StringUtils.isEmpty(name)) name = formPart.value();
                    Validate.isTrue(!StringUtils.isEmpty(name), "The name of param is null on %s.", host);

                    handleCode.add("\n");
                    if (mMultipart.equals(typeName)) {
                        handleCode.addStatement("$T param$L = null", mMultipart, i)
                            .beginControlFlow("if (multiRequest != null)")
                            .addStatement("param$L = multiRequest.getFile($S)", i, name)
                            .endControlFlow();
                        if (formPart.required()) {
                            handleCode.beginControlFlow("if (param$L == null)", i)
                                .addStatement("throw new $T($S)", mParamMissing, name)
                                .endControlFlow();
                        }
                    } else {
                        TypeName wrapperType = ParameterizedTypeName.get(mTypeWrapper, typeName);
                        handleCode.addStatement("$T param$L = null", typeName, i)
                            .addStatement("$T param$LType = new $T(){}.getType()", Type.class, i, wrapperType)
                            .beginControlFlow("if (converter != null && multiRequest != null)")
                            .addStatement("$T param$LFile = multiRequest.getFile($S)", mMultipart, i, name)
                            .beginControlFlow("if (param$LFile != null)", i)
                            .addStatement("$T stream = param$LFile.getStream()", InputStream.class, i)
                            .addStatement("$T mimeType = param$LFile.getContentType()", mMediaType, i)
                            .addStatement("param$L = converter.convert(stream, mimeType, param$LType)", i, i)
                            .endControlFlow()
                            .beginControlFlow("if (param$L == null)", i)
                            .addStatement("String param$LStr = multiRequest.getParameter($S)", i, name)
                            .beginControlFlow("if (!$T.isEmpty(param$LStr))", mStringUtils, i)
                            .addStatement("$T stream = new $T(param$LStr.getBytes())", InputStream.class,
                                ByteArrayInputStream.class, i)
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
                    if (paramBuild.length() > 0) paramBuild.append(", ");
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

                    if (paramBuild.length() > 0) paramBuild.append(", ");
                    paramBuild.append(String.format(Locale.getDefault(), "body%d", i));
                    continue;
                }

                throw new IllegalStateException(
                    String.format("The parameter type [%s] is not supported on %s.", typeName, host));
            }
        }

        String executeName = execute.getSimpleName().toString();
        TypeMirror returnMirror = execute.getReturnType();
        handleCode.add("\n").addStatement("Object o = null", type, executeName).beginControlFlow("try");
        if (!TypeKind.VOID.equals(returnMirror.getKind())) {
            handleCode.addStatement("o = (($T)mHost).$L($L)", type, executeName, paramBuild.toString());
        } else {
            handleCode.addStatement("(($T)mHost).$L($L)", type, executeName, paramBuild.toString());
        }
        handleCode.endControlFlow().beginControlFlow("catch (Throwable e)").addStatement("throw e").endControlFlow();
        handleCode.addStatement("return new $T($L, o)", mViewObject, isRest);

        MethodSpec handleMethod = MethodSpec.methodBuilder("handle")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(mView)
            .addParameter(mRequest, "request")
            .addParameter(mResponse, "response")
            .addException(IOException.class)
            .addCode(handleCode.build())
            .build();


        String packageName = MoreElements.getPackage(type).getQualifiedName().toString();
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
        return mString.equals(typeName) || mInt.equals(typeName) || mLong.equals(typeName) || mFloat.equals(typeName) ||
            mDouble.equals(typeName) || mBoolean.equals(typeName);
    }

    private void createParameter(CodeBlock.Builder builder, TypeName type, Object... obj) {
        if (mString.equals(type)) {
            builder.addStatement("String $L$L = null", obj);
        } else if (mInt.equals(type)) {
            builder.addStatement("int $L$L = 0", obj);
        } else if (mLong.equals(type)) {
            builder.addStatement("long $L$L = 0L", obj);
        } else if (mFloat.equals(type)) {
            builder.addStatement("float $L$L = 0F", obj);
        } else if (mDouble.equals(type)) {
            builder.addStatement("double $L$L = 0D", obj);
        } else if (mBoolean.equals(type)) {
            builder.addStatement("boolean $L$L = false", obj);
        }
    }

    private void assignmentParameter(CodeBlock.Builder builder, TypeName type, Object... obj) {
        builder.beginControlFlow("try");
        if (mString.equals(type)) {
            builder.addStatement("$L$L = $L$LStr", obj);
        } else if (mInt.equals(type)) {
            builder.addStatement("$L$L = Integer.parseInt($L$LStr)", obj);
        } else if (mLong.equals(type)) {
            builder.addStatement("$L$L = Long.parseLong($L$LStr)", obj);
        } else if (mFloat.equals(type)) {
            builder.addStatement("$L$L = Float.parseFloat($L$LStr)", obj);
        } else if (mDouble.equals(type)) {
            builder.addStatement("$L$L = Double.parseDouble($L$LStr)", obj);
        } else if (mBoolean.equals(type)) {
            builder.addStatement("$L$L = Boolean.parseBoolean($L$LStr)", obj);
        }
        builder.endControlFlow()
            .beginControlFlow("catch (Throwable e)")
            .addStatement("throw new $T(e)", mParamError)
            .endControlFlow();
    }

    private void createRegister(List<String> adapterList) {
        TypeName typeName = ParameterizedTypeName.get(ClassName.get(List.class), mAdapter);
        FieldSpec listField = FieldSpec.builder(typeName, "mList", Modifier.PRIVATE).build();

        CodeBlock.Builder rootCode = CodeBlock.builder().addStatement("this.mList = new $T<>()", ArrayList.class);
        for (String adapterName : adapterList) {
            ClassName className = ClassName.bestGuess(adapterName);
            rootCode.addStatement("this.mList.add(new $T())", className);
        }
        MethodSpec rootMethod = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addCode(rootCode.build())
            .build();

        MethodSpec registerMethod = MethodSpec.methodBuilder("onRegister")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(mRegisterType, "register")
            .beginControlFlow("for ($T item : mList)", mAdapter)
            .addStatement("register.addAdapter(item)")
            .endControlFlow()
            .build();

        String packageName = Constants.REGISTER_PACKAGE;
        String className = "AdapterRegister";
        TypeSpec handlerClass = TypeSpec.classBuilder(className)
            .addJavadoc(Constants.DOC_EDIT_WARN)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addSuperinterface(mOnRegisterType)
            .addField(listField)
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

    @Override
    protected void addAnnotation(Set<Class<? extends Annotation>> classSet) {
        classSet.add(RequestMapping.class);
        classSet.add(GetMapping.class);
        classSet.add(PostMapping.class);
        classSet.add(PutMapping.class);
        classSet.add(PatchMapping.class);
        classSet.add(DeleteMapping.class);
    }
}