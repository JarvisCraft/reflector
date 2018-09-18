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
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import ru.progrm_jarvis.reflector.invoke.MethodHandleUtil;
import ru.progrm_jarvis.reflector.util.Caches;
import ru.progrm_jarvis.reflector.wrapper.AbstractConstructorWrapper;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;

@Value
@EqualsAndHashCode(callSuper = true)
public class FastConstructorWrapper<T>
        extends AbstractConstructorWrapper<T> implements FastReflectorWrapper<Constructor<T>> {

    /**
     * Cache of method wrappers
     */
    private static final Cache<Constructor, FastConstructorWrapper<?>> CACHE = Caches.weakValuesCache();

    @NonNull private MethodHandle constructorHandle;

    protected FastConstructorWrapper(@NonNull final Constructor<T> constructor,
                                     @NonNull final MethodHandle constructorHandle) {
        super(constructor);

        this.constructorHandle = constructorHandle;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> FastConstructorWrapper<T> from(@NonNull final Constructor<T> constructor) {
        return (FastConstructorWrapper<T>) CACHE.get(constructor, () -> new FastConstructorWrapper<>(
                constructor, MethodHandleUtil.methodHandleFrom(constructor)
        ));
    }

    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public T construct(final Object... arguments) {
        return (T) constructorHandle.invokeWithArguments(arguments);
    }
}
