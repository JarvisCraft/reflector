package ru.progrm_jarvis.reflector.util;

import lombok.Value;

@Value(staticConstructor = "of")
public class Possible<T> {

    private static final Possible<?> EMPTY = new Possible<>(null);

    private T value;

    @SuppressWarnings("unchecked")
    public static <T> Possible<T> empty() {
        return (Possible<T>) EMPTY;
    }
}
