package ru.progrm_jarvis.reflector.util.emptyconstructor;

public interface ClassDefiner {

    Class<?> defineClass(ClassLoader loader, String className, byte[] data);

}
