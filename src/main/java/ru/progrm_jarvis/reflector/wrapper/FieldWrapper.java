/*
 *  Copyright 2018 Petr P.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.progrm_jarvis.reflector.wrapper;

import lombok.NonNull;
import lombok.val;

import java.lang.reflect.Field;
import java.util.function.UnaryOperator;

/**
 * Wrapper for {@link Field}
 *
 * @param <T> type of class containing this field
 * @param <V> type of value contained in this field
 */
public interface FieldWrapper<T, V> extends ReflectorWrapper<Field> {

    /**
     * Gets value of this field ignoring any limitations if possible.
     *
     * @param instance instance of which field's value is get
     * @return value of this field
     * @throws NullPointerException if {@code object} is {@code null} but this field is not static
     */
    V getValue(T instance);

    /**
     * Gets value of this field on no instance (which means that static value is to be got)
     * ignoring any limitations if possible.
     *
     * @return value of this static field
     * @throws NullPointerException if this field is not static
     */
    V getValue();

    /**
     * Sets value of this field ignoring any limitations if possible.
     *
     * @param instance instance of which field's value is set
     * @param value value to set to this field
     * @throws NullPointerException if {@code object} is {@code null} but this field is not static
     */
    void setValue(T instance, V value);

    /**
     * Sets value of this field on no instance (which means that static value is to be set)
     * ignoring any limitations if possible.
     *
     * @param value value to set to this static field
     * @throws NullPointerException if this field is not static
     */
    void setValue(V value);

    /**
     * Updates value of this field ignoring any limitations if possible and returning previous value.
     *
     * @param instance instance of which field's value is set
     * @param value value to set to this field
     * @return previous value of this field
     * @throws NullPointerException if {@code object} is {@code null} but this field is not static
     */
    default V getAndUpdate(T instance, V value) {
        val previousValue = getValue(instance);
        setValue(instance, value);

        return previousValue;
    }

    /**
     * Updates value of {@code static} field ignoring any limitations if possible and returning previous value.
     *
     * @param value value to set to this field
     * @return previous value of this static field
     * @throws NullPointerException if this field is not static
     */
    default V getAndUpdate(V value) {
        val previousValue = getValue();
        setValue(value);

        return previousValue;
    }

    /**
     * Updates value of this field based on previous value using unary operator given
     * ignoring any limitations if possible and returning previous value.
     *
     * @param operator operator to create new value based on old
     * @return previous value of this field
     * @throws NullPointerException if {@code object} is {@code null} but this field is not static
     */
    default V getAndCompute(final T instance, @NonNull final UnaryOperator<V> operator) {
        val previousValue = getValue(instance);
        setValue(instance, operator.apply(previousValue));

        return previousValue;
    }

    /**
     * Updates value of {@code static} field based on previous value using unary operator given
     * ignoring any limitations if possible and returning previous value.
     *
     * @param operator operator to create new value based on old
     * @return previous value of this static field
     * @throws NullPointerException if this field is not static
     */
    default V getAndCompute(@NonNull final UnaryOperator<V> operator) {
        val previousValue = getValue();
        setValue(operator.apply(previousValue));

        return previousValue;
    }

    /**
     * Updates value of this field based on previous value using unary operator given
     * ignoring any limitations if possible and returning newly computed value.
     *
     * @param operator operator to create new value based on old
     * @return new value of this field
     * @throws NullPointerException if {@code object} is {@code null} but this field is not static
     */
    default V computeAndGet(final T instance, @NonNull final UnaryOperator<V> operator) {
        val newValue = operator.apply(getValue(instance));
        setValue(instance, newValue);

        return newValue;
    }

    /**
     * Updates value of {@code static} field based on previous value using unary operator given
     * ignoring any limitations if possible and returning newly computed value.
     *
     * @param operator operator to create new value based on old
     * @return new value of this static field
     * @throws NullPointerException if this field is not static
     */
    default V computeAndGet(@NonNull final UnaryOperator<V> operator) {
        val newValue = operator.apply(getValue());
        setValue(newValue);

        return newValue;
    }
}
