package ru.progrm_jarvis.reflector.bytecode.asm;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.progrm_jarvis.reflector.util.ObjectUtil;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A {@link GeneratedClassNameSupplier} which produces a class name
 * by appending numeric ID (non-negative, starting from 0) to base name.
 * This class's instances are caches so that there is never more than one instance for the same base name.
 * The cache concurrency level defaults to 2 and can be changed using system property named as this class full name
 * plus ".CacheConcurrencyLevel" (if not relocated, it is
 * "ru.progrm_jarvis.reflector.bytecode.asm.IncrementingNameIdGeneratedClassNameSupplier.CacheConcurrencyLevel").
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class IncrementingNameIdGeneratedClassNameSupplier implements GeneratedClassNameSupplier {

    /**
     * Cache used to always have only one GeneratedClassNameSupplier
     * for the same path so that the same ID is never generated for it.
     */
    private static final Cache<String, IncrementingNameIdGeneratedClassNameSupplier> CACHE = CacheBuilder
            .newBuilder()
            .concurrencyLevel(Integer.parseInt(ObjectUtil.orDefault(System.getProperty(
                    IncrementingNameIdGeneratedClassNameSupplier.class.getName().concat(".CacheConcurrencyLevel")), "2"
            )))
            .build();

    /**
     * Name of a package to which to append the id.
     */
    @NonNull private final String baseName;

    /**
     * Atomic, thread-safe long which is incremented every time class name is requested
     */
    private final AtomicReference<BigInteger> id = new AtomicReference<>(BigInteger.ZERO);

    /**
     * Creates new instance of this IncrementingNameIdGeneratedClassNameSupplier.
     * So for {@code final String baseName;} {@code from(baseName) == from(baseName)} is always {@code true}.
     *
     * @param baseName name to which to append numeric ID
     * @return IncrementingNameIdGeneratedClassNameSupplier created or got from cache
     */
    @SneakyThrows
    public static IncrementingNameIdGeneratedClassNameSupplier from(@NonNull final String baseName) {
        return CACHE.get(baseName, () -> new IncrementingNameIdGeneratedClassNameSupplier(baseName));
    }

    /**
     * Returns new package name by appending numeric ID (non-negative, starting from 0) to base name.
     * It atomically increments ID after getting so there are never collisions (similar values returned).
     *
     * @return generated class name
     */
    @Override
    public String get() {
        return baseName.concat(id.getAndAccumulate(BigInteger.ONE, BigInteger::add).toString());
    }
}
