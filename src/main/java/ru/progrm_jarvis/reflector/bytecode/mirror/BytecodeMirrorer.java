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

import javassist.*;
import lombok.*;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.reflector.bytecode.mirror.annotation.*;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class BytecodeMirrorer {

    private final String
            MIRRORED_FIELD_ANNOTATION_NAME = MirroredField.class.getTypeName(),
            MIRRORED_METHOD_ANNOTATION_NAME = MirroredMethod.class.getTypeName(),
            MIRRORED_CONSTRUCTOR_ANNOTATION_NAME = MirroredConstructor.class.getTypeName();

    public BytecodeMirroringTask.Builder mirroringTaskBuilderFrom(@Nonnull final BytecodeMirroringTask.Builder builder,
                                                                  @Nonnull final Set<CtClass> delegators,
                                                                  @Nonnull final Set<CtClass> implementations) {
        builder.delegators(delegators);

        for (val implementation : implementations) {
            builder.fields(getFieldMirrorers(implementation));
            builder.methods(getMethodMirrorers(implementation));
            builder.constructors(getConstructorMirrorers(implementation));
            builder.constructors(getClassInitializerMirrorers(implementation));
        }

        return builder;
    }

    public BytecodeMirroringTask.Builder mirroringTaskBuilderFrom(@Nonnull final Set<CtClass> interfaces,
                                                                  @Nonnull final Set<CtClass> implementations) {
        return mirroringTaskBuilderFrom(BytecodeMirroringTask.builder(), interfaces, implementations);
    }

    @SneakyThrows
    public Set<ClassMemberMirrorer<CtField>> getFieldMirrorers(@Nonnull final CtClass ctClass) {
        val mirroringPolicy = getFieldsMirroringPolicy(ctClass);

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
            case ALL: {
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
    public Set<ClassMemberMirrorer<CtMethod>> getMethodMirrorers(@Nonnull final CtClass ctClass) {
        val mirroringPolicy = getMethodsMirroringPolicy(ctClass);

        if (mirroringPolicy == MirroringPolicy.NONE) return Collections.emptySet();

        switch (mirroringPolicy) {
            case VISIBLE: return Arrays.stream(ctClass.getMethods())
                    .map(ClassMemberMirrorer::mirrorerOf)
                    .collect(Collectors.toSet());
            default: case ANNOTATED: return Arrays.stream(ctClass.getDeclaredMethods())
                    .filter(ctMethod -> ctMethod.hasAnnotation(MIRRORED_METHOD_ANNOTATION_NAME))
                    .map(ClassMemberMirrorer::mirrorerOf)
                    .collect(Collectors.toSet());
            case ALL: return Arrays.stream(ctClass.getDeclaredMethods())
                    .map(ClassMemberMirrorer::mirrorerOf)
                    .collect(Collectors.toSet());
        }
    }

    @SneakyThrows
    public Set<ClassMemberMirrorer<CtConstructor>> getConstructorMirrorers(@Nonnull final CtClass ctClass) {
        val mirroringPolicy = getConstructorsMirroringPolicy(ctClass);

        if (mirroringPolicy == MirroringPolicy.NONE) return Collections.emptySet();

        switch (mirroringPolicy) {
            case VISIBLE: return Arrays.stream(ctClass.getConstructors())
                    .filter(CtConstructor::isConstructor)
                    .map(ClassMemberMirrorer::mirrorerOf)
                    .collect(Collectors.toSet());
            default: case ANNOTATED: return Arrays.stream(ctClass.getDeclaredConstructors())
                    .filter(ctConstructor -> ctConstructor.isConstructor()
                            && ctConstructor.hasAnnotation(MIRRORED_CONSTRUCTOR_ANNOTATION_NAME))
                    .map(ClassMemberMirrorer::mirrorerOf)
                    .collect(Collectors.toSet());
            case ALL: return Arrays.stream(ctClass.getDeclaredConstructors())
                    .filter(CtConstructor::isConstructor)
                    .map(ClassMemberMirrorer::mirrorerOf)
                    .collect(Collectors.toSet());
        }
    }

    @SneakyThrows
    public Set<ClassMemberMirrorer<CtConstructor>> getClassInitializerMirrorers(@Nonnull final CtClass ctClass) {
        if (isMirrorClassInitializers(ctClass)) return Arrays
                .stream(ctClass.getDeclaredConstructors())
                .filter(CtConstructor::isClassInitializer)
                .map(ClassMemberMirrorer::mirrorerOf)
                .collect(Collectors.toSet());
        return Collections.emptySet();
    }

    @NotNull
    public MirroringPolicy getFieldsMirroringPolicy(@Nonnull final CtClass ctClass)
            throws ClassNotFoundException {
        @Nullable val mirrorFields = (MirrorFields) ctClass.getAnnotation(MirrorFields.class);
        if (mirrorFields == null) {
            val mirrorAll = (MirrorAll) ctClass.getAnnotation(MirrorAll.class);

            return mirrorAll == null ? MirroringPolicy.ANNOTATED : mirrorAll.value();
        }

        return mirrorFields.value();
    }

    @NotNull
    public MirroringPolicy getMethodsMirroringPolicy(@Nonnull final CtClass ctClass)
            throws ClassNotFoundException {
        @Nullable val mirrorMethods = (MirrorMethods) ctClass.getAnnotation(MirrorMethods.class);
        if (mirrorMethods == null) {
            val mirrorAll = (MirrorAll) ctClass.getAnnotation(MirrorAll.class);

            return mirrorAll == null ? MirroringPolicy.ANNOTATED : mirrorAll.value();
        }

        return mirrorMethods.value();
    }

    @NotNull
    public MirroringPolicy getConstructorsMirroringPolicy(@Nonnull final CtClass ctClass)
            throws ClassNotFoundException {
        @Nullable val mirrorConstructors = (MirrorConstructors) ctClass.getAnnotation(MirrorConstructors.class);
        if (mirrorConstructors == null) {
            val mirrorAll = (MirrorAll) ctClass.getAnnotation(MirrorAll.class);

            return mirrorAll == null ? MirroringPolicy.ANNOTATED : mirrorAll.value();
        }

        return mirrorConstructors.value();
    }

    public boolean isMirrorClassInitializers(@Nonnull final CtClass ctClass) throws ClassNotFoundException {
        val mirrorClassInitializers = (MirrorClassInitializers) ctClass.getAnnotation(MirrorClassInitializers.class);

        return mirrorClassInitializers != null && mirrorClassInitializers.value();
    }
}
