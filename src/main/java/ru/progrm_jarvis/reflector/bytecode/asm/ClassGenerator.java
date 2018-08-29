package ru.progrm_jarvis.reflector.bytecode.asm;

public interface ClassGenerator {

    public EmptyConstructorCreator newClassInstanceManipulator(Class<?> clazz);
}
