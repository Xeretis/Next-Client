package me.lor3mipsum.next.api.util.misc;

import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Field;

public class ReflectUtils {

    public static Field getField(final Class<?> cls, String obfName, String deobfName) {
        if (cls == null)
            return null;

        Field field = null;
        for (Class<?> cls1 = cls; cls1 != null; cls1 = cls1.getSuperclass()) {
            try {
                field = cls1.getDeclaredField(obfName);
            } catch (Exception e) {
                try {
                    field = cls1.getDeclaredField(deobfName);
                } catch (Exception e1) {
                    continue;
                }
            }

            if (!field.isAccessible()) {
                field.setAccessible(true);
            }

            return field;
        }

        for (final Class<?> class1 : ClassUtils.getAllInterfaces(cls)) {
            try {
                field = class1.getField(obfName);
            } catch (Exception e) {
                try {
                    field = class1.getField(deobfName);
                } catch (Exception e1) {
                    continue;
                }
            }

            return field;
        }

        throw new RuntimeException("Error reflecting field: " + deobfName + "/" + obfName + " @" + cls.getSimpleName());
    }

    public static void writeField(final Object target, final Object value, String obfName, String deobfName) {
        if (target == null)
            return;

        final Class<?> cls = target.getClass();
        final Field field = getField(cls, obfName, deobfName);

        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        try {
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Error writing reflected field: " + deobfName + "/" + obfName + " @" + target.getClass().getSimpleName());
        }

    }

}
