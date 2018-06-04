package ru.progrm_jarvis.reflector.wrapper;

import lombok.*;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.reflector.AccessHelper;

import java.lang.reflect.Field;

/**
 * Wrapper for {@link Field} to be used with Reflector.
 */
@Value(staticConstructor = "of")
public class RField<T> implements ReflectorWrapper {

    /**
     * Actual field wrapped.
     */
    @NonNull private Field field;

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public T getValue(@Nullable final Object object) {
        return AccessHelper.accessAndGet(field, field -> (T) field.get(object));
    }

    public T getValue() {
        return getValue(null);
    }

    @SneakyThrows
    public void setValue(@Nullable final Object object, @Nullable final Object value) {
        AccessHelper.operate(field, field -> field.set(object, value));
    }

    public void setValue(@Nullable final Object value) {
        setValue(null, value);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public T updateValue(@Nullable final Object object, @Nullable final Object value) {
        return AccessHelper.operateAndGet(field, field -> {
            val oldValue = (T) field.get(object);

            field.set(object, value);

            return oldValue;
        });
    }

    public T updateValue(@Nullable final Object value) {
        return updateValue(null, value);
    }
}
