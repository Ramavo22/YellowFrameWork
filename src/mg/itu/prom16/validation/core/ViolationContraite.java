package mg.itu.prom16.validation.core;

public class ViolationContraite {
    String champs;
    String validation;
    Object value;

    public String getChamps() {
        return champs;
    }
    public void setChamps(String champs) {
        this.champs = champs;
    }
    public String getValidation() {
        return validation;
    }
    public void setValidation(String validation) {
        this.validation = validation;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }

    public ViolationContraite(String champs, String validation, Object value){
        setChamps(champs);
        setValidation(validation);
        setValue(value);
    }

    public String showException(){
       return "Erreur sur le champs: "+ getChamps() +" avec une "+getValidation()+" Validation. Vous avez fournie la valeur suivant: "+getValue();
    }
}
