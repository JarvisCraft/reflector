package ru.progrm_jarvis.reflector.wrapper;

import lombok.*;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.reflector.AccessHelper;

import java.lang.reflect.Method;

/**
 * Wrapper for {@link Method} to be used with Reflector.
 */
@Value(staticConstructor = "of")
public class RMethod<T> implements ReflectorWrapper {

    /**
     * Actual method wrapped.
     */
    @NonNull private Method method;

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public T invoke(@Nullable final Object object, @NonNull final Object... arguments) {
        return AccessHelper.accessAndGet(method, method -> (T) method.invoke(object, arguments));
    }

    public T invokeStatic(@NonNull final Object... arguments) {
        return invoke(null, arguments);
    }
}
