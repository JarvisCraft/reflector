package ru.progrm_jarvis.reflector.bytecode.mirror;

import javassist.NotFoundException;
import org.junit.jupiter.api.Test;
import ru.progrm_jarvis.reflector.bytecode.mirror.annotation.*;

import static org.junit.jupiter.api.Assertions.*;
import static ru.progrm_jarvis.reflector.TestUtil.getCtClass;
import static ru.progrm_jarvis.reflector.bytecode.mirror.BytecodeMirrorer.*;
import static ru.progrm_jarvis.reflector.bytecode.mirror.MirroringPolicy.*;

class BytecodeMirrorerTest {

    @Test
    void testGetFieldsMirroringPolicy() throws NotFoundException, ClassNotFoundException {
        {
            final class TestClass {}

            assertSame(ANNOTATED, getFieldsMirroringPolicy(getCtClass(TestClass.class)));
        }

        {
            @MirrorFields
            final class TestClass {}

            assertSame(ALL, getFieldsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorFields(NONE)
            final class TestClass {}

            assertSame(NONE, getFieldsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorFields(ANNOTATED)
            final class TestClass {}

            assertSame(ANNOTATED, getFieldsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorFields(VISIBLE)
            final class TestClass {}

            assertSame(VISIBLE, getFieldsMirroringPolicy(getCtClass(TestClass.class)));
        }

        {
            @MirrorAll
            final class TestClass {}

            assertSame(ALL, getFieldsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll(NONE)
            final class TestClass {}

            assertSame(NONE, getFieldsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll(ANNOTATED)
            final class TestClass {}

            assertSame(ANNOTATED, getFieldsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll(VISIBLE)
            final class TestClass {}

            assertSame(VISIBLE, getFieldsMirroringPolicy(getCtClass(TestClass.class)));
        }

        {
            @MirrorAll(NONE)
            @MirrorFields
            final class TestClass {}

            assertSame(ALL, getFieldsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll(ALL)
            @MirrorFields(NONE)
            final class TestClass {}

            assertSame(NONE, getFieldsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll(NONE)
            @MirrorFields(ANNOTATED)
            final class TestClass {}

            assertSame(ANNOTATED, getFieldsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll(NONE)
            @MirrorFields(VISIBLE)
            final class TestClass {}

            assertSame(VISIBLE, getFieldsMirroringPolicy(getCtClass(TestClass.class)));
        }
    }

    @Test
    void testGetMethodsMirroringPolicy() throws NotFoundException, ClassNotFoundException {
        {
            final class TestClass {}

            assertSame(ANNOTATED, getMethodsMirroringPolicy(getCtClass(TestClass.class)));
        }

        {
            @MirrorMethods
            final class TestClass {}

            assertSame(ALL, getMethodsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorMethods(NONE)
            final class TestClass {}

            assertSame(NONE, getMethodsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorMethods(ANNOTATED)
            final class TestClass {}

            assertSame(ANNOTATED, getMethodsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorMethods(VISIBLE)
            final class TestClass {}

            assertSame(VISIBLE, getMethodsMirroringPolicy(getCtClass(TestClass.class)));
        }

        {
            @MirrorAll
            final class TestClass {}

            assertSame(ALL, getMethodsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll(NONE)
            final class TestClass {}

            assertSame(NONE, getMethodsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll(ANNOTATED)
            final class TestClass {}

            assertSame(ANNOTATED, getMethodsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll(VISIBLE)
            final class TestClass {}

            assertSame(VISIBLE, getMethodsMirroringPolicy(getCtClass(TestClass.class)));
        }

        {
            @MirrorAll(NONE)
            @MirrorMethods
            final class TestClass {}

            assertSame(ALL, getMethodsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll(ALL)
            @MirrorMethods(NONE)
            final class TestClass {}

            assertSame(NONE, getMethodsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll(NONE)
            @MirrorMethods(ANNOTATED)
            final class TestClass {}

            assertSame(ANNOTATED, getMethodsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll(NONE)
            @MirrorMethods(VISIBLE)
            final class TestClass {}

            assertSame(VISIBLE, getMethodsMirroringPolicy(getCtClass(TestClass.class)));
        }
    }

    @Test
    void testGetConstructorsMirroringPolicy() throws NotFoundException, ClassNotFoundException {
        {
            final class TestClass {}

            assertSame(ANNOTATED, getConstructorsMirroringPolicy(getCtClass(TestClass.class)));
        }
        
        {
            @MirrorConstructors
            final class TestClass {}

            assertSame(ALL, getConstructorsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorConstructors(NONE)
            final class TestClass {}

            assertSame(NONE, getConstructorsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorConstructors(ANNOTATED)
            final class TestClass {}

            assertSame(ANNOTATED, getConstructorsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorConstructors(VISIBLE)
            final class TestClass {}

            assertSame(VISIBLE, getConstructorsMirroringPolicy(getCtClass(TestClass.class)));
        }

        {
            @MirrorAll
            final class TestClass {}

            assertSame(ALL, getConstructorsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll(NONE)
            final class TestClass {}

            assertSame(NONE, getConstructorsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll(ANNOTATED)
            final class TestClass {}

            assertSame(ANNOTATED, getConstructorsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll(VISIBLE)
            final class TestClass {}

            assertSame(VISIBLE, getConstructorsMirroringPolicy(getCtClass(TestClass.class)));
        }
        
        {
            @MirrorAll(NONE)
            @MirrorConstructors
            final class TestClass {}

            assertSame(ALL, getConstructorsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll(ALL)
            @MirrorConstructors(NONE)
            final class TestClass {}

            assertSame(NONE, getConstructorsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll(NONE)
            @MirrorConstructors(ANNOTATED)
            final class TestClass {}

            assertSame(ANNOTATED, getConstructorsMirroringPolicy(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll(NONE)
            @MirrorConstructors(VISIBLE)
            final class TestClass {}

            assertSame(VISIBLE, getConstructorsMirroringPolicy(getCtClass(TestClass.class)));
        }
    }

    @Test
    void testIsMirrorClassInitializers() throws NotFoundException, ClassNotFoundException {
        {
            @MirrorClassInitializers
            final class TestClass {}

            assertTrue(isMirrorClassInitializers(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll
            @MirrorClassInitializers
            final class TestClass {}

            assertTrue(isMirrorClassInitializers(getCtClass(TestClass.class)));
        }
        {
            @MirrorClassInitializers(false)
            final class TestClass {}

            assertFalse(isMirrorClassInitializers(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll
            final class TestClass {}

            assertFalse(isMirrorClassInitializers(getCtClass(TestClass.class)));
        }
        {
            @MirrorAll
            @MirrorClassInitializers(false)
            final class TestClass {}

            assertFalse(isMirrorClassInitializers(getCtClass(TestClass.class)));
        }
    }
}