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

package ru.progrm_jarvis.reflector.util.function;

import lombok.SneakyThrows;

import java.util.function.Supplier;

/**
 * {@inheritDoc}
 * This {@code Supplier} extension provides {@link #supply()} method
 * which is the same as {@link #get()} but is declared as {@code throws {@link Throwable}}
 *
 * @param <T> the type of the input to the operation
 */
@FunctionalInterface
public interface CheckedSupplier<T> extends Supplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     * @throws Throwable if an exception occurs while performing
     */
    T supply() throws Throwable;

    /**
     * {@inheritDoc}
     * Invokes {@link #supply()} wrapping any thrown catched exception with {@link RuntimeException}.
     *
     * @throws RuntimeException if an exception occurs while performing
     */
    @Override
    @SneakyThrows
    default T get() {
        return supply();
    }
}
