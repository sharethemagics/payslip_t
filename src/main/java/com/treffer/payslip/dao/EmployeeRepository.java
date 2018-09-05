package com.treffer.payslip.dao;

import java.util.Date;
import java.util.List;

import com.treffer.payslip.to.Employee;

import com.treffer.payslip.to.Payroll_details;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Employee findByEmpId(String empId);


}