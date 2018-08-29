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
import ru.progrm_jarvis.reflector.MethodHandleBuilder;
import ru.progrm_jarvis.reflector.util.Caches;
import ru.progrm_jarvis.reflector.wrapper.AbstractFieldWrapper;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

@Value
@EqualsAndHashCode(callSuper = true)
public class FastFieldWrapper<T, V>
        extends AbstractFieldWrapper<T, V> implements FastReflectorWrapper<Field> {

    /**
     * Cache of method wrappers
     */
    private static final Cache<Field, FastFieldWrapper<?, ?>> CACHE = Caches.weakValuesCache();

    @NonNull private MethodHandle getterHandle;
    @NonNull private MethodHandle setterHandle;

    protected FastFieldWrapper(@NonNull final Field field, @NonNull final MethodHandle getterHandle,
                               @NonNull final MethodHandle setterHandle) {
        super(field);

        this.getterHandle = getterHandle;
        this.setterHandle = setterHandle;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T, V> FastFieldWrapper<T, V> from(@NonNull final Field field) {
        return (FastFieldWrapper<T, V>) CACHE.get(field, () -> new FastFieldWrapper<T, V>(
                field,
                MethodHandleBuilder.methodHandleFromGetter(field),
                MethodHandleBuilder.methodHandleFromSetter(field)
        ));
    }

    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public V getValue(final T instance) {
        if (instance == null) return getValue();
        return (V) getterHandle.invokeWithArguments(instance);
    }

    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public V getValue() {
        return (V) getterHandle.invokeWithArguments();
    }

    @Override
    @SneakyThrows
    public void setValue(final T instance, final V value) {
        if (instance == null) setValue(value);
        setterHandle.invokeWithArguments(instance, value);
    }

    @Override
    @SneakyThrows
    public void setValue(final V value) {
        setterHandle.invokeWithArguments(value);
    }
}
