package ru.progrm_jarvis.reflector;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import ru.progrm_jarvis.reflector.util.CheckedConsumer;
import ru.progrm_jarvis.reflector.util.CheckedFunction;

import java.lang.reflect.*;

@UtilityClass
public class AccessHelper {

    private static final Field
            FIELD_MODIFIERS,
            METHOD_MODIFIERS,
            CONSTRUCTOR_MODIFIERS;

    static {
        try {
            FIELD_MODIFIERS = Field.class.getDeclaredField("modifiers");
            METHOD_MODIFIERS = Field.class.getDeclaredField("modifiers");
            CONSTRUCTOR_MODIFIERS = Field.class.getDeclaredField("modifiers");
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException("Could not initialize reflector's AccessHelper due to exception", e);
        }
    }

    public  <T extends AccessibleObject, R> R accessAndGet(final T object, final CheckedFunction<T, R> CheckedFunction)
            throws Throwable {
        if (object.isAccessible()) return CheckedFunction.apply(object);
        try {
            object.setAccessible(true);
            return CheckedFunction.apply(object);
        } finally {
            object.setAccessible(false);
        }
    }

    public <T extends AccessibleObject> void access(final T object, final CheckedConsumer<T> CheckedConsumer)
            throws Throwable {
        if (object.isAccessible()) CheckedConsumer.accept(object);
        else try {
            object.setAccessible(true);
            CheckedConsumer.accept(object);
        } finally {
            object.setAccessible(false);
        }
    }

    @SneakyThrows
    public <R> R operateAndGet(@NonNull final Field field, @NonNull final CheckedFunction<Field, R> CheckedFunction) {
        val modifiers = field.getModifiers();
        if ((modifiers & Modifier.FINAL) == 0) return accessAndGet(field, CheckedFunction);
        if (!FIELD_MODIFIERS.isAccessible()) FIELD_MODIFIERS.setAccessible(true);
        try {
            FIELD_MODIFIERS.set(field, modifiers & ~Modifier.FINAL);
            return accessAndGet(field, CheckedFunction);
        } finally {
            FIELD_MODIFIERS.set(field, modifiers);
        }
    }

    @SneakyThrows
    public void operate(@NonNull final Field field, @NonNull final CheckedConsumer<Field> CheckedConsumer) {
        val modifiers = field.getModifiers();
        if ((modifiers & Modifier.FINAL) == 0) access(field, CheckedConsumer);
        else {
            if (!FIELD_MODIFIERS.isAccessible()) FIELD_MODIFIERS.setAccessible(true);
            try {
                FIELD_MODIFIERS.set(field, modifiers & ~Modifier.FINAL);
                access(field, CheckedConsumer);
            } finally {
                FIELD_MODIFIERS.set(field, modifiers);
            }
        }
    }

    @SneakyThrows
    public <R> R operateAndGet(@NonNull final Method method,
                               @NonNull final CheckedFunction<Method, R> CheckedFunction) {
        val modifiers = method.getModifiers();
        if ((modifiers & Modifier.FINAL) == 0) return accessAndGet(method, CheckedFunction);
        if (!METHOD_MODIFIERS.isAccessible()) METHOD_MODIFIERS.setAccessible(true);
        try {
            METHOD_MODIFIERS.set(method, modifiers & ~Modifier.FINAL);
            return accessAndGet(method, CheckedFunction);
        } finally {
            METHOD_MODIFIERS.set(method, modifiers);
        }
    }

    @SneakyThrows
    public void operate(@NonNull final Method method,
                        @NonNull final CheckedConsumer<Method> CheckedConsumer) {
        val modifiers = method.getModifiers();
        if ((modifiers & Modifier.FINAL) == 0) access(method, CheckedConsumer);
        else {
            if (!METHOD_MODIFIERS.isAccessible()) METHOD_MODIFIERS.setAccessible(true);
            try {
                METHOD_MODIFIERS.set(method, modifiers & ~Modifier.FINAL);
                access(method, CheckedConsumer);
            } finally {
                METHOD_MODIFIERS.set(method, modifiers);
            }
        }
    }

    @SneakyThrows
    public <R> R operateAndGet(@NonNull final Constructor constructor,
                               @NonNull final CheckedFunction<Constructor, R> CheckedFunction) {
        val modifiers = constructor.getModifiers();
        if ((modifiers & Modifier.FINAL) == 0) return accessAndGet(constructor, CheckedFunction);
        if (!CONSTRUCTOR_MODIFIERS.isAccessible()) CONSTRUCTOR_MODIFIERS.setAccessible(true);
        try {
            CONSTRUCTOR_MODIFIERS.set(constructor, modifiers & ~Modifier.FINAL);
            return accessAndGet(constructor, CheckedFunction);
        } finally {
            CONSTRUCTOR_MODIFIERS.set(constructor, modifiers);
        }
    }

    @SneakyThrows
    public void operate(@NonNull final Constructor constructor,
                        @NonNull final CheckedConsumer<Constructor> CheckedConsumer) {
        val modifiers = constructor.getModifiers();
        if ((modifiers & Modifier.FINAL) == 0) access(constructor, CheckedConsumer);
        else {
            if (!CONSTRUCTOR_MODIFIERS.isAccessible()) CONSTRUCTOR_MODIFIERS.setAccessible(true);
            try {
                CONSTRUCTOR_MODIFIERS.set(constructor, modifiers & ~Modifier.FINAL);
                access(constructor, CheckedConsumer);
            } finally {
                CONSTRUCTOR_MODIFIERS.set(constructor, modifiers);
            }
        }
    }
}
