package id.holigo.services.holigopinservice.web.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import id.holigo.services.holigopinservice.web.model.CreateNewPinDto;
public class EqualPinValidator implements ConstraintValidator<EqualPin, CreateNewPinDto> {

    @Override
    public boolean isValid(CreateNewPinDto value, ConstraintValidatorContext context) {
        return value.getPin().equals(value.getPinConfirmation());
    }

}
