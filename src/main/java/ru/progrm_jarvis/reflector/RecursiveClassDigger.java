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
import ru.progrm_jarvis.reflector.util.Possible;
import ru.progrm_jarvis.reflector.util.ThrowingFunction;

import java.util.Optional;

@UtilityClass
@SuppressWarnings("WeakerAccess")
public class RecursiveClassDigger {

    public <T, R, E extends Throwable> Optional<ClassMember<? super T, R>> dig(
            @NonNull final Class<? super T> clazz,
            @NonNull final ThrowingFunction<Class<? super T>, Possible<R>, E> digger,
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

    public <R, E extends Throwable> Optional<ClassMember<?, R>> digWithInterfaces(
            @NonNull final Class<?> clazz,
            @NonNull final ThrowingFunction<Class<?>, Possible<R>, E> digger,
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
