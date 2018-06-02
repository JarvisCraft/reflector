package ru.progrm_jarvid.reflector.wrapper;

import lombok.*;

import java.lang.reflect.Method;

/**
 * Wrapper for {@link Method} to be used with Reflector.
 */
@Value(staticConstructor = "of")
public class RMethod implements RValueHolder {

    /**
     * Actual method wrapped.
     */
    private Method method;

    @Override
    @SneakyThrows
    public Object getValue(@NonNull final Object object, final Object... arguments) {
        val accessible = method.isAccessible();
        try {
            method.setAccessible(true);
            return method.invoke(object, arguments);
        } finally {
            method.setAccessible(accessible);
        }
    }
}
