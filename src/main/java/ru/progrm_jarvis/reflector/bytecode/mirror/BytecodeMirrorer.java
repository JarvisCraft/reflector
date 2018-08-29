/*
 *  Copyright 2018 Petr P.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.progrm_jarvis.reflector.bytecode.mirror;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import ru.progrm_jarvis.reflector.bytecode.BytecodeHelper;
import ru.progrm_jarvis.reflector.bytecode.mirror.annotation.*;
import ru.progrm_jarvis.reflector.util.ThrowableUtil;
import ru.progrm_jarvis.reflector.wrapper.FieldWrapper;
import ru.progrm_jarvis.reflector.wrapper.fast.FastFieldWrapper;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BytecodeMirrorer {

    @NonNull private BytecodeHelper bytecodeHelper;

    private static final String
            MIRRORED_FIELD_ANNOTATION_NAME = MirroredField.class.getTypeName(),
            MIRRORED_METHOD_ANNOTATION_NAME = MirroredMethod.class.getTypeName(),
            MIRRORED_CONSTRUCTOR_ANNOTATION_NAME = MirroredConstructor.class.getTypeName();

    public static BytecodeMirrorer create(@NonNull final BytecodeHelper bytecodeHelper) {
        return new BytecodeMirrorer(bytecodeHelper);
    }

    public static BytecodeMirrorer create(@NonNull final ClassPool classPool) {
        return create(BytecodeHelper.create(classPool));
    }

    public static BytecodeMirrorer create() {
        return create(BytecodeHelper.create());
    }

    public BytecodeMirroringTask.Builder mirroringTaskBuilderFrom(@Nonnull final Set<CtClass> interfaces,
                                                                  @Nonnull final Set<CtClass> implementations) {
        val builder = BytecodeMirroringTask.builder()
                .interfaces(interfaces);

        for (val implementation : implementations) {
            builder.fields(getFieldMirrorers(implementation, interfaces));
            builder.methods(getMethodMirrorers(implementation, interfaces));
            // TODO: 28.08.2018
        }

        return builder;
    }


    private static final FieldWrapper<String, char[]> STRING_CLASS_VALUE_FIELD
            = FastFieldWrapper.from(ThrowableUtil.executeChecked(() -> String.class.getDeclaredField("value")));

    static {
        System.out.println(STRING_CLASS_VALUE_FIELD.getValue("foo"));
        STRING_CLASS_VALUE_FIELD.setValue("bar", new char[]{'l', 'o', 'x'});
        System.out.println(STRING_CLASS_VALUE_FIELD.updateValue("one", value -> Arrays.copyOf(value, 1)));// prev
        System.out.println(STRING_CLASS_VALUE_FIELD.computeValue("two", value -> Arrays.copyOf(value, 1)));// new
    }

    @SneakyThrows
    public Set<ClassMemberMirrorer<CtField>> getFieldMirrorers(@Nonnull final CtClass ctClass,
                                                               @Nonnull final Set<CtClass> interfaces) {
        final MirroringPolicy mirroringPolicy;
        {
            val mirrorFields = (MirrorFields) ctClass.getAnnotation(MirrorFields.class);
            mirroringPolicy = mirrorFields == null ? MirroringPolicy.ANNOTATED : mirrorFields.value();
        }

        if (mirroringPolicy == MirroringPolicy.NONE) return Collections.emptySet();

        final CtField[] fields;
        switch (mirroringPolicy) {
            case VISIBLE: {
                fields = ctClass.getFields();

                break;
            }
            default: case ANNOTATED: {
                fields = Arrays.stream(ctClass.getDeclaredFields())
                        .filter(field -> field.hasAnnotation(MIRRORED_FIELD_ANNOTATION_NAME))
                        .toArray(CtField[]::new);

                break;
            }
            case IMPLEMENTING: case ALL: {
                // Maybe later IMPLEMENTING should filter like [public | isUsedInImplMethods]
                fields = ctClass.getDeclaredFields();

                break;
            }
        }

        return Arrays.stream(fields)
                .map(field -> {
                    final MirroredField mirroredField;
                    try {
                        mirroredField = (MirroredField) field.getAnnotation(MirroredField.class);
                    } catch (final ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    return mirroredField == null
                            ? ClassMemberMirrorer.mirrorerOf(field)
                            : ClassMemberMirrorer.mirrorerOf(field, mirroredField.value());
                })
                .collect(Collectors.toSet());
    }

    @SneakyThrows
    public Set<ClassMemberMirrorer<CtMethod>> getMethodMirrorers(@Nonnull final CtClass ctClass,
                                                                 @Nonnull final Set<CtClass> interfaces) {
        final MirroringPolicy mirroringPolicy;
        {
            val mirrorFields = (MirrorMethods) ctClass.getAnnotation(MirrorFields.class);
            mirroringPolicy = mirrorFields == null ? MirroringPolicy.ANNOTATED : mirrorFields.value();
        }

        if (mirroringPolicy == MirroringPolicy.NONE) return Collections.emptySet();

        final CtMethod[] methods;
        switch (mirroringPolicy) {
            case VISIBLE: return Arrays.stream(ctClass.getMethods())
                    .map(ClassMemberMirrorer::mirrorerOf)
                    .collect(Collectors.toSet());
            default: case ANNOTATED: return Arrays.stream(ctClass.getDeclaredMethods())
                    .filter(ctField -> ctField.hasAnnotation(MIRRORED_METHOD_ANNOTATION_NAME))
                    .map(ClassMemberMirrorer::mirrorerOf)
                    .collect(Collectors.toSet());
            case IMPLEMENTING: return Arrays.stream(ctClass.getMethods())
                    // getMethods() is used because implemented interface methods are always public
                    .filter(method -> {

                        val declaringClass = method.getDeclaringClass();
                        for (val anInterface : interfaces) ;
                        return true;
                    })
                    .map(ClassMemberMirrorer::mirrorerOf)
                    .collect(Collectors.toSet());
            case ALL: return Arrays.stream(ctClass.getDeclaredMethods())
                    .map(ClassMemberMirrorer::mirrorerOf)
                    .collect(Collectors.toSet());
        }
    }
}
