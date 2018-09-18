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

import lombok.NonNull;

import java.lang.reflect.Method;

/**
 * Wrapper for {@link Method} to be used with Reflector
 *
 * @param <T> type of class containing this method
 * @param <R> type of value returned by this method
 */
public interface MethodWrapper<T, R> extends ReflectorWrapper<Method> {

    /**
     * Invokes this method ignoring any limitations if possible.
     *
     * @param instance instance from which to declare method
     * @param arguments arguments to be passed to this method
     * @return value returned by method
     * @throws NullPointerException if {@code object} is {@code null} but this field is not static
     */
    R invoke(T instance, @NonNull Object... arguments);

    /**
     * Invokes this method ignoring on no instance (which means that static method is to be invoked)
     * ignoring any limitations if possible.
     *
     * @param arguments arguments to be passed to this method
     * @return value returned by method
     * @throws NullPointerException if this field is not {@code static}
     */
    R invokeStatic(@NonNull Object... arguments);
}
