package com.example.customerapi.service.impl;

import com.example.customerapi.dto.CustomerCreateRequest;
import com.example.customerapi.dto.CustomerResponse;
import com.example.customerapi.dto.CustomerUpdateRequest;
import com.example.customerapi.dto.PageResult;
import com.example.customerapi.exception.BusinessException;
import com.example.customerapi.mapper.CustomerMapper;
import com.example.customerapi.model.Customer;
import com.example.customerapi.service.CustomerService;
import com.example.customerapi.util.CustomerCodeGenerator;
import com.example.customerapi.validator.CustomerValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;
    private final CustomerValidator customerValidator;

    @Override
    public PageResult<CustomerResponse> getCustomers(int page, int size, String keyword) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", (keyword != null && !keyword.isBlank()) ? keyword : null);
        params.put("limit", size);
        params.put("offset", (long) (page - 1) * size);

        long total = customerMapper.countAll(params);
        List<CustomerResponse> data = customerMapper.findAll(params).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PageResult.<CustomerResponse>builder()
                .total(total)
                .page(page)
                .size(size)
                .data(data)
                .build();
    }

    @Override
    public CustomerResponse getCustomerById(Integer id) {
        Customer customer = customerMapper.findById(id);
        if (customer == null) {
            throw new BusinessException("Customer not found with id: " + id);
        }
        return toResponse(customer);
    }

    @Override
    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        customerValidator.validateForCreate(request);

        long count = customerMapper.countActive();
        String code = CustomerCodeGenerator.INSTANCE.generateCode((int) count + 1);

        Customer customer = Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .customerCode(code)
                .active(true)
                .build();

        customerMapper.insert(customer);
        return toResponse(customer);
    }

    @Override
    public CustomerResponse updateCustomer(Integer id, CustomerUpdateRequest request) {
        customerValidator.validateForUpdate(request);

        Customer existing = customerMapper.findById(id);
        if (existing == null) {
            throw new BusinessException("Customer not found with id: " + id);
        }

        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());
        existing.setAddress(request.getAddress());
        // customerCode is preserved - not updated

        customerMapper.update(existing);
        return toResponse(existing);
    }

    @Override
    public void deleteCustomer(Integer id) {
        Customer existing = customerMapper.findById(id);
        if (existing == null) {
            throw new BusinessException("Customer not found with id: " + id);
        }
        customerMapper.deactivate(id);
    }

    private CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .customerCode(customer.getCustomerCode())
                .active(customer.getActive())
                .build();
    }
}
