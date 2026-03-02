package com.example.customerapi.validator;

import com.example.customerapi.dto.CustomerCreateRequest;
import com.example.customerapi.dto.CustomerUpdateRequest;
import com.example.customerapi.exception.BusinessException;
import com.example.customerapi.util.ValidationUtil;
import org.springframework.stereotype.Component;

@Component
public class CustomerValidator {

    public void validateForCreate(CustomerCreateRequest request) {
        if (request == null) throw new BusinessException("Request must not be null");
        validateCommonFields(request.getName(), request.getEmail(), request.getPhone(), request.getAddress());
    }

    public void validateForUpdate(CustomerUpdateRequest request) {
        if (request == null) throw new BusinessException("Request must not be null");
        validateCommonFields(request.getName(), request.getEmail(), request.getPhone(), request.getAddress());
    }

    private void validateCommonFields(String name, String email, String phone, String address) {
        if (!ValidationUtil.isNotBlank(name)) {
            throw new BusinessException("Name must not be blank");
        }
        if (!ValidationUtil.isNotBlank(address)) {
            throw new BusinessException("Address must not be blank");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new BusinessException("Invalid email format");
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new BusinessException("Phone must be exactly 10 digits");
        }
    }
}
