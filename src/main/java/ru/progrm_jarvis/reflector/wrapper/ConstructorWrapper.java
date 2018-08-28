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
import ru.progrm_jarvis.reflector.util.emptyconstructor.ClassGenerator;
import ru.progrm_jarvis.reflector.util.emptyconstructor.EmptyConstructorCreator;

/**
 * Wrapper for {@link Constructor} to be used with Reflector
 *
 * @param <T> type of class whose constructor it is
 */
//@Value
@RequiredArgsConstructor
public class ConstructorWrapper<T> implements ReflectorWrapper {

    /**
     * Cache of constructor wrappers
     */
    private static final Cache<Constructor<?>, ConstructorWrapper<?>> CACHE = CacheBuilder
            .newBuilder().weakValues().build();

    /**
     * Actual constructor wrapped.
     */
    @NonNull
    private final Constructor<T> constructor;

    /**
     * Uses for creating new instance without invoking constructor.
     */
    private EmptyConstructorCreator emptyConstructorCreator;

    /**
     * Creates new constructor wrapper instance for the field given or gets it from cache if one
     * already exists.
     *
     * @param constructor constructor to get wrapped
     * @param <T> type of object which's constructor is invoked
     * @return constructor wrapper created or got from cache
     */
    @SuppressWarnings("unchecked")
    public static <T> ConstructorWrapper<T> of(@NonNull final Constructor<T> constructor) {
        try {
            return ((ConstructorWrapper<T>) CACHE
                    .get(constructor, () -> new ConstructorWrapper<>(constructor)));
        } catch (final ExecutionException e) {
            throw new RuntimeException("Could not obtain ConstructorWrapper<T> value from cache");
        }
    }

    /**
     * Creates new instance of this object by invoking it's constructor wrapped ignoring any
     * limitations if possible.
     *
     * @param arguments arguments to be passed to constructor
     * @return object instantiated using constructor
     */
    @SneakyThrows
    public T construct(@NonNull final Object... arguments) {
        return AccessHelper
                .accessAndGet(constructor, constructor -> constructor.newInstance(arguments));
    }

    @SneakyThrows
    public T constructUsingMagic(@NonNull ClassGenerator classGenerator) {
        if (this.emptyConstructorCreator == null) {
            this.emptyConstructorCreator = classGenerator
                    .newClassInstanceManipulator(constructor.getDeclaringClass());
        }
        return (T) this.emptyConstructorCreator.newInstance();
    }
}
