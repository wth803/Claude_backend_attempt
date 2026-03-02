package com.example.customerapi.controller;

import com.example.customerapi.dto.*;
import com.example.customerapi.exception.BusinessException;
import com.example.customerapi.service.CustomerService;
import com.example.customerapi.validator.CustomerValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private CustomerValidator customerValidator;

    private CustomerResponse sampleResponse() {
        return CustomerResponse.builder()
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
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getCustomers_returns200() throws Exception {
        PageResult<CustomerResponse> page = PageResult.<CustomerResponse>builder()
                .total(1).page(1).size(10).data(List.of(sampleResponse())).build();
        when(customerService.getCustomers(1, 10, null)).thenReturn(page);

        mockMvc.perform(get("/api/customers").param("page", "1").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Alice"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getCustomerById_returns200() throws Exception {
        when(customerService.getCustomerById(1)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getCustomerById_notFound_returns404() throws Exception {
        when(customerService.getCustomerById(99))
                .thenThrow(new BusinessException("Customer not found with id: 99"));

        mockMvc.perform(get("/api/customers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createCustomer_returns201() throws Exception {
        CustomerCreateRequest req = new CustomerCreateRequest("Alice", "alice@example.com", "1234567890", "123 Main St");
        when(customerService.createCustomer(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/customers").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerCode").value("20240101120000-1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCustomer_returns200() throws Exception {
        CustomerUpdateRequest req = new CustomerUpdateRequest("Alice Updated", "alice@example.com", "1234567890", "456 Elm St");
        CustomerResponse updated = sampleResponse();
        updated.setName("Alice Updated");
        when(customerService.updateCustomer(eq(1), any())).thenReturn(updated);

        mockMvc.perform(put("/api/customers/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Updated"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteCustomer_returns204() throws Exception {
        doNothing().when(customerService).deleteCustomer(1);

        mockMvc.perform(delete("/api/customers/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
