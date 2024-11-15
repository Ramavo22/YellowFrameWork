package mg.itu.prom16.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mg.itu.prom16.validation.ClassValidator.NotBlankValidator;
import mg.itu.prom16.validation.core.Constraint;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validedBy = NotBlankValidator.class)
public @interface NotBlank {
    
}
