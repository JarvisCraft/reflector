package ru.progrm_jarvis.reflector.util;

@FunctionalInterface
public interface CheckedFunction<T, R> {
    R apply(T t) throws Throwable;
}
