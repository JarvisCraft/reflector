package ru.progrm_jarvis.reflector.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ru.progrm_jarvis.reflector.util.function.CheckedSupplier;

@UtilityClass
public class ObjectUtil {

    public <T> T orDefault(@NonNull final T value, final T defaultValue) {
        return value == null ? defaultValue : value;
    }

    public <T> T orDefault(@NonNull final T value, final CheckedSupplier<T> defaultValueSupplier) {
        return value == null ? defaultValueSupplier.get() : value;
    }
}
