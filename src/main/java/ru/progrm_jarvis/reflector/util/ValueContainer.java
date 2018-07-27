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

import lombok.Value;

/**
 * An immutable value container which may hold a value (including null)
 *
 * @param <T> type of value stored
 */
@Value(staticConstructor = "of")
public class ValueContainer<T> {

    /**
     * Static instance of value container holding {@code null}
     */
    private static final ValueContainer<?> EMPTY = new ValueContainer<>(null);

    /**
     * Value of this container
     */
    private T value;

    /**
     * Returns an empty value container without allocating any new space for it (as a static instance is always stored).
     *
     * @param <T> type of container
     * @return empty value container (the one storing {@code null})
     */
    @SuppressWarnings("unchecked")
    public static <T> ValueContainer<T> empty() {
        return (ValueContainer<T>) EMPTY;
    }
}
