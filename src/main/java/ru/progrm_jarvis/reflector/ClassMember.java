package ru.progrm_jarvis.reflector;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

@Value
@RequiredArgsConstructor
public class ClassMember<T, R> {

    @NonNull private Class<? extends T> owner;
    @Nullable private R value;
}
