package ru.progrm_jarvis.reflector.util;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable> {

    R apply(T t) throws E;
}
