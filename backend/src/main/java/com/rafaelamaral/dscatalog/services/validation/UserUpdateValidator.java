package com.rafaelamaral.dscatalog.services.validation;

import com.rafaelamaral.dscatalog.dto.UserInsertDTO;
import com.rafaelamaral.dscatalog.dto.UserUpdateDTO;
import com.rafaelamaral.dscatalog.entities.User;
import com.rafaelamaral.dscatalog.repositories.UserRepository;
import com.rafaelamaral.dscatalog.resources.exceptions.FieldMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid , UserUpdateDTO> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UserUpdateValid constraintAnnotation) {

    }

    @Override
    public boolean isValid(UserUpdateDTO userUpdateDTO, ConstraintValidatorContext context) {

        @SuppressWarnings("unchecked")
        var uriVars = (Map<String , String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        long userIdUpdated = Long.parseLong(uriVars.get("id"));

        List<FieldMessage> list = new ArrayList<>();

        User user = userRepository.findByEmail(userUpdateDTO.getEmail());
        if(user != null && userIdUpdated != user.getId()){
            list.add(new FieldMessage("email" , "Email não pode ser usado pois já está em uso"));
        }

        list.forEach(fieldMessage -> {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(fieldMessage.getMessage()).addPropertyNode(fieldMessage.getFieldName())
                    .addConstraintViolation();
        });

        return list.isEmpty();
    }
}
