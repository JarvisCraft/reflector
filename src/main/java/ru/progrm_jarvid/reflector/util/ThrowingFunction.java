package ru.progrm_jarvid.reflector.util;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable> {

    R apply(T t) throws E;
}
