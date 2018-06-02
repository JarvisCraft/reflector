package ru.progrm_jarvid.reflector.wrapper;

import lombok.*;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * Wrapper for {@link Field} to be used with Reflector.
 */
@Value(staticConstructor = "of")
public class RField implements RValueHolder {

    /**
     * Actual field wrapped.
     */
    private Field field;

    @Override
    @SneakyThrows
    public Object getValue(@NonNull final Object object, final Object... arguments) {
        val accessible = field.isAccessible();
        try {
            field.setAccessible(true);
            return field.get(object);
        } finally {
            field.setAccessible(accessible);
        }
    }
}
