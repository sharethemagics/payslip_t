package com.treffer.payslip.dao;

import java.util.Date;
import java.util.List;

import com.treffer.payslip.to.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, Long> { // Long: Type of Employee ID.
/*
    @Override
    public List<Employee> findAll();*/

    Employee findByEmpId(double empId);



   /* List<Employee> findByFullNameLike(String fullName);

    List<Employee> findByHireDateGreaterThan(Date hireDate);

    // Supports native JSON query string
    @Query("{fullName:'?0'}")
    List<Employee> findCustomByFullName(String fullName);*/

}