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
import ru.progrm_jarvis.reflector.AccessHelper;

import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutionException;

/**
 * Wrapper for {@link Constructor} to be used with Reflector.
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RConstructor<T> implements ReflectorWrapper {

    private static final Cache<Constructor<?>, RConstructor<?>> CACHE = CacheBuilder.newBuilder().weakValues().build();

    /**
     * Actual constructor wrapped.
     */
    @NonNull private Constructor<T> constructor;

    @SneakyThrows
    public T construct(@NonNull final Object... arguments) {
        return AccessHelper.accessAndGet(constructor, constructor -> constructor.newInstance(arguments));
    }

    @SuppressWarnings("unchecked")
    public static <T> RConstructor<T> of(@NonNull final Constructor<T> constructor) {
        try {
            return ((RConstructor<T>) CACHE.get(constructor, () -> new RConstructor<>(constructor)));
        } catch (final ExecutionException e) {
            throw new RuntimeException("Could not obtain RConstructor<T> value from cache");
        }
    }
}
