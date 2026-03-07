package com.mutrix.prepa.cors;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface UseCase {

    @AliasFor(annotation = Component.class)
    String value() default "";
}
