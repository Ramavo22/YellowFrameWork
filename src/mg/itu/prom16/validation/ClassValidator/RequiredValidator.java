package mg.itu.prom16.validation.ClassValidator;

import mg.itu.prom16.validation.core.ContrainteValidation;

public class RequiredValidator implements ContrainteValidation<Object>{

    @Override
    public boolean isValid(Object fieldValue) {
        if(fieldValue == null) return false;
        return true;
    }
    
    
}
