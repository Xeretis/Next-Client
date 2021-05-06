package me.lor3mipsum.next.client.core.module.annotation;

import me.lor3mipsum.next.client.core.module.Category;
import org.lwjgl.glfw.GLFW;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mod {
    String name();
    String description();
    Category category();

    int bind() default GLFW.GLFW_KEY_UNKNOWN;
    boolean enabled() default false;
    boolean drawn() default true;
}
