package me.lor3mipsum.next.client.event;

import me.lor3mipsum.next.client.event.types.Priority;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventTarget {
    byte value() default Priority.MEDIUM;
}
