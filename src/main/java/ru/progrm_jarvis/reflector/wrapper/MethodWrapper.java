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

package ru.progrm_jarvis.reflector.wrapper;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.*;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.reflector.AccessHelper;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

/**
 * Wrapper for {@link Method} to be used with Reflector.
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodWrapper<T, R> implements ReflectorWrapper {

    private static final Cache<Method, MethodWrapper<?, ?>> CACHE = CacheBuilder.newBuilder().weakValues().build();

    /**
     * Actual method wrapped.
     */
    @NonNull private Method method;

    /**
     * Creates new method wrapper instance for the field given or gets it from cache if one already exists.
     *
     * @param method method to get wrapped
     * @param <T> type containing this method
     * @param <R> method return type
     * @return method wrapper created or got from cache
     */
    @SuppressWarnings("unchecked")
    public static <T, R> MethodWrapper<T, R> of(@NonNull final Method method) {
        try {
            return ((MethodWrapper<T, R>) CACHE.get(method, () -> new MethodWrapper<>(method)));
        } catch (final ExecutionException e) {
            throw new RuntimeException("Could not obtain MethodWrapper<R> value from cache");
        }
    }

    /**
     * Invokes this method ignoring any limitations if possible.
     *
     * @param instance instance from which to declare method
     * @param arguments arguments to be passed to this method
     * @return value returned by method
     * @throws NullPointerException if {@code object} is {@code null} but this field is not static
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public R invoke(@Nullable final T instance, @NonNull final Object... arguments) {
        return AccessHelper.accessAndGet(method, method -> (R) method.invoke(instance, arguments));
    }

    /**
     * Invokes this method ignoring on no instance (which means that static method is to be invoked)
     * ignoring any limitations if possible.
     *
     * @param arguments arguments to be passed to this method
     * @return value returned by method
     * @throws NullPointerException if this field is not {@code static}
     */
    public R invokeStatic(@NonNull final Object... arguments) {
        return invoke(null, arguments);
    }
}
