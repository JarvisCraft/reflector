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

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import ru.progrm_jarvis.reflector.util.Possible;
import ru.progrm_jarvis.reflector.util.ThrowingFunction;
import ru.progrm_jarvis.reflector.wrapper.RConstructor;
import ru.progrm_jarvis.reflector.wrapper.RField;
import ru.progrm_jarvis.reflector.wrapper.RMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Main reflector class which wraps {@link Class} methods with methods which don't throw checked exceptions.
 */
@UtilityClass
@SuppressWarnings({"unused", "WeakerAccess"})
public class Reflector {

    ///////////////////////////////////////////////////////////////////////////
    // Unwrapped
    ///////////////////////////////////////////////////////////////////////////

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> Optional<ClassMember<? super T, Constructor<? super T>>> digForConstructor(
            @NonNull final Class<T> clazz,
            @NonNull final Predicate<Constructor<? super T>> predicate,
            @NonNull final Class<? super T> bound
    ) {
        return RecursiveClassDigger
                .dig(clazz, (ThrowingFunction<Class<? super T>, Possible<Constructor<? super T>>, Throwable>) owner -> {
                        for (val constructor : owner.getDeclaredConstructors()) {
                            val genericConstructor = (Constructor<? super T>) constructor;

                            if (predicate.test(genericConstructor)) return Possible.of(genericConstructor);
                        }
                    return null;
                }, bound);
    }

    @SneakyThrows
    public <T> Optional<ClassMember<? super T, Field>> digForField(
            @NonNull final Class<T> clazz,
            @NonNull final Predicate<Field> predicate,
            @NonNull final Class<? super T> bound
    ) {
        return RecursiveClassDigger
                .dig(clazz, (ThrowingFunction<Class<? super T>, Possible<Field>, Throwable>) owner -> {
                    for (val field : owner.getDeclaredFields()) if (predicate.test(field)) return Possible.of(field);
                    return null;
                    }, bound);
    }

