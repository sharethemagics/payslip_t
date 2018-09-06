package com.treffer.payslip.to;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigInteger;

@Document(collection = "Employee")
public class Employee {

    @Id
    private BigInteger _id;

    @Field(value = "empno")
    private String empId;

    @Field(value = "EmpName")
    private String empName;

    @Field(value = "Designation")
    private String desgination;

    @Field(value = "DOB")
    private String dob;
    @Field(value = "DOJ")
    private String doj;
    @Field(value = "PAN")
    private String panno;
    @Field(value = "UAN")
    private String uan;
    @Field(value = "ESI")
    private String esi;
    @Field(value = "AADHAAR")
    private String aadhaar;
    @Field(value = "bankname")
    private String bankname;
    @Field(value = "accnumber")
    private String accnum;
    @Field(value = "branch")
    private String branch;
    @Field(value = "fathername")
    private String fathername;
    @Field(value = "paiddate")
    private String paidate;
    @Field(value = "modeofpymt")
    private String pymt_Mode;

    public String getDoj() {
        return doj;
    }

    public void setDoj(String doj) {
        this.doj = doj;
    }

    public String getPanno() {
        return panno;
    }

    public void setPanno(String panno) {
        this.panno = panno;
    }

    public String getUan() {
        return uan;
    }

    public void setUan(String uan) {
        this.uan = uan;
    }

    public String getEsi() {
        return esi;
    }

    public void setEsi(String esi) {
        this.esi = esi;
    }

    public String getAadhaar() {
        return aadhaar;
    }

    public void setAadhaar(String aadhaar) {
        this.aadhaar = aadhaar;
    }

    public String getFathername() {
        return fathername;
    }

    public void setFathername(String fathername) {
        this.fathername = fathername;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getAccnum() {
        return accnum;
    }

    public void setAccnum(String accnum) {
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

    public String getDesgination() {
        return desgination;
    }

    public void setDesgination(String desgination) {
        this.desgination = desgination;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }


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


    @Override
    public String toString() {
        return "id:" + this._id + ", empNo: " + this.empId //
                + ", name : " + this.empName;
    }
}
