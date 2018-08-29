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
     * @return value of this field (static)
     * @throws NullPointerException if {@code object} is {@code null} but this field is not static
     */
    V getValue(T instance);

    /**
     * Gets value of this field on no instance (which means that static value is to be got)
     * ignoring any limitations if possible.
     *
     * @return value of this {@code static} field
     * @throws NullPointerException if this field is not {@code static}
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
     * @param value value to set to this field
     * @throws NullPointerException if this field is not {@code static}
     */
    void setValue(V value);

    // updates (get and change)

    /**
     * Updates value of this field ignoring any limitations if possible.
     *
     * @param instance instance of which field's value is set
     * @param value value to set to this field
     * @return previous value of this field
     * @throws NullPointerException if {@code object} is {@code null} but this field is not static
     */
    default V updateValue(T instance, V value) {
        val oldValue = getValue(instance);
        setValue(instance, value);

        return oldValue;
    }

    /**
     * Updates value of this field on no instance (which means that static value is to be updated)
     * ignoring any limitations if possible.
     *
     * @param value value to set to this field
     * @return previous value of this field
     * @throws NullPointerException if {@code object} is {@code null} but this field is not static
     */
    default V updateValue(V value) {
        val oldValue = getValue();
        setValue(value);

        return oldValue;
    }

    /**
     * Updates value of this field based on previous value using function given ignoring any limitations if possible.
     *
     * @param instance instance of which field's value is set
     * @param operator operator to create new value based on old
     * @return previous value of this field
     * @throws NullPointerException if {@code object} is {@code null} but this field is not static
     */
    default V updateValue(T instance, @NonNull UnaryOperator<V> operator) {
        val oldValue = getValue(instance);
        setValue(instance, operator.apply(oldValue));

        return oldValue;
    }

    /**
     * Updates value of this field based on previous value using function given
     * on no instance (which means that static value is to be updated) ignoring any limitations if possible.
     *
     * @param operator operator to create new value based on old
     * @return previous value of this field
     * @throws NullPointerException if {@code object} is {@code null} but this field is not static
     */
    default V updateValue(@NonNull UnaryOperator<V> operator) {
        val oldValue = getValue();
        setValue(operator.apply(oldValue));

        return oldValue;
    }

    //computes (change and get)

    default V computeValue(T instance, @NonNull UnaryOperator<V> operator) {
        val newValue = operator.apply(getValue(instance));
        setValue(instance, newValue);

        return newValue;
    }

    default V computeValue(@NonNull UnaryOperator<V> operator) {
        val newValue = operator.apply(getValue());
        setValue(newValue);

        return newValue;
    }
}
