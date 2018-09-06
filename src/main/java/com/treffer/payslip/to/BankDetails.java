package com.treffer.payslip.to;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigInteger;

@Document(collection = "BankDetails")

public class BankDetails {
    @Id
    private BigInteger _id;

    @Field(value = "Emp")
    private String empId;

    @Field(value = "bankname")
    private String bankname;

    @Field(value = "accnumber")
    private Double accnum;

    @Field(value = "branch")
    private String branch;

    @Field(value = "paiddate")
    private String paidate;

    @Field(value = "modeofpymt")
    private String pymt_Mode;

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getBankName() {
        return bankname;
    }

    public void setBankName(String bankName) {
        this.bankname = bankName;
    }

    public Double getAccnum() {
        return accnum;
    }

    public void setAccnum(Double accnum) {
        this.accnum = accnum;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getPaidate() {
        return paidate;
    }

    public void setPaidate(String paidate) {
        this.paidate = paidate;
    }

    public String getPymt_Mode() {
        return pymt_Mode;
    }

    public void setPymt_Mode(String pymt_Mode) {
        this.pymt_Mode = pymt_Mode;
    }


}

