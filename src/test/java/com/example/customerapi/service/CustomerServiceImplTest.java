package com.example.customerapi.service;

import com.example.customerapi.dto.CustomerCreateRequest;
import com.example.customerapi.dto.CustomerResponse;
import com.example.customerapi.dto.CustomerUpdateRequest;
import com.example.customerapi.dto.PageResult;
import com.example.customerapi.exception.BusinessException;
import com.example.customerapi.mapper.CustomerMapper;
import com.example.customerapi.model.Customer;
import com.example.customerapi.service.impl.CustomerServiceImpl;
import com.example.customerapi.validator.CustomerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private CustomerValidator customerValidator;

    @InjectMocks
    private CustomerServiceImpl service;

    private Customer sampleCustomer() {
        return Customer.builder()
                .id(1)
                .name("Alice")
                .email("alice@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .customerCode("20240101120000-1")
                .active(true)
                .build();
    }

    @Test
    void createCustomer_setsActiveAndGeneratesCode() {
        CustomerCreateRequest req = new CustomerCreateRequest("Alice", "alice@example.com", "1234567890", "123 Main St");
        doNothing().when(customerValidator).validateForCreate(req);
        when(customerMapper.countActive()).thenReturn(0L);
        doAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setId(1);
            return null;
        }).when(customerMapper).insert(any(Customer.class));

        CustomerResponse resp = service.createCustomer(req);

        assertThat(resp.getActive()).isTrue();
        assertThat(resp.getCustomerCode()).isNotBlank();
        assertThat(resp.getCustomerCode()).matches("\\d{14}-\\d+");
    }

    @Test
    void getCustomerById_returnsCustomer() {
        when(customerMapper.findById(1)).thenReturn(sampleCustomer());

        CustomerResponse resp = service.getCustomerById(1);

        assertThat(resp.getId()).isEqualTo(1);
        assertThat(resp.getName()).isEqualTo("Alice");
    }

    @Test
    void getCustomerById_throwsWhenNotFound() {
        when(customerMapper.findById(99)).thenReturn(null);

        assertThatThrownBy(() -> service.getCustomerById(99))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void updateCustomer_preservesCustomerCode() {
        Customer existing = sampleCustomer();
        when(customerMapper.findById(1)).thenReturn(existing);
        doNothing().when(customerMapper).update(any(Customer.class));

        CustomerUpdateRequest req = new CustomerUpdateRequest("Bob", "bob@example.com", "0987654321", "456 Elm St");
        doNothing().when(customerValidator).validateForUpdate(req);

        CustomerResponse resp = service.updateCustomer(1, req);

        assertThat(resp.getCustomerCode()).isEqualTo("20240101120000-1");
        assertThat(resp.getName()).isEqualTo("Bob");
    }

    @Test
    void deleteCustomer_setsInactive() {
        when(customerMapper.findById(1)).thenReturn(sampleCustomer());
        doNothing().when(customerMapper).deactivate(1);

        service.deleteCustomer(1);

        verify(customerMapper).deactivate(1);
    }

    @Test
    void deleteCustomer_throwsWhenNotFound() {
        when(customerMapper.findById(99)).thenReturn(null);

        assertThatThrownBy(() -> service.deleteCustomer(99))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void getCustomers_paginationWorks() {
        Customer c1 = sampleCustomer();
        when(customerMapper.countAll(any(Map.class))).thenReturn(1L);
        when(customerMapper.findAll(any(Map.class))).thenReturn(List.of(c1));

        PageResult<CustomerResponse> result = service.getCustomers(1, 10, null);

        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getData()).hasSize(1);
    }
}
