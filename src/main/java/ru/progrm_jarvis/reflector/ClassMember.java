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
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

/**
 * POJO representing a member of class.
 *
 * @param <T> type of class containing member
 * @param <R> member of class
 */
@Value
@RequiredArgsConstructor
public class ClassMember<T, R> {

    /**
     * Class owning this member.
     */
    @NonNull private Class<? extends T> owner;

    /**
     * Member contained in the class.
     */
    @Nullable private R value;
}
