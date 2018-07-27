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
import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.reflector.util.ValueContainer;
import ru.progrm_jarvis.reflector.util.ThrowingFunction;

import java.util.Optional;

/**
 * Utility to recursively find class members
 */
@UtilityClass
@SuppressWarnings("WeakerAccess")
public class RecursiveClassDigger {

    /**
     * Recursively "digs" for some value in class.
     * <i>Digging</i> here means scanning class and then it's parents (excluding interfaces) until the value is found
     * or the bound is reached.
     * Digger here is a function taking class which is clazz or its parent trying to find something in it,
     * returning value container if something was found
     * or {@code null} if nothing was found (and so digging should be continued is possible).
     * May throw exception of specified type.
     *
     * @param clazz class from which to start the digging process
     * @param digger function digging for specified member
     * @param bound class after reaching which digging should be stopped (inclusive)
     * @param <T> type of class digged
     * @param <R> type of value which should be found
     * @param <E> exception which mey be thrown
     * @return {@link Optional} containing {@link ClassMember} of class in which it was found and the very value
     * or else {@link Optional#empty()} if nothing was found
     * @throws E if an exception occurs while digging
     *
     * @see #digWithInterfaces(Class, ThrowingFunction, Class) digs including interfaces
     */
    public <T, R, E extends Throwable> Optional<ClassMember<? super T, R>> dig(
            @NonNull final Class<? super T> clazz,
            @NonNull final ThrowingFunction<Class<? super T>, ValueContainer<R>, E> digger,
            @Nullable final Class<? super T> bound
    ) throws E {
        // try find in clazz
        {
            val possible = digger.use(clazz);
            // if something was found then return it
            if (possible != null) return Optional.of(new ClassMember<>(clazz, possible.getValue()));
        }

        // if bound is reached then return what was found here
        if (clazz == bound) return Optional.empty();
        // try dig in superclass if not found
        val superClass = (Class<? super T>) clazz.getSuperclass();
        return superClass == null ? Optional.empty() : dig(superClass, digger, bound);
    }

    /**
     * Recursively "digs" for some value in class.
     * <i>Digging</i> here means scanning class and then it's parents (including interfaces) until the value is found
     * or the bound is reached.
     * Digger here is a function taking class which is clazz or its parent trying to find something in it,
     * returning value container if something was found
     * or {@code null} if nothing was found (and so digging should be continued is possible).
     * May throw exception of specified type.
     *
     * @param clazz class from which to start the digging process
     * @param digger function digging for specified member
     * @param bound class after reaching which digging should be stopped (inclusive)
     * @param <R> type of value which should be found
     * @param <E> exception which mey be thrown
     * @return {@link Optional} containing {@link ClassMember} of class in which it was found and the very value
     * or else {@link Optional#empty()} if nothing was found
     * @throws E if an exception occurs while digging
     *
     * @see #dig(Class, ThrowingFunction, Class) digs excluging interfaces
     */
    public <R, E extends Throwable> Optional<ClassMember<?, R>> digWithInterfaces(
            @NonNull final Class<?> clazz,
            @NonNull final ThrowingFunction<Class<?>, ValueContainer<R>, E> digger,
            @Nullable final Class<?> bound
    ) throws E {
        // try find in clazz
        {
            val possible = digger.use(clazz);
            // if something was found then return it
            if (possible != null) return Optional.of(new ClassMember<>(clazz, possible.getValue()));
        }

        // if bound is reached then return what was found here
        if (clazz == bound) return Optional.empty();

        Optional<ClassMember<?, R>> dug = Optional.empty();
        {
            // try dig in superclass if not found
            val superClass = (Class<?>) clazz.getSuperclass();
            if (superClass != null) dug = digWithInterfaces(superClass, digger, bound);
        }

        if (dug.isPresent()) return dug;

        for (val superInterface : clazz.getInterfaces()) {
            dug = digWithInterfaces(superInterface, digger, bound);
            if (dug.isPresent()) return dug;
        }

        return Optional.empty();
    }
}
