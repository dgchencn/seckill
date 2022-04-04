package com.wyu.seckill.common;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ObjectValidator implements ErrorCode {

    //hibernate-validator提供的约束验证框架
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public Map<String, String> validate(Object obj) {
        if (obj == null) {
            return null;
        }

        Map<String, String> result = new HashMap<>();

        Set<ConstraintViolation<Object>> set = validator.validate(obj);
        if (set != null && set.size() > 0) {
            for (ConstraintViolation cv : set) {
                result.put(cv.getPropertyPath().toString(), cv.getMessage());
            }
        }

        return result;
    }

}
