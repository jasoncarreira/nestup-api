package com.ppi.api.security;

import com.ppi.api.model.BaseEntity;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@NameBinding
@Retention(RUNTIME)
@Target({TYPE})
public @interface DataFilter {
    Class<? extends BaseEntity> value() default BaseEntity.class;
}
