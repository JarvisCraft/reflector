package ru.progrm_jarvis.reflector.bytecode.asm;

/**
 * An object to define classes at runtime from their bytecode
 */
@FunctionalInterface
public interface ClassDefiner {

    /**
     * Defines a class at runtime from its bytecode.
     *
     * @param parentLoader parent class loader
     * @param className name of a class to use for its defining
     * @param bytecode bytecode of a class
     * @return defined class
     */
    Class<?> defineClass(ClassLoader parentLoader, String className, byte[] bytecode);
}
