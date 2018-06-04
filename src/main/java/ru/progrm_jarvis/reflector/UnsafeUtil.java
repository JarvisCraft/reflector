package ru.progrm_jarvis.reflector;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import ru.progrm_jarvis.reflector.util.CheckedConsumer;
import ru.progrm_jarvis.reflector.util.CheckedFunction;
import sun.misc.Unsafe;

@UtilityClass
public class UnsafeUtil {

    public static Unsafe UNSAFE;

    static {
        try {
            val unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            val accessible = unsafeField.isAccessible();
            try {
                unsafeField.setAccessible(true);
                UNSAFE = (Unsafe) unsafeField.get(null);
            } finally {
                unsafeField.setAccessible(accessible);
            }
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void useUnsafe(@NonNull final CheckedConsumer<Unsafe> consumer) throws Throwable {
        consumer.accept(UNSAFE);
    }

    public <T> T useUnsafeAndGet(@NonNull final CheckedFunction<Unsafe, T> function) throws Throwable {
        return function.apply(UNSAFE);
    }
}
