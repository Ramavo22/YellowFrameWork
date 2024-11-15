package mg.itu.prom16.validation.core;

public interface ContrainteValidation<T> {

    public boolean isValid(T fieldValue);
    
}