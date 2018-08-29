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

package ru.progrm_jarvis.reflector.wrapper.fast;

import com.google.common.cache.Cache;
import lombok.*;
import ru.progrm_jarvis.reflector.MethodHandleBuilder;
import ru.progrm_jarvis.reflector.util.Caches;
import ru.progrm_jarvis.reflector.wrapper.AbstractMethodWrapper;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

@Value
@EqualsAndHashCode(callSuper = true)
public class FastMethodWrapper<T, R>
        extends AbstractMethodWrapper<T, R> implements FastReflectorWrapper<Method> {

    /**
     * Cache of method wrappers
     */
    private static final Cache<Method, FastMethodWrapper<?, ?>> CACHE = Caches.weakValuesCache();

    @NonNull private MethodHandle methodHandle;

    protected FastMethodWrapper(@NonNull final Method method, @NonNull final MethodHandle methodHandle) {
        super(method);

        this.methodHandle = methodHandle;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T, R> FastMethodWrapper<T, R> from(@NonNull final Method method) {
        return (FastMethodWrapper<T, R>) CACHE.get(method, () -> new FastMethodWrapper<>(
                method, MethodHandleBuilder.methodHandleFrom(method)
        ));
    }

    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public R invoke(final T instance, final Object... arguments) {
        val length = arguments.length;
        if (length == 0) return (R) methodHandle.invokeWithArguments(instance);
        val argumentsArray = new Object[length + 1];
        argumentsArray[0] = instance;

        int i = 0;
        for (val argument : arguments) argumentsArray[++i] = argument;

        return (R) methodHandle.invokeWithArguments(argumentsArray);
    }

    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public R invokeStatic(final Object... arguments) {
        return (R) methodHandle.invokeWithArguments(arguments);
    }
}
