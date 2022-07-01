package com.rafaelamaral.dscatalog.services.validation;

import com.rafaelamaral.dscatalog.dto.UserInsertDTO;
import com.rafaelamaral.dscatalog.entities.User;
import com.rafaelamaral.dscatalog.repositories.UserRepository;
import com.rafaelamaral.dscatalog.resources.exceptions.FieldMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class UserInsertValidation implements ConstraintValidator<UserInsertValid , UserInsertDTO> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UserInsertValid constraintAnnotation) {

    }

    @Override
    public boolean isValid(UserInsertDTO userInsertDTO , ConstraintValidatorContext context) {

        List<FieldMessage> list = new ArrayList<>();

        User user = userRepository.findByEmail(userInsertDTO.getEmail());
        if(user != null){
            list.add(new FieldMessage("email" , "Email já existe"));
        }

        list.forEach(fieldMessage ->{
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(fieldMessage.getMessage()).addPropertyNode(fieldMessage.getFieldName())
                    .addConstraintViolation();
        });

        return list.isEmpty();
    }
}
