package ru.progrm_jarvis.reflector.wrapper;

import lombok.*;
import ru.progrm_jarvis.reflector.AccessHelper;

import java.lang.reflect.Constructor;

/**
 * Wrapper for {@link Constructor} to be used with Reflector.
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RConstructor<T> implements ReflectorWrapper {

    /**
     * Actual constructor wrapped.
     */
    @NonNull private Constructor<T> constructor;

    @SneakyThrows
    public T construct(@NonNull final Object... arguments) {
        return AccessHelper.accessAndGet(constructor, constructor -> constructor.newInstance(arguments));
    }

    // not using lombok due to Intellij IDEA removing generic info sry
    public static <T> RConstructor<T> of(@NonNull final Constructor<T> constructor) {
        return new RConstructor<>(constructor);
    }
}
