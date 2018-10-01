package ru.progrm_jarvis.reflector.bytecode;

import javassist.bytecode.annotation.*;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;

import javax.annotation.Nonnull;
import java.lang.reflect.Array;

@UtilityClass
public class JavassistUtil {

    ///////////////////////////////////////////////////////////////////////////
    // Array
    ///////////////////////////////////////////////////////////////////////////

    public MemberValue[] getArray(@Nonnull final Annotation annotation, @Nonnull final String name) {
        return ((ArrayMemberValue) annotation.getMemberValue(name)).getValue();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Primitives & String & Annotation
    ///////////////////////////////////////////////////////////////////////////

    public boolean getBoolean(@NonNull final Annotation annotation, @NonNull final String name) {
        return ((BooleanMemberValue) annotation.getMemberValue(name)).getValue();
    }

    public boolean[] getBooleanArray(@Nonnull final Annotation annotation, @Nonnull final String name) {
        val memberValues = getArray(annotation, name);
        val length = memberValues.length;

        val values = new boolean[length];
        for (var i = 0; i < length; i++) values[i] = ((BooleanMemberValue) memberValues[i]).getValue();

        return values;
    }

    public char getChar(@NonNull final Annotation annotation, @NonNull final String name) {
        return ((CharMemberValue) annotation.getMemberValue(name)).getValue();
    }

    public char[] getCharArray(@Nonnull final Annotation annotation, @Nonnull final String name) {
        val memberValues = getArray(annotation, name);
        val length = memberValues.length;

        val values = new char[length];
        for (var i = 0; i < length; i++) values[i] = ((CharMemberValue) memberValues[i]).getValue();

        return values;
    }

    public byte getByte(@NonNull final Annotation annotation, @NonNull final String name) {
        return ((ByteMemberValue) annotation.getMemberValue(name)).getValue();
    }

    public byte[] getByteArray(@Nonnull final Annotation annotation, @Nonnull final String name) {
        val memberValues = getArray(annotation, name);
        val length = memberValues.length;

        val values = new byte[length];
        for (var i = 0; i < length; i++) values[i] = ((ByteMemberValue) memberValues[i]).getValue();

        return values;
    }

    public short getShort(@NonNull final Annotation annotation, @NonNull final String name) {
        return ((ShortMemberValue) annotation.getMemberValue(name)).getValue();
    }

    public short[] getShortArray(@Nonnull final Annotation annotation, @Nonnull final String name) {
        val memberValues = getArray(annotation, name);
        val length = memberValues.length;

        val values = new short[length];
        for (var i = 0; i < length; i++) values[i] = ((ShortMemberValue) memberValues[i]).getValue();

        return values;
    }

    public int getInt(@NonNull final Annotation annotation, @NonNull final String name) {
        return ((IntegerMemberValue) annotation.getMemberValue(name)).getValue();
    }

    public int[] getIntArray(@Nonnull final Annotation annotation, @Nonnull final String name) {
        val memberValues = getArray(annotation, name);
        val length = memberValues.length;

        val values = new int[length];
        for (var i = 0; i < length; i++) values[i] = ((IntegerMemberValue) memberValues[i]).getValue();

        return values;
    }

    public long getLong(@NonNull final Annotation annotation, @NonNull final String name) {
        return ((LongMemberValue) annotation.getMemberValue(name)).getValue();
    }

    public long[] getLongArray(@Nonnull final Annotation annotation, @Nonnull final String name) {
        val memberValues = getArray(annotation, name);
        val length = memberValues.length;

        val values = new long[length];
        for (var i = 0; i < length; i++) values[i] = ((LongMemberValue) memberValues[i]).getValue();

        return values;
    }

    public float getFloat(@NonNull final Annotation annotation, @NonNull final String name) {
        return ((FloatMemberValue) annotation.getMemberValue(name)).getValue();
    }

    public float[] getFloatArray(@Nonnull final Annotation annotation, @Nonnull final String name) {
        val memberValues = getArray(annotation, name);
        val length = memberValues.length;

        val values = new float[length];
        for (var i = 0; i < length; i++) values[i] = ((FloatMemberValue) memberValues[i]).getValue();

        return values;
    }

    public double getDouble(@NonNull final Annotation annotation, @NonNull final String name) {
        return ((DoubleMemberValue) annotation.getMemberValue(name)).getValue();
    }

    public double[] getDoubleArray(@Nonnull final Annotation annotation, @Nonnull final String name) {
        val memberValues = getArray(annotation, name);
        val length = memberValues.length;

        val values = new double[length];
        for (var i = 0; i < length; i++) values[i] = ((DoubleMemberValue) memberValues[i]).getValue();

        return values;
    }

    public String getString(@NonNull final Annotation annotation, @NonNull final String name) {
        return ((StringMemberValue) annotation.getMemberValue(name)).getValue();
    }

    public String[] getStringArray(@Nonnull final Annotation annotation, @Nonnull final String name) {
        val memberValues = getArray(annotation, name);
        val length = memberValues.length;

        val values = new String[length];
        for (var i = 0; i < length; i++) values[i] = ((StringMemberValue) memberValues[i]).getValue();

        return values;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Annotation
    ///////////////////////////////////////////////////////////////////////////

    public Annotation getAnnotation(@NonNull final Annotation annotation, @NonNull final String name) {
        return ((AnnotationMemberValue) annotation.getMemberValue(name)).getValue();
    }

    public Annotation[] getAnnotationArray(@Nonnull final Annotation annotation, @Nonnull final String name) {
        val memberValues = getArray(annotation, name);
        val length = memberValues.length;

        val values = new Annotation[length];
        for (var i = 0; i < length; i++) values[i] = ((AnnotationMemberValue) memberValues[i]).getValue();

        return values;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Class
    ///////////////////////////////////////////////////////////////////////////

    public String getClassName(@NonNull final Annotation annotation, @NonNull final String name) {
        return ((ClassMemberValue) annotation.getMemberValue(name)).getValue();
    }

    public String[] getClassNameArray(@Nonnull final Annotation annotation, @Nonnull final String name) {
        val memberValues = getArray(annotation, name);
        val length = memberValues.length;

        val values = new String[length];
        for (var i = 0; i < length; i++) values[i] = ((ClassMemberValue) memberValues[i]).getValue();

        return values;
    }

    @SneakyThrows
    public Class<?> getClass(@Nonnull final Annotation annotation, @Nonnull final String name) {
        return Class.forName(getClassName(annotation, name));
    }

    @SneakyThrows
    public Class<?>[] getClassArray(@Nonnull final Annotation annotation, @Nonnull final String name) {
        val memberValues = getArray(annotation, name);
        val length = memberValues.length;

        val values = new Class<?>[length];
        for (var i = 0; i < length; i++) values[i] = Class.forName(((ClassMemberValue) memberValues[i]).getValue());

        return values;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Enum
    ///////////////////////////////////////////////////////////////////////////

    public String getEnumName(@NonNull final Annotation annotation, @NonNull final String name) {
        return ((EnumMemberValue) annotation.getMemberValue(name)).getValue();
    }

    public String[] getEnumNameArray(@Nonnull final Annotation annotation, @Nonnull final String name) {
        val memberValues = getArray(annotation, name);
        val length = memberValues.length;

        val values = new String[length];
        for (var i = 0; i < length; i++) values[i] = ((EnumMemberValue) memberValues[i]).getValue();

        return values;
    }

    public <E extends Enum<E>> E getEnum(@Nonnull final Annotation annotation, @Nonnull final String name,
                                         @NonNull final Class<E> enumType) {
        return Enum.valueOf(enumType, getEnumName(annotation, name));
    }

    public <E extends Enum<E>> E[] getEnumNameArray(@Nonnull final Annotation annotation, @Nonnull final String name,
                                     @NonNull final Class<E> enumType) {
        val memberValues = getArray(annotation, name);
        val length = memberValues.length;

        @SuppressWarnings("unchecked") val values = (E[]) Array.newInstance(enumType, length);
        for (var i = 0; i < length; i++) values[i] = Enum
                .valueOf(enumType, ((EnumMemberValue) memberValues[i]).getValue());

        return values;
    }
}

