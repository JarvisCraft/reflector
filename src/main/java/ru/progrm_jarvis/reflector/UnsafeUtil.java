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
import sun.misc.Unsafe;

/**
 * An utility class to make access tu {@link Unsafe} simpler
 */
@UtilityClass
public class UnsafeUtil {

    /**
     * {@code public} instance of {@link Unsafe}
     */
    @SuppressWarnings("WeakerAccess")
    public static Unsafe UNSAFE;

    static {
        try {
            val unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            val accessible = unsafeField.isAccessible();
            try {
                unsafeField.setAccessible(true);
                UNSAFE = (Unsafe) unsafeField.get(null);
            } finally {
                unsafeField.setAccessible(accessible);
            }
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses {@link Unsafe} using consumer given.
     *
     * @param consumer consumer accepting unsafe
     */
    @SneakyThrows
    public void useUnsafe(@NonNull final CheckedConsumer<Unsafe> consumer) {
        consumer.consume(UNSAFE);
    }

    /**
     * Uses {@link Unsafe} using function given returning operation's result.
     *
     * @param function accepting unsafe and returning result of operation performed
     * @param <T> type of value returned by operation
     * @return value obtained by invoking function
     */
    @SneakyThrows
    public <T> T useUnsafeAndGet(@NonNull final CheckedFunction<Unsafe, T> function) {
        return function.use(UNSAFE);
    }
}
