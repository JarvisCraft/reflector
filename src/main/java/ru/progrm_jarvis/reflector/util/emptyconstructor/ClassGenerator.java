package ru.progrm_jarvis.reflector.util.emptyconstructor;

public interface ClassGenerator {

    public EmptyConstructorCreator newClassInstanceManipulator(Class<?> clazz);
}
