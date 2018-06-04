package ru.progrm_jarvis.reflector.util;

@FunctionalInterface
public interface CheckedConsumer<T> {
    void accept(T t) throws Throwable;
}