    @SneakyThrows
    public Optional<ClassMember<?, Method>> digForMethod(
            @NonNull final Class<?> clazz,
            @NonNull final Predicate<Method> predicate,
            @NonNull final Class<?> bound
    ) {
        return RecursiveClassDigger
                .digWithInterfaces(clazz, (ThrowingFunction<Class<?>, Possible<Method>, Throwable>) owner -> {
                    for (val method : owner.getDeclaredMethods()) if (predicate.test(method)) return Possible
                            .of(method);
                    return null;
                    }, bound);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Class
    ///////////////////////////////////////////////////////////////////////////

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> Class<T> classForName(@NonNull final String className) {
        return (Class<T>) Class.forName(className);
    }

    @SuppressWarnings("unchecked")
    public <T> Class<? extends T> classOf(@NonNull final T object) {
        return (Class<? extends T>) object.getClass();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Wrapped field
    ///////////////////////////////////////////////////////////////////////////

    @SneakyThrows
    public <T> RField<T> getField(@NonNull final Class<?> clazz, @NonNull final String name) {
        return RField.of(clazz.getField(name));
    }

    public <T> RField<T> getField(@NonNull final Object object, @NonNull final String name) {
        return getField(classOf(object), name);
    }

    @SneakyThrows
    public <T> RField<T> getDeclaredField(@NonNull final Class<?> clazz, @NonNull final String name) {
        return RField.of(clazz.getDeclaredField(name));
    }

    public <T> RField<T> getDeclaredField(@NonNull final Object object, @NonNull final String name) {
        return getDeclaredField(classOf(object), name);
    }

    public <T> Optional<RField<T>> getFieldOptional(@NonNull final Class<?> clazz, @NonNull final Predicate<Field> predicate) {
        return digForField(clazz, predicate, Object.class).map(member -> RField.of(member.getValue()));
    }

    public <T> Optional<RField<T>> getFieldOptional(@NonNull final Object object, @NonNull final Predicate<Field> predicate) {
        return getFieldOptional(classOf(object), predicate);
    }

    @SuppressWarnings("ConstantConditions")
    public <T> RField<T> getField(@NonNull final Class<?> clazz, @NonNull final Predicate<Field> predicate) {
        return RField.of(digForField(clazz, predicate, Object.class).get().getValue());
    }

    public <T> RField<T> getField(@NonNull final Object object, @NonNull final Predicate<Field> predicate) {
        return getField(classOf(object), predicate);
    }

    public <T> Optional<RField<T>> getAnyFieldOptional(@NonNull final Class<?> clazz, @NonNull final String name) {
        return getFieldOptional(clazz, field -> name.equals(field.getName()));
    }

    public <T> Optional<RField<T>> getAnyFieldOptional(@NonNull final Object object, @NonNull final String name) {
        return getAnyFieldOptional(classOf(object), name);
    }

    public <T> RField<T> getAnyField(@NonNull final Class<?> clazz, @NonNull final String name) {
        return getField(clazz, field -> name.equals(field.getName()));
    }

    public <T> RField<T> getAnyField(@NonNull final Object object, @NonNull final String name) {
        return getAnyField(classOf(object), name);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Wrapped field
    ///////////////////////////////////////////////////////////////////////////

    @SneakyThrows
    public <T> RMethod<T> getMethod(@NonNull final Class<?> clazz, @NonNull final String name) {
        return RMethod.of(clazz.getMethod(name));
    }

    public <T> RMethod<T> getMethod(@NonNull final Object object, @NonNull final String name) {
        return getMethod(classOf(object), name);
    }

    @SneakyThrows
    public <T> RMethod<T> getDeclaredMethod(@NonNull final Class<?> clazz, @NonNull final String name) {
        return RMethod.of(clazz.getDeclaredMethod(name));
    }

    public <T> RMethod<T> getDeclaredMethod(@NonNull final Object object, @NonNull final String name) {
        return getDeclaredMethod(classOf(object), name);
    }

    public <T> Optional<RMethod<T>> getMethodOptional(@NonNull final Class<?> clazz,
                                                      @NonNull final Predicate<Method> predicate) {
        return digForMethod(clazz, predicate, Object.class).map(member -> RMethod.of(member.getValue()));
    }

    public <T> Optional<RMethod<T>> getMethodOptional(@NonNull final Object object,
                                                      @NonNull final Predicate<Method> predicate) {
        return getMethodOptional(classOf(object), predicate);
    }

    @SuppressWarnings("ConstantConditions")
    public <T> RMethod<T> getMethod(@NonNull final Class<?> clazz, @NonNull final Predicate<Method> predicate) {
        return RMethod.of(digForMethod(clazz, predicate, Object.class).get().getValue());
    }

    public <T> RMethod<T> getMethod(@NonNull final Object object, @NonNull final Predicate<Method> predicate) {
        return getMethod(classOf(object), predicate);
    }

    public <T> Optional<RMethod<T>> getAnyMethodOptional(@NonNull final Class<?> clazz, @NonNull final String name) {
        return getMethodOptional(clazz, field -> name.equals(field.getName()));
    }

    public <T> Optional<RMethod<T>> getAnyMethodOptional(@NonNull final Object object, @NonNull final String name) {
        return getAnyMethodOptional(classOf(object), name);
    }

    public <T> RMethod<T> getAnyMethod(@NonNull final Class<?> clazz, @NonNull final String name) {
        return getMethod(clazz, field -> name.equals(field.getName()));
    }

    public <T> RMethod<T> getAnyMethod(@NonNull final Object object, @NonNull final String name) {
        return getAnyMethod(classOf(object), name);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Wrapped constructors
    ///////////////////////////////////////////////////////////////////////////

    @SneakyThrows
    public <T> RConstructor<? extends T> getConstructor(@NonNull final Class<T> clazz,
                                                        @NonNull final Class<?>... parameterTypes) {
        return RConstructor.of(clazz.getConstructor(parameterTypes));
    }

    public <T> RConstructor<? extends T> getConstructor(@NonNull final T object,
                                                        @NonNull final Class<?>... parameterTypes) {
        return getConstructor(classOf(object), parameterTypes);
    }

    @SneakyThrows
    public <T> RConstructor<? extends T> getDeclaredConstructor(@NonNull final Class<T> clazz,
                                                                @NonNull final Class<?>... parameterTypes) {
        return RConstructor.of(clazz.getDeclaredConstructor(parameterTypes));
    }

    public <T> RConstructor<? extends T> getDeclaredConstructor(@NonNull final T object,
                                                                @NonNull final Class<?>... parameterTypes) {
        return getDeclaredConstructor(classOf(object), parameterTypes);
    }

    public <T> Optional<RConstructor<? super T>> getConstructorOptional(@NonNull final Class<T> clazz,
                                                                        @NonNull final Predicate<Constructor<? super T>>
                                                                        predicate) {
        return digForConstructor(clazz, predicate, Object.class).map(member -> RConstructor.of(member.getValue()));
    }

    public <T> Optional<RConstructor<? super T>> getConstructorOptional(@NonNull final T object,
                                                                        @NonNull final Predicate<Constructor<? super T>>
                                                                        predicate) {
        return getConstructorOptional(classOf(object), predicate);
    }

    @SuppressWarnings("ConstantConditions")
    public <T> RConstructor<? super T> getConstructor(@NonNull final Class<T> clazz,
                                                      @NonNull final Predicate<Constructor<? super T>> predicate) {
        return RConstructor.of(digForConstructor(clazz, predicate, Object.class).get().getValue());
    }

    public <T> RConstructor<? super T> getConstructor(@NonNull final T object,
                                                      @NonNull final Predicate<Constructor<? super T>> predicate) {
        return getConstructor(classOf(object), predicate);
    }

    public <T> Optional<RConstructor<? super T>> getAnyConstructorOptional(@NonNull final Class<T> clazz,
                                                                           @NonNull final Class<?>... parameterTypes) {
        return getConstructorOptional(clazz, (Predicate<Constructor<? super T>>)
                constructor -> Arrays.equals(parameterTypes, constructor.getParameterTypes()));
    }

    public <T> Optional<RConstructor<? super T>> getAnyConstructorOptional(@NonNull final T object,
                                                                           @NonNull final Class<?>... parameterTypes) {
        return getAnyConstructorOptional(classOf(object), parameterTypes);
    }

    public <T> RConstructor<? super T> getAnyConstructor(@NonNull final Class<T> clazz,
                                                         @NonNull final Class<?>... parameterTypes) {
        return getConstructor(clazz, (Predicate<Constructor<? super T>>)
                constructor -> Arrays.equals(parameterTypes, constructor.getParameterTypes()));
    }

    public <T> RConstructor<? super T> getAnyConstructor(@NonNull final T object,
                                                         @NonNull final Class<?>... parameterTypes) {
        return getAnyConstructor(classOf(object), parameterTypes);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Unsafe
    ///////////////////////////////////////////////////////////////////////////

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> T newUnsafeInstance(@NonNull final Class<T> clazz) {
        return (T) UnsafeUtil.UNSAFE.allocateInstance(clazz);
    }
}
