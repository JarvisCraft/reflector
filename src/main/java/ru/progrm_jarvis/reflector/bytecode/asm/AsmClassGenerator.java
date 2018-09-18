package ru.progrm_jarvis.reflector.bytecode.asm;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

@AllArgsConstructor
public class AsmClassGenerator implements ClassGenerator {

    public static final String[] EMPTY_CONSTRUCTOR_CREATOR_CLASS_NAME_STRING_ARRAY = {
            EmptyConstructorCreator.class.getTypeName().replace('.', '/')
    };

    @NonNull private ClassDefiner classDefiner;
    @NonNull private GeneratedClassNameSupplier classNameSupplier;
    @Nullable private ClassLoader classLoader;

    public AsmClassGenerator(final ClassDefiner classDefiner) {
        this(classDefiner, IncrementingNameIdGeneratedClassNameSupplier
                .from(AsmClassGenerator.class.getPackage().getName()
                        .concat(".$$generated$$.emptyconstructor.EmptyConstructorCreator$")), null);
    }

    @SneakyThrows
    public EmptyConstructorCreator newClassInstanceManipulator(@NonNull final Class<?> clazz) {
        val className = classNameSupplier.get();

        val dotClassName = className.replace("/", ".");
        val targetClassName = clazz.getName().replace(".", "/");
        val superClassName = "sun/reflect/MagicAccessorImpl";
        val classWriter = new ClassWriter(0);
        MethodVisitor mv;

        classWriter.visit(
                52, ACC_PUBLIC + ACC_SUPER, className, null, superClassName,
                EMPTY_CONSTRUCTOR_CREATOR_CLASS_NAME_STRING_ARRAY
        );

        generateEmptyConstructor(classWriter, superClassName);
        {
            mv = classWriter.visitMethod(ACC_PUBLIC, "newInstance", "()Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, targetClassName);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }

        classWriter.visitEnd();

        val bytes = classWriter.toByteArray();
        val definedClass = classDefiner.defineClass(classLoader == null
                        ? getClass().getClassLoader() : classLoader, dotClassName, bytes);

        return (EmptyConstructorCreator) definedClass.newInstance();
    }

    private void generateEmptyConstructor(@NonNull final ClassWriter cw, @NonNull final String superClassName) {
        val methodVisitor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, superClassName, "<init>", "()V", false);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();
    }
}
