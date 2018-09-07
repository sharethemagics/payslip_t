package com.treffer.payslip.to;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigInteger;

@Document(collection = "Emp_Payroll")
public class Payroll_details {
    @Id
    private BigInteger _id;

    @Field(value = "empno")
    private String empId;

    @Field(value = "basicPay")
    private Double basicpay;

    @Field(value = "hra")
    private Double hra;
    @Field(value = "noofdays")
    private String paiddays;
    @Field(value = "conveyance")
    private Double conveyance;
    @Field(value = "medical")
    private Double medical;
    @Field(value = "others")
    private Double others;
    @Field(value = "fuelAllowance")
    private Double fuelAllowance;
    @Field(value = "incentive")
    private Double incentive;
    @Field(value = "pf_Employer")
    private Double pfemployer;
    @Field(value = "esi_Employer")
    private Double esiemployer;
    @Field(value = "foodAllowance")
    private Double foodallowance;
    @Field(value = "accomodation")
    private Double accomodation;
    @Field(value = "festivalBonus")
    private Double festivalbonus;
    @Field(value = "serviceReward")
    private Double servicereward;
    @Field(value = "lwf_Employee")
    private Double lwfEmployee;
    @Field(value = "lwf_Employer")
    private Double lwfEmployer;
    @Field(value = "pf_Employee")
    private Double pfemployee;
    @Field(value = "esi_Employee")
    private Double esiemployee;
    @Field(value = "taxOnEmplmnt")
    private Double taxonemplmnt;
    @Field(value = "tds")
    private Double tds;
    @Field(value = "salaryAdvance")
    private Double salaryadvance;

    public String getPaiddays() {
        return paiddays;
    }

    public void setPaiddays(String paiddays) {
        this.paiddays = paiddays;
    }

    public Double getBasicpay() {
        return basicpay;
    }

    public void setBasicpay(Double basicpay) {
        this.basicpay = basicpay;
    }

    public Double getHra() {
        return hra;
    }

    public void setHra(Double hra) {
        this.hra = hra;
    }

    public Double getConveyance() {
        return conveyance;
    }

    public void setConveyance(Double conveyance) {
        this.conveyance = conveyance;
    }

    public Double getMedical() {
        return medical;
    }

    public void setMedical(Double medical) {
        this.medical = medical;
    }

    public Double getOthers() {
        return others;
    }

    public void setOthers(Double others) {
        this.others = others;
    }

    public Double getFuelAllowance() {
        return fuelAllowance;
    }

    public void setFuelAllowance(Double fuelAllowance) {
        this.fuelAllowance = fuelAllowance;
    }

    public Double getIncentive() {
        return incentive;
    }

    public void setIncentive(Double incentive) {
        this.incentive = incentive;
    }

    public Double getPfemployer() {
        return pfemployer;
    }

    public void setPfemployer(Double pfemployer) {
        this.pfemployer = pfemployer;
    }

    public Double getEsiemployer() {
        return esiemployer;
    }

    public void setEsiemployer(Double esiemployer) {
        this.esiemployer = esiemployer;
    }

    public Double getFoodallowance() {
        return foodallowance;
    }

    public void setFoodallowance(Double foodallowance) {
        this.foodallowance = foodallowance;
    }

    public Double getAccomodation() {
        return accomodation;
    }

    public void setAccomodation(Double accomodation) {
        this.accomodation = accomodation;
    }

    public Double getFestivalbonus() {
        return festivalbonus;
    }

    public void setFestivalbonus(Double festivalbonus) {
        this.festivalbonus = festivalbonus;
    }

    public Double getServicereward() {
        return servicereward;
    }

    public void setServicereward(Double servicereward) {
        this.servicereward = servicereward;
    }

    public Double getPfemployee() {
        return pfemployee;
    }

    public void setPfemployee(Double pfemployee) {
        this.pfemployee = pfemployee;
    }

    public Double getEsiemployee() {
        return esiemployee;
    }

    public void setEsiemployee(Double esiemployee) {
        this.esiemployee = esiemployee;
    }

    public Double getTaxonemplmnt() {
        return taxonemplmnt;
    }

    public void setTaxonemplmnt(Double taxonemplmnt) {
        this.taxonemplmnt = taxonemplmnt;
    }

    public Double getTds() {
        return tds;
    }

    public void setTds(Double tds) {
        this.tds = tds;
    }

    public Double getSalaryadvance() {
        return salaryadvance;
    }

    public void setSalaryadvance(Double salaryadvance) {
        this.salaryadvance = salaryadvance;
    }

    public Double getLwfEmployee() {
        return lwfEmployee;
    }

    public void setLwfEmployee(Double lwfEmployee) {
        this.lwfEmployee = lwfEmployee;
    }

    public Double getLwfEmployer() {
        return lwfEmployer;
    }

    public void setLwfEmployer(Double lwfEmployer) {
        this.lwfEmployer = lwfEmployer;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }


}

