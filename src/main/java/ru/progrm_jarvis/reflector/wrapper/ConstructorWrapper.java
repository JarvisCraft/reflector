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

import java.lang.reflect.Constructor;

/**
 * Wrapper for {@link Constructor} to be used with Reflector
 *
 * @param <T> type of class whose constructor it is
 */
public interface ConstructorWrapper<T> extends ReflectorWrapper<Constructor<T>> {

    /**
     * Creates new instance of this object by invoking it's constructor wrapped ignoring any limitations if possible.
     *
     * @param arguments arguments to be passed to constructor
     * @return object instantiated using constructor
     */
    T construct(@NonNull Object... arguments);
}
