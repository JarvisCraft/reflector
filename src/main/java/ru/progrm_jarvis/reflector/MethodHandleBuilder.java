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

package ru.progrm_jarvis.reflector;

import lombok.*;
import lombok.experimental.Accessors;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Builder based on {@link MethodHandle}
 *
 * @param <T> type of class whose methods are accessed
 * @param <R> type of returned value
 */
@Data
@Accessors(chain = true, fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MethodHandleBuilder<T, R> {

    private static final MethodHandles.Lookup PUBLIC_LOOKUP = MethodHandles.publicLookup(),
            LOOKUP = MethodHandles.lookup();

    @NonNull protected Class<T> reference;
    @NonNull protected MethodHandles.Lookup lookup = LOOKUP;
    @NonNull protected Mode mode = Mode.NORMAL;

    public static <T, R> MethodHandleBuilder<T, R> create() {
        return new MethodHandleBuilder<>();
    }

    @SneakyThrows
    public static MethodHandle methodHandleFrom(@NonNull final Method method) {
        return AccessHelper.operateAndGet(method, LOOKUP::unreflect);
    }

    @SneakyThrows
    public static MethodHandle methodHandleFrom(@NonNull final Constructor constructor) {
        return AccessHelper.operateAndGet(constructor, LOOKUP::unreflectConstructor);
    }

    @SneakyThrows
    public static MethodHandle methodHandleFromGetter(@NonNull final Field field) {
        return AccessHelper.operateAndGet(field, LOOKUP::unreflectGetter);
    }

    @SneakyThrows
    public static MethodHandle methodHandleFromSetter(@NonNull final Field field) {
        return AccessHelper.operateAndGet(field, LOOKUP::unreflectSetter);
    }

    @SneakyThrows
    public static MethodHandle methodHandleFromSpecial(@NonNull final Method method,
                                                       @NonNull final Class<?> specialCaller) {
        return AccessHelper.operateAndGet(method, m -> LOOKUP.unreflectSpecial(m, specialCaller));
    }

    @SuppressWarnings("unchecked")
    public MethodHandleBuilder<T, R> reference(@NonNull final Class<T> reference) {
        this.reference = reference;

        return this;
    }

    @SuppressWarnings("unchecked")
    public MethodHandleBuilder<T, R> lookup(@NonNull final MethodHandles.Lookup lookup) {
        this.lookup = lookup;

        return this;
    }

    @SuppressWarnings("unchecked")
    public MethodHandleBuilder<T, R> mode(@NonNull final Mode mode) {
        this.mode = mode;

        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Lookup
    ///////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    public MethodHandleBuilder<T, R> fullLookup() {
        lookup = LOOKUP;

        return this;
    }

    @SuppressWarnings("unchecked")
    public MethodHandleBuilder<T, R> publicLoopkup() {
        lookup = PUBLIC_LOOKUP;

        return this;
    }

    private boolean isStatic;
    @NonNull private String name;
    @NonNull private MethodType methodType;

    @SuppressWarnings("unchecked")
    public MethodHandleBuilder<T, R> returnType(@NonNull final Class<R> returnType) {
        methodType = methodType == null ?
                methodType = MethodType.methodType(returnType) : methodType.changeReturnType(returnType);

        return this;
    }

    @SuppressWarnings("unchecked")
    public Class<R> returnType() {
        return methodType == null ? null : (Class<R>) methodType.returnType();
    }

    @SuppressWarnings("unchecked")
    public MethodHandleBuilder<T, R> parameterTypes(@NonNull final Class<?>[] parameterTypes) {
        methodType = MethodType
                .methodType(methodType == null ? Object.class : methodType.returnType(), parameterTypes);

        return this;
    }

    public Class<?>[] parameterTypes() {
        return methodType == null ? new Class[0] : methodType.parameterArray();
    }

    protected void assertParameters() {
        Objects.requireNonNull(reference, "reference is null");
        Objects.requireNonNull(lookup, "lookup is null");
        Objects.requireNonNull(mode, "mode is null");
        Objects.requireNonNull(name, "name is null");
        Objects.requireNonNull(methodType, "methodType is null");
    }

    private MethodType getMethodType() {
        return Objects.requireNonNull(methodType, "methodType is null");
    }

    @SneakyThrows
    public MethodHandle build() {
        assertParameters();

        return isStatic
                ? lookup.findStatic(reference, name, getMethodType())
                : lookup.findVirtual(reference, name, getMethodType());
    }

    enum Mode implements MethodHandleInvoker {
        EXACT {
            @Override
            public Object apply(final MethodHandle methodHandle, final Object... arguments) throws Throwable {
                return methodHandle.invokeExact(arguments);
            }
        },
        NORMAL {
            @Override
            public Object apply(final MethodHandle methodHandle, final Object... arguments) throws Throwable {
                return methodHandle.invoke(arguments);
            }
        },
        ARGUMENTS {
            @Override
            public Object apply(final MethodHandle methodHandle, final Object... arguments) throws Throwable {
                return methodHandle.invokeWithArguments(arguments);
            }
        }
    }

    @FunctionalInterface
    public interface MethodHandleInvoker {

        Object apply(MethodHandle methodHandle, Object... arguments) throws Throwable;
    }
}
