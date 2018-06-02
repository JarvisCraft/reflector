package ru.progrm_jarvid.reflector;

import com.sun.istack.internal.Nullable;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import ru.progrm_jarvid.reflector.util.Possible;
import ru.progrm_jarvid.reflector.util.ThrowingFunction;

import java.util.Optional;

@UtilityClass
public class RecursiveClassDigger {

    public <T, R, E extends Throwable> Optional<ClassMember<? super T, R>> dig(
            @NonNull final Class<? super T> clazz,
            @NonNull final ThrowingFunction<Class<? super T>, Possible<R>, E> digger,
            @Nullable final Class<? super T> bound
    ) throws E {
        // try find in clazz
        {
            val possible = digger.apply(clazz);
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
            val possible = digger.apply(clazz);
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
