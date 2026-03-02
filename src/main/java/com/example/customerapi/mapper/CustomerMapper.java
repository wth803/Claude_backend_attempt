package com.example.customerapi.mapper;

import com.example.customerapi.model.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import java.util.List;
import java.util.Map;

@Mapper
public interface CustomerMapper {

    List<Customer> findAll(Map<String, Object> params);

    long countAll(Map<String, Object> params);

    Customer findById(Integer id);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Customer customer);

    void update(Customer customer);

    void deactivate(Integer id);

    long countActive();
}
