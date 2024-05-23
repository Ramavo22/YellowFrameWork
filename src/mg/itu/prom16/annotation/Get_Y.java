package mg.itu.prom16.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) // Cible les classes (et éventuellement les interfaces)
public @interface Get_Y {
    String url();
}
