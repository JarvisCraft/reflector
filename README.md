# reflector
A wrapper for Java reflection with reach support for functional programming

master: 
[![Build Status](https://travis-ci.org/JarvisCraft/reflector.svg?branch=master)](https://travis-ci.org/JarvisCraft/reflector) 
development: 
[![Build Status](https://travis-ci.org/JarvisCraft/reflector.svg?branch=development)](https://travis-ci.org/JarvisCraft/reflector)
## Main purpose
**Reflector** provides comfortable and fast access to Java's reflection-API wrapping common objects with fast methods
which don't throw any checked exceptions. This approach lets you use this in initialization-statements anywhere in your class.
## Usage
### Adding as Maven dependency [![Maven Central](https://img.shields.io/maven-central/v/ru.progrm-jarvis.reflector/reflector.svg)](https://mvnrepository.com/artifact/ru.progrm-jarvis.reflector/reflector/)
Reflector is available in Maven Central so add the following to your `pom.xml`s `dependencies` tag in order to add it as dependency.
```xml
<dependency>
    <groupId>ru.progrm-jarvis.reflector</groupId>
    <artifactId>reflector</artifactId>
    <version>1.0</version>
</dependency>
```
###### Notes:
>`version` may be any valid version, for full list of them
see [Maven Central versions](https://mvnrepository.com/artifact/ru.progrm-jarvis.reflector/reflector/)

>So called nightly builds are also available with `-SNAPSHOT` suffix after version name as by Maven specification
### Reflection
#### Fields
This example shows how you can work with fields using reflector
```java
import ru.progrm_jarvis.reflector.Reflector;
import ru.progrm_jarvis.reflector.wrapper.FieldWrapper;

public class StringModifier {
    private static final FieldWrapper<String, char[]> STRING_CLASS__VALUE_FIELD = Reflector.getField(String.class, "value");
    
    public static char[] getStringBackend(final String string) {
        return STRING_CLASS__VALUE_FIELD.getValue(string);
    }
    
    public static void setStringBackend(final String string, final char[] value) {
        STRING_CLASS__VALUE_FIELD.setValue(string, value);
    }
    
    public static char[] setStringBackend(final String string, final char[] value) {
        return STRING_CLASS__VALUE_FIELD.updateValue(string, value);
    }
}
```
###### Note
>Although reflector is trying hard to be able to set values of fields ignoring access-limitations and `final` keyword,
it is not able to change the value of `static final` field due to JVM limitation.
### Goals
- [x] Develop core
- [x] Configure Travis-CI
- [x] Deploy to Maven Central automatically
- [ ] Fully cover with JavaDocs
- [ ] Create wiki and reach ReadMe
