package ru.progrm_jarvid.reflector.wrapper;

public interface RValueHolder extends RWrapper {

    Object getValue(Object object, Object... arguments);

    @SuppressWarnings("unchecked")
    default <T> T value(Object object, Object... arguments) {
        return (T) getValue(arguments);
    }
}
