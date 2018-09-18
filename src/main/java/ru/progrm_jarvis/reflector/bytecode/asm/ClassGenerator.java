package ru.progrm_jarvis.reflector.bytecode.asm;

public interface ClassGenerator {

    EmptyConstructorCreator newClassInstanceManipulator(Class<?> clazz);
}
