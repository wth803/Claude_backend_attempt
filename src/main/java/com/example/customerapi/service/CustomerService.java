package com.example.customerapi.service;

import com.example.customerapi.dto.CustomerCreateRequest;
import com.example.customerapi.dto.CustomerResponse;
import com.example.customerapi.dto.CustomerUpdateRequest;
import com.example.customerapi.dto.PageResult;

public interface CustomerService {

    PageResult<CustomerResponse> getCustomers(int page, int size, String keyword);

    CustomerResponse getCustomerById(Integer id);

    CustomerResponse createCustomer(CustomerCreateRequest request);

    CustomerResponse updateCustomer(Integer id, CustomerUpdateRequest request);

    void deleteCustomer(Integer id);
}
