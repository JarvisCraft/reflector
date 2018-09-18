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
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.reflector.AccessHelper;
import ru.progrm_jarvis.reflector.util.Caches;
import ru.progrm_jarvis.reflector.wrapper.AbstractMethodWrapper;
import ru.progrm_jarvis.reflector.wrapper.MethodWrapper;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

@Value
@EqualsAndHashCode(callSuper = true)
public class ReflectionMethodWrapper<T, R>
        extends AbstractMethodWrapper<T, R> implements ReflectionReflectorWrapper<Method> {

    /**
     * Cache of method wrappers
     */
    private static final Cache<Method, ReflectionMethodWrapper<?, ?>> CACHE = Caches.weakValuesCache();

    protected ReflectionMethodWrapper(@NonNull final Method method) {
        super(method);
    }

    /**
     * Creates new method wrapper instance for the field given or gets it from cache if one already exists.
     *
     * @param method method to get wrapped
     * @param <T> type containing this method
     * @param <R> method return type
     * @return method wrapper created or got from cache
     */
    @SuppressWarnings("unchecked")
    public static <T, R> MethodWrapper<T, R> from(@NonNull final Method method) {
        try {
            return ((MethodWrapper<T, R>) CACHE
                    .get(method, () -> new ReflectionMethodWrapper<>(method)));
        } catch (final ExecutionException e) {
            throw new RuntimeException("Could not obtain MethodWrapper<R> value methodHandleFrom cache");
        }
    }

    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public R invoke(@Nullable final T instance, @NonNull final Object... arguments) {
        return AccessHelper.accessAndGet(method, method -> (R) method.invoke(instance, arguments));
    }

    @Override
    public R invokeStatic(@NonNull final Object... arguments) {
        return invoke(null, arguments);
    }
}
