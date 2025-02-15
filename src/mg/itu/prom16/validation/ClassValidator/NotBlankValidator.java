package mg.itu.prom16.validation.ClassValidator;

import mg.itu.prom16.validation.core.ContrainteValidation;

public class NotBlankValidator implements ContrainteValidation<String> {

    @Override
    public boolean isValid(String fieldValue) {
        return fieldValue != null && !fieldValue.isBlank();
    }
    
    
}
