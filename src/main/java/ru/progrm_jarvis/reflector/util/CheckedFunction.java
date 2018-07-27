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

import java.util.function.Function;

/**
 * {@inheritDoc}
 * This {@code Function} extension provides {@link #use(Object)} method
 * which is the same as {@link #apply(Object)} but is declared as {@code throws Throwable}
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface CheckedFunction<T, R> extends Function<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     * @throws Throwable if an exception occurs while performing
     */
    R use(T t) throws Throwable;

    /**
     * {@inheritDoc}
     * Invokes {@link #use(Object)} wrapping any thrown catched exception with {@code RuntimeException}.
     *
     * @throws RuntimeException if an exception occurs while performing
     */
    @Override
    @SneakyThrows
    default R apply(T t) {
        return use(t);
    }
}
