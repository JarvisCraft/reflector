package ru.progrm_jarvis.reflector.bytecode.asm;

import java.util.function.Supplier;

/**
 * Supplier of class name for generated classes which should guarantee
 * that the value returned is always different (also in multi-thread environment)
 */
@FunctionalInterface
public interface GeneratedClassNameSupplier extends Supplier<String> {

    /**
     * Supplies the caller with a new name for a generated class.
     * This method behaviour should be designed to be thread-safe
     * so that it never returns some value more than one time.
     *
     * @return name for a new generated class, unique for each call to this method
     */
    @Override
    String get();
}
