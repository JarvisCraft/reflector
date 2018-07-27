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

package ru.progrm_jarvis.reflector.util;

import lombok.SneakyThrows;

import java.util.function.Consumer;

/**
 * {@inheritDoc}
 * This {@code Consumer} extension provides {@link #consume(Object)} method
 * which is the same as {@link #accept(Object)} but is declared as {@code throws Throwable}
 *
 * @param <T> the type of the input to the operation
 */
@FunctionalInterface
public interface CheckedConsumer<T> extends Consumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     * @throws Throwable if an exception occurs while performing
     */
    void consume(T t) throws Throwable;

    /**
     * {@inheritDoc}
     * Invokes {@link #consume(Object)} wrapping any thrown catched exception with {@code RuntimeException}.
     *
     * @throws RuntimeException if an exception occurs while performing
     */
    @Override
    @SneakyThrows
    default void accept(final T t) {
        consume(t);
    }
}
