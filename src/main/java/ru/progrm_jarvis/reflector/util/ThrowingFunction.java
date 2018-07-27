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

/**
 * {@inheritDoc}
 * This is the same as normal {@code CheckedFunction} but its {@code Throwable} type is known at compile time
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @param <E> the type of the exception to be declared as checked
 */
@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable> extends CheckedFunction<T, R> {

    /**
     * {@inheritDoc}
     *
     * @throws E if an exception occurs while performing
     */
    @Override
    R use(T t) throws E;
}
