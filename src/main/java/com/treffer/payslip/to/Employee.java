package com.treffer.payslip.to;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigInteger;

@Document(collection = "Employee")
public class Employee {

    @Id
    private BigInteger _id;

    @Field(value = "empId")
    private String empId;

    @Field(value = "empName")
    private String empName;

    @Field(value = "firstName")
    private String firstName;

    @Field(value = "lastName")
    private String lastName;

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public BigInteger getId() {
        return _id;
    }

    public void setId(BigInteger id) {
        this._id = id;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    @Override
    public String toString() {
        return "id:" + this._id + ", empNo: " + this.empId //
                + ", name : " + this.empName;
    }
}
