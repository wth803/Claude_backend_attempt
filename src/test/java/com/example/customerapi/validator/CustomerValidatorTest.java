package com.example.customerapi.validator;

import com.example.customerapi.dto.CustomerCreateRequest;
import com.example.customerapi.dto.CustomerUpdateRequest;
import com.example.customerapi.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CustomerValidatorTest {

    private CustomerValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CustomerValidator();
    }

    @Test
    void validCreateRequest_doesNotThrow() {
        CustomerCreateRequest req = new CustomerCreateRequest("Alice", "alice@example.com", "1234567890", "123 Main St");
        assertThatCode(() -> validator.validateForCreate(req)).doesNotThrowAnyException();
    }

    @Test
    void invalidEmail_noAt_throwsBusinessException() {
        CustomerCreateRequest req = new CustomerCreateRequest("Alice", "aliceexample.com", "1234567890", "123 Main St");
        assertThatThrownBy(() -> validator.validateForCreate(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("email");
    }

    @Test
    void invalidPhone_notTenDigits_throwsBusinessException() {
        CustomerCreateRequest req = new CustomerCreateRequest("Alice", "alice@example.com", "12345", "123 Main St");
        assertThatThrownBy(() -> validator.validateForCreate(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Phone");
    }

    @Test
    void blankName_throwsBusinessException() {
        CustomerCreateRequest req = new CustomerCreateRequest("   ", "alice@example.com", "1234567890", "123 Main St");
        assertThatThrownBy(() -> validator.validateForCreate(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Name");
    }

    @Test
    void blankAddress_throwsBusinessException() {
        CustomerCreateRequest req = new CustomerCreateRequest("Alice", "alice@example.com", "1234567890", "");
        assertThatThrownBy(() -> validator.validateForCreate(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Address");
    }

    @Test
    void validUpdateRequest_doesNotThrow() {
        CustomerUpdateRequest req = new CustomerUpdateRequest("Bob", "bob@example.com", "0987654321", "456 Elm St");
        assertThatCode(() -> validator.validateForUpdate(req)).doesNotThrowAnyException();
    }

    @Test
    void invalidEmail_updateRequest_throwsBusinessException() {
        CustomerUpdateRequest req = new CustomerUpdateRequest("Bob", "bobexample", "0987654321", "456 Elm St");
        assertThatThrownBy(() -> validator.validateForUpdate(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("email");
    }
}
