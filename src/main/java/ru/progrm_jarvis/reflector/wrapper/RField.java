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

import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;

/**
 * Wrapper for {@link Field} to be used with Reflector.
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RField<T, R> implements ReflectorWrapper {

    private static final Cache<Field, RField<?, ?>> CACHE = CacheBuilder.newBuilder().weakValues().build();

    /**
     * Actual field wrapped.
     */
    @NonNull private Field field;

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public R getValue(@Nullable final T object) {
        return AccessHelper.accessAndGet(field, field -> (R) field.get(object));
    }

    public R getValue() {
        return getValue(null);
    }

    @SneakyThrows
    public void setValue(@Nullable final T object, @Nullable final R value) {
        AccessHelper.operate(field, field -> field.set(object, value));
    }

    public void setValue(@Nullable final R value) {
        setValue(null, value);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public R updateValue(@Nullable final Object object, @Nullable final Object value) {
        return AccessHelper.operateAndGet(field, field -> {
            val oldValue = (R) field.get(object);

            field.set(object, value);

            return oldValue;
        });
    }

    public R updateValue(@Nullable final Object value) {
        return updateValue(null, value);
    }

    @SuppressWarnings("unchecked")
    public static <T, R> RField<T, R> of(@NonNull final Field field) {
        try {
            return ((RField<T, R>) CACHE.get(field, () -> new RField<>(field)));
        } catch (final ExecutionException e) {
            throw new RuntimeException("Could not obtain RField<R> value from cache");
        }
    }
}
