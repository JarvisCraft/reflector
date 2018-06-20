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
public class RMethod<T, R> implements ReflectorWrapper {

    private static final Cache<Method, RMethod<?, ?>> CACHE = CacheBuilder.newBuilder().weakValues().build();

    /**
     * Actual method wrapped.
     */
    @NonNull private Method method;

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public R invoke(@Nullable final Object object, @NonNull final Object... arguments) {
        return AccessHelper.accessAndGet(method, method -> (R) method.invoke(object, arguments));
    }

    public R invokeStatic(@NonNull final Object... arguments) {
        return invoke(null, arguments);
    }

    @SuppressWarnings("unchecked")
    public static <T, R> RMethod<T, R> of(@NonNull final Method method) {
        try {
            return ((RMethod<T, R>) CACHE.get(method, () -> new RMethod<>(method)));
        } catch (final ExecutionException e) {
            throw new RuntimeException("Could not obtain RMethod<R> value from cache");
        }
    }
}
