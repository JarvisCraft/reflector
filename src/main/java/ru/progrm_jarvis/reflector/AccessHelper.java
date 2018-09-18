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

package ru.progrm_jarvis.reflector;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import ru.progrm_jarvis.reflector.util.function.CheckedConsumer;
import ru.progrm_jarvis.reflector.util.function.CheckedFunction;

import java.lang.reflect.*;

/**
 * Utilities used for easier manipulation with reflection-related objects which have access restrictions
 */
@UtilityClass
public class AccessHelper {

    /**
     * {@code modifiers} field of {@link Field} class used for manipulating its flags
     */
    private static final Field FIELD_MODIFIERS;

    static {
        try {
            FIELD_MODIFIERS = Field.class.getDeclaredField("modifiers");
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException("Could not initialize reflector's AccessHelper due to exception", e);
        }
    }

    public  <T extends AccessibleObject, R> R accessAndGet(final T object, final CheckedFunction<T, R> checkedFunction)
            throws Throwable {
        if (object.isAccessible()) return checkedFunction.use(object);
        try {
            object.setAccessible(true);
            return checkedFunction.use(object);
        } finally {
            object.setAccessible(false);
        }
    }

    public <T extends AccessibleObject> void access(final T object, final CheckedConsumer<T> checkedConsumer)
            throws Throwable {
        if (object.isAccessible()) checkedConsumer.consume(object);
        else try {
            object.setAccessible(true);
            FIELD_MODIFIERS.setAccessible(true);
            checkedConsumer.consume(object);
        } finally {
            object.setAccessible(false);
        }
    }

    @SneakyThrows
    public <R> R operateAndGet(@NonNull final Field field, @NonNull final CheckedFunction<Field, R> checkedFunction) {
        val modifiers = field.getModifiers();
        if (Modifier.isFinal(modifiers)) {
            if (!FIELD_MODIFIERS.isAccessible()) FIELD_MODIFIERS.setAccessible(true);
            try {
                FIELD_MODIFIERS.set(field, modifiers & ~Modifier.FINAL);
                return accessAndGet(field, checkedFunction);
            } finally {
                FIELD_MODIFIERS.set(field, modifiers);
            }
        }
        return accessAndGet(field, checkedFunction);
    }

    @SneakyThrows
    public void operate(@NonNull final Field field, @NonNull final CheckedConsumer<Field> checkedConsumer) {
        val modifiers = field.getModifiers();
        if (Modifier.isFinal(modifiers)) {
            if (!FIELD_MODIFIERS.isAccessible()) FIELD_MODIFIERS.setAccessible(true);
            try {
                FIELD_MODIFIERS.set(field, modifiers & ~Modifier.FINAL);
                access(field, checkedConsumer);
            } finally {
                FIELD_MODIFIERS.set(field, modifiers);
            }
        } else access(field, checkedConsumer);
    }

    @SneakyThrows
    public <R> R operateAndGet(@NonNull final Method method,
                               @NonNull final CheckedFunction<Method, R> checkedFunction) {
        return accessAndGet(method, checkedFunction);
    }

    @SneakyThrows
    public void operate(@NonNull final Method method,
                        @NonNull final CheckedConsumer<Method> checkedConsumer) {
        access(method, checkedConsumer);
    }

    @SneakyThrows
    public <R> R operateAndGet(@NonNull final Constructor constructor,
                               @NonNull final CheckedFunction<Constructor, R> checkedFunction) {
        return accessAndGet(constructor, checkedFunction);
    }

    @SneakyThrows
    public void operate(@NonNull final Constructor constructor,
                        @NonNull final CheckedConsumer<Constructor> checkedConsumer) {
        access(constructor, checkedConsumer);
    }
}
