package ru.progrm_jarvis.reflector.invoke;

import java.lang.invoke.MethodHandle;

@FunctionalInterface
public interface MethodHandleInvoker {

    Object apply(MethodHandle methodHandle, Object... arguments) throws Throwable;
}
