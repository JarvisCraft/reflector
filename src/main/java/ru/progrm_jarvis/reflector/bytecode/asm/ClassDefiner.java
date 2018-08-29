package ru.progrm_jarvis.reflector.bytecode.asm;

public interface ClassDefiner {

    Class<?> defineClass(ClassLoader loader, String className, byte[] data);
}
