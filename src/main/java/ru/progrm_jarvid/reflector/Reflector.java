package ru.progrm_jarvid.reflector;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import ru.progrm_jarvid.reflector.util.Possible;
import ru.progrm_jarvid.reflector.util.ThrowingFunction;
import ru.progrm_jarvid.reflector.wrapper.RField;
import ru.progrm_jarvid.reflector.wrapper.RMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Main reflector class which wraps {@link Class} methods with methods which don't throw checked exceptions.
 */
@UtilityClass
@SuppressWarnings({"unused", "WeakerAccess"})
public class Reflector {

    ///////////////////////////////////////////////////////////////////////////
    // Unwrapped
    ///////////////////////////////////////////////////////////////////////////

    @SneakyThrows
    public <T> Optional<ClassMember<? super T, Field>> digForField(@NonNull final Class<T> clazz,
                                                                   @NonNull final Predicate<Field> predicate,
                                                                   @NonNull final Class<? super T> bound) {
        return RecursiveClassDigger
                .dig(clazz, (ThrowingFunction<Class<? super T>, Possible<Field>, Throwable>) owner -> {
                    for (val field : owner.getDeclaredFields()) if (predicate.test(field)) return Possible.of(field);
                    return null;
                    }, bound);
    }

    @SneakyThrows
    public Optional<ClassMember<?, Method>> digForMethod(@NonNull final Class<?> clazz,
                                                         @NonNull final Predicate<Method> predicate,
                                                         @NonNull final Class<?> bound) {
        return RecursiveClassDigger
                .digWithInterfaces(clazz, (ThrowingFunction<Class<?>, Possible<Method>, Throwable>) owner -> {
                    for (val method : owner.getDeclaredMethods()) if (predicate.test(method)) return Possible
                            .of(method);
                    return null;
                    }, bound);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Wrapped field
    ///////////////////////////////////////////////////////////////////////////

    @SneakyThrows
    public RField getField(@NonNull final Class<?> clazz, @NonNull final String name) {
        return RField.of(clazz.getField(name));
    }

    @SneakyThrows
    public RField getField(@NonNull final Object object, @NonNull final String name) {
        return getField(object.getClass(), name);
    }

    @SneakyThrows
    public RField getDeclaredField(@NonNull final Class<?> clazz, @NonNull final String name) {
        return RField.of(clazz.getDeclaredField(name));
    }

    @SneakyThrows
    public RField getDeclaredField(@NonNull final Object object, @NonNull final String name) {
        return getDeclaredField(object.getClass(), name);
    }

    @SneakyThrows
    public Optional<RField> getField(@NonNull final Class<?> clazz, @NonNull final Predicate<Field> predicate) {
        return digForField(clazz, predicate, Object.class).map(member -> RField.of(member.getValue()));
    }

    @SneakyThrows
    public Optional<RField> getField(@NonNull final Object object, @NonNull final Predicate<Field> predicate) {
        return getField(object.getClass(), predicate);
    }

    @SneakyThrows
    public Optional<RField> getAnyField(@NonNull final Class<?> clazz, @NonNull final String name) {
        return getField(clazz, field -> name.equals(field.getName()));
    }

    @SneakyThrows
    public Optional<RField> getAnyField(@NonNull final Object object, @NonNull final String name) {
        return getAnyField(object.getClass(), name);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Wrapped field
    ///////////////////////////////////////////////////////////////////////////

    @SneakyThrows
    public RMethod getMethod(@NonNull final Class<?> clazz, @NonNull final String name) {
        return RMethod.of(clazz.getMethod(name));
    }

    @SneakyThrows
    public RMethod getMethod(@NonNull final Object object, @NonNull final String name) {
        return getMethod(object.getClass(), name);
    }

    @SneakyThrows
    public RMethod getDeclaredMethod(@NonNull final Class<?> clazz, @NonNull final String name) {
        return RMethod.of(clazz.getDeclaredMethod(name));
    }

    @SneakyThrows
    public RMethod getDeclaredMethod(@NonNull final Object object, @NonNull final String name) {
        return getDeclaredMethod(object.getClass(), name);
    }

    @SneakyThrows
    public Optional<RMethod> getMethod(@NonNull final Class<?> clazz, @NonNull final Predicate<Method> predicate) {
        return digForMethod(clazz, predicate, Object.class).map(member -> RMethod.of(member.getValue()));
    }

    @SneakyThrows
    public Optional<RMethod> getMethod(@NonNull final Object object, @NonNull final Predicate<Method> predicate) {
        return getMethod(object.getClass(), predicate);
    }

    @SneakyThrows
    public Optional<RMethod> getAnyMethod(@NonNull final Class<?> clazz, @NonNull final String name) {
        return getMethod(clazz, field -> name.equals(field.getName()));
    }

    @SneakyThrows
    public Optional<RMethod> getAnyMethod(@NonNull final Object object, @NonNull final String name) {
        return getAnyMethod(object.getClass(), name);
    }
}
