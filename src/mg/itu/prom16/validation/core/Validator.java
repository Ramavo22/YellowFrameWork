package mg.itu.prom16.validation.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class Validator {
    
    public <T> ArrayList<ViolationContraite> validate(T obj) {
        ArrayList<ViolationContraite> violations = new ArrayList<>();
        // Prendre les attributs de la classe
        Field[] fields = obj.getClass().getDeclaredFields();

        // Parcourir chaque attribut
        for (Field field : fields) {
            // Rendre le champ accessible
            field.setAccessible(true);

            // Récupérer les annotations sur l'attribut
            Annotation[] annotations = field.getAnnotations();

            // Parcourir chaque annotation
            for (Annotation annotation : annotations) {
                // Vérifier si l'annotation possède l'annotation `@Constraint`
                if (annotation.annotationType().isAnnotationPresent(Constraint.class)) {
                    // Obtenir l'annotation `@Constraint`
                    Constraint constraint = annotation.annotationType().getAnnotation(Constraint.class);

                    // Obtenir la classe du validateur
                    Class<? extends ContrainteValidation<?>> classValidator = constraint.validedBy();

                    try {
                        // Instancier le validateur
                        ContrainteValidation<Object> validatorInstance = (ContrainteValidation<Object>) classValidator.getDeclaredConstructor().newInstance();

                        // Récupérer la valeur du champ
                        Object fieldValue = field.get(obj);

                        // Faire la validation et mettre à jour le résultat
                        if (!validatorInstance.isValid(fieldValue)) {
                            ViolationContraite violation = new ViolationContraite(field.getName(), classValidator.getSimpleName(), fieldValue);
                            violations.add(violation);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return violations;
    }
}
