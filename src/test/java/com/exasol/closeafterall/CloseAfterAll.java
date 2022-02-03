package com.exasol.closeafterall;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a resource that should be closed after all tests were executed.
 * <p>
 * In order to make this work you need to add the {@link CloseAfterAllExtension} to your test class.
 * </p>
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CloseAfterAll {
}
