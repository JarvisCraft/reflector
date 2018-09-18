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
import lombok.*;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.reflector.AccessHelper;
import ru.progrm_jarvis.reflector.util.Caches;
import ru.progrm_jarvis.reflector.wrapper.FieldWrapper;
import ru.progrm_jarvis.reflector.wrapper.AbstractFieldWrapper;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;
import java.util.function.UnaryOperator;

@Value
@EqualsAndHashCode(callSuper = true)
public class ReflectionFieldWrapper<T, V>
        extends AbstractFieldWrapper<T, V> implements ReflectionReflectorWrapper<Field> {

    /**
     * Cache of field wrappers
     */
    private static final Cache<Field, ReflectionFieldWrapper<?, ?>> CACHE = Caches.weakValuesCache();

    protected ReflectionFieldWrapper(@NonNull final Field field) {
        super(field);
    }

    /**
     * Creates new field wrapper instance for the field given or gets it from cache if one already exists.
     *
     * @param field field to get wrapped
     * @param <T> type containing this field
     * @param <V> type of this field's value
     * @return field wrapper created or got from cache
     */
    @SuppressWarnings("unchecked")
    public static <T, V> FieldWrapper<T, V> from(@NonNull final Field field) {
        try {
            return (FieldWrapper<T, V>) CACHE.get(field, () -> new ReflectionFieldWrapper<>(field));
        } catch (final ExecutionException e) {
            throw new RuntimeException("Could not obtain FieldWrapper<V> value methodHandleFrom cache");
        }
    }

    /**
     * Gets value of this field ignoring any limitations if possible.
     *
     * @param instance instance of which field's value is get
     * @return value of this field (static)
     * @throws NullPointerException if {@code object} is {@code null} but this field is not static
     */
    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public V getValue(@Nullable final T instance) {
        return AccessHelper.accessAndGet(field, field -> (V) field.get(instance));
    }

    /**
     * Gets value of this field on no instance (which means that static value is to be got)
     * ignoring any limitations if possible.
     *
     * @return value of this {@code static} field
     * @throws NullPointerException if this field is not {@code static}
     */
    @Override public V getValue() {
        return getValue(null);
    }

    /**
     * Sets value of this field ignoring any limitations if possible.
     *
     * @param instance instance of which field's value is set
     * @param value value to set to this field
     * @throws NullPointerException if {@code object} is {@code null} but this field is not static
     */
    @Override
    @SneakyThrows
    public void setValue(@Nullable final T instance, @Nullable final V value) {
        AccessHelper.operate(field, field -> field.set(instance, value));
    }

    /**
     * Sets value of this field on no instance (which means that static value is to be set)
     * ignoring any limitations if possible.
     *
     * @param value value to set to this field
     * @throws NullPointerException if this field is not {@code static}
     */
    @Override
    public void setValue(@Nullable final V value) {
        setValue(null, value);
    }

    /**
     * Updates value of this field ignoring any limitations if possible.
     *
     * @param instance instance of which field's value is set
     * @param value value to set to this field
     * @return previous value of this field
     * @throws NullPointerException if {@code object} is {@code null} but this field is not static
     */
    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public V getAndUpdate(@Nullable final T instance, @Nullable final V value) {
        return AccessHelper.operateAndGet(field, field -> {
            val oldValue = (V) field.get(instance);

            field.set(instance, value);

            return oldValue;
        });
    }


    @Override
    public V getAndUpdate(@Nullable final V value) {
        return getAndUpdate(null, value);
    }

    /**
     * Updates value of this field based on previous value using function given ignoring any limitations if possible.
     *
     * @param instance instance of which field's value is set
     * @param operator function to create new value based on old
     * @return previous value of this field
     * @throws NullPointerException if {@code object} is {@code null} but this field is not static
     */
    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public V getAndCompute(@Nullable final T instance, @NonNull final UnaryOperator<V> operator) {
        return AccessHelper.operateAndGet(field, field -> {
            val oldValue = (V) field.get(instance);

            field.set(instance, operator.apply(oldValue));

            return oldValue;
        });
    }

    @Override
    public V getAndCompute(final UnaryOperator<V> operator) {
        return getAndCompute(null, operator);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V computeAndGet(final T instance, final UnaryOperator<V> operator) {
        return AccessHelper.operateAndGet(field, field -> {
            val newValue = operator.apply((V) field.get(instance));
            field.set(instance, newValue);

            return newValue;
        });
    }

    @Override
    public V computeAndGet(final UnaryOperator<V> operator) {
        return computeAndGet(null, operator);
    }
}
