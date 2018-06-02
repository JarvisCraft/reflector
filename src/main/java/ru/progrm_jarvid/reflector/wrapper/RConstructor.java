package ru.progrm_jarvid.reflector.wrapper;

import lombok.*;

import java.lang.reflect.Constructor;

/**
 * Wrapper for {@link Constructor} to be used with Reflector.
 */
@Value(staticConstructor = "of")
public class RConstructor<T> implements RValueHolder {

    /**
     * Actual constructor wrapped.
     */
    private Constructor<T> constructor;

    @Override
    @SneakyThrows
    public T getValue(final Object object, @NonNull final Object... arguments) {
        val accessible = constructor.isAccessible();
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(arguments);
        } finally {
            constructor.setAccessible(accessible);
        }
    }
}
