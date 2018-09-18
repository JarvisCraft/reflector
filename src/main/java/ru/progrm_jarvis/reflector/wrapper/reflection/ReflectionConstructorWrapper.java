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

package ru.progrm_jarvis.reflector.wrapper.reflection;

import com.google.common.cache.Cache;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import ru.progrm_jarvis.reflector.AccessHelper;
import ru.progrm_jarvis.reflector.util.Caches;
import ru.progrm_jarvis.reflector.wrapper.AbstractConstructorWrapper;
import ru.progrm_jarvis.reflector.wrapper.ConstructorWrapper;

import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutionException;

@Value
@EqualsAndHashCode(callSuper = true)
public class ReflectionConstructorWrapper<T>
        extends AbstractConstructorWrapper<T> implements ReflectionReflectorWrapper<Constructor<T>> {

    /**
     * Cache of constructor wrappers
     */
    private static final Cache<Constructor<?>, ReflectionConstructorWrapper<?>> CACHE = Caches.weakValuesCache();

    protected ReflectionConstructorWrapper(@NonNull final Constructor<T> constructor) {
        super(constructor);
    }

    /**
     * Creates new constructor wrapper instance for the field given or gets it from cache if one already exists.
     *
     * @param constructor constructor to get wrapped
     * @param <T> type of object which's constructor is invoked
     * @return constructor wrapper created or got from cache
     */
    @SuppressWarnings("unchecked")
    public static <T> ConstructorWrapper<T> from(@NonNull final Constructor<T> constructor) {
        try {
            return (ConstructorWrapper<T>) CACHE
                    .get(constructor, () -> new ReflectionConstructorWrapper<>(constructor));
        } catch (final ExecutionException e) {
            throw new RuntimeException("Could not obtain ConstructorWrapper<T> value methodHandleFrom cache");
        }
    }

    @Override
    @SneakyThrows
    public T construct(@NonNull final Object... arguments) {
        return AccessHelper.accessAndGet(constructor, constructor -> constructor.newInstance(arguments));
    }
}
