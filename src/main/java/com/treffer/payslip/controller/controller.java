package com.treffer.payslip.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.treffer.payslip.dao.EmployeeRepository;
import com.treffer.payslip.dao.PayrollRepository;
import com.treffer.payslip.to.Employee;
import com.treffer.payslip.to.Payroll_details;
import com.treffer.payslip.to.tempTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

@RestController
@RequestMapping("/treffer")
public class controller {
    public static BaseFont fontReg;
    public static BaseFont fontBold;
    public static Font fontContent;
    public static Font fontTableHdr;
    public static Font fontTableSideHdr;
    public static Font fontTableHeading;
    public static BaseColor lightGray;
    public static BaseColor white;
    public static BaseColor red;
    private final AtomicLong counter = new AtomicLong();
    String IMG1 = "src/main/resources/logo.jpg";
    String FONT = "Calibri.ttf";
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PayrollRepository payrollRepository;

    {
        try {
            fontReg = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            fontBold = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            fontContent = new Font(fontReg, 10);
            fontTableHdr = new Font(fontBold, 9, Font.BOLD, BaseColor.WHITE);
            fontTableSideHdr = new Font(fontBold, 9, Font.BOLD, BaseColor.BLACK);
            fontTableHeading = new Font(fontBold, 12, Font.BOLD, BaseColor.BLACK);
            lightGray = WebColors.getRGBColor("#F5F5F5");
            white = WebColors.getRGBColor("#ffffff");
            red = WebColors.getRGBColor("#EF5350");
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String convertToIndianCurrency(String num) {
        BigDecimal bd = new BigDecimal(num);
        long number = bd.longValue();
        long no = bd.longValue();
        int decimal = (int) (bd.remainder(BigDecimal.ONE).doubleValue() * 100);
        int digits_length = String.valueOf(no).length();
        int i = 0;
        ArrayList<String> str = new ArrayList<>();
        HashMap<Integer, String> words = new HashMap<>();
        words.put(0, "");
        words.put(1, "One");
        words.put(2, "Two");
        words.put(3, "Three");
        words.put(4, "Four");
        words.put(5, "Five");
        words.put(6, "Six");
        words.put(7, "Seven");
        words.put(8, "Eight");
        words.put(9, "Nine");
        words.put(10, "Ten");
        words.put(11, "Eleven");
        words.put(12, "Twelve");
        words.put(13, "Thirteen");
        words.put(14, "Fourteen");
        words.put(15, "Fifteen");
        words.put(16, "Sixteen");
        words.put(17, "Seventeen");
        words.put(18, "Eighteen");
        words.put(19, "Nineteen");
        words.put(20, "Twenty");
        words.put(30, "Thirty");
        words.put(40, "Forty");
        words.put(50, "Fifty");
        words.put(60, "Sixty");
        words.put(70, "Seventy");
        words.put(80, "Eighty");
        words.put(90, "Ninety");
        String digits[] = {"", "Hundred", "Thousand", "Lakh", "Crore"};
        while (i < digits_length) {
            int divider = (i == 2) ? 10 : 100;
            number = no % divider;
            no = no / divider;
            i += divider == 10 ? 1 : 2;
            if (number > 0) {
                int counter = str.size();
                String plural = (counter > 0 && number > 9) ? "s" : "";
                String tmp = (number < 21) ? words.get(Integer.valueOf((int) number)) + " " + digits[counter] + plural : words.get(Integer.valueOf((int) Math.floor(number / 10) * 10)) + " " + words.get(Integer.valueOf((int) (number % 10))) + " " + digits[counter] + plural;
                str.add(tmp);
            } else {
                str.add("");
            }
        }

        Collections.reverse(str);
        String Rupees = String.join(" ", str).trim();

        String paise = (decimal) > 0 ? " And Paise " + words.get(Integer.valueOf((int) (decimal - decimal % 10))) + " " + words.get(Integer.valueOf((int) (decimal % 10))) : "";
        return "Rupees " + Rupees + paise + " Only";
    }

    public static Phrase prepareAddress() {

        Phrase phrase = new Phrase();
        phrase.add(new Chunk("Tre", new Font(fontBold, 10, Font.BOLD)));
        phrase.add(new Chunk("ff", new Font(fontBold, 10, Font.BOLD, BaseColor.RED)));
        phrase.add(new Chunk("er Technologies ", new Font(fontBold, 10, Font.BOLD)));
        phrase.add(new Chunk("| From concept to realization..\n", new Font(fontBold, 10, Font.NORMAL)));
        phrase.add(new Chunk("\"Mantralayam\" , 9/24-A, P & T Colony, Podanur\nCoimbatore - 641 023, Tamil Nadu, INDIA" +
                "\nPh: 96009-42800 | Email: info@tre", new Font(fontBold, 10, Font.NORMAL)));
        phrase.add(new Chunk("ff", new Font(fontBold, 10, Font.NORMAL, BaseColor.RED)));
        phrase.add(new Chunk("ertech.com\nWeb: www.tre", new Font(fontBold, 10, Font.NORMAL)));
        phrase.add(new Chunk("ff", new Font(fontBold, 10, Font.NORMAL, BaseColor.RED)));
        phrase.add(new Chunk("ertech.com", new Font(fontBold, 10, Font.NORMAL)));
        return phrase;
    }


    @RequestMapping("/greeting")
    public String greeting(@RequestParam(required=false,name="days",defaultValue="30") String workDays) {

        String pdfName;
        Payroll_details payroll_details;
        ArrayList<tempTO> earning;
        ArrayList<tempTO> ded;
        ArrayList<tempTO> bene;

        try {

            for (Employee employee : this.employeeRepository.findAll()) {
                pdfName = employee.getEmpName();
                System.out.println(pdfName);
                payroll_details = this.payrollRepository.findByEmpId(employee.getEmpId());
                OutputStream file = new FileOutputStream(new File("out/" + pdfName + ".pdf"));
                Document document = new Document(PageSize.A4);
                document.setMargins(20, 20, 20, 20);
                PdfWriter.getInstance(document, file);
                document.open();

                Phrase phrase = new Phrase();
                Paragraph para = new Paragraph();
                PdfPCell cell;

                /*Logo*/
                Image image = Image.getInstance(IMG1);
                image.setAbsolutePosition(20, 740);
                image.scalePercent(10);
                document.add(image);

                /*Company Address*/
                para.add(prepareAddress());
                para.setAlignment(Element.ALIGN_RIGHT);
                document.add(para);

                /*Table 1 Main detail*/
                PdfPTable table = new PdfPTable(6);
                table.setWidthPercentage(100);
                table.setSpacingBefore(15);

                table.setWidths(new int[]{1, 2, 1, 2, 1, 2});

                cell = new PdfPCell(new Phrase("Payslip (Form-T) for the month of August 2018", fontTableHeading));
                cell.setHorizontalAlignment(1);
                cell.setBackgroundColor(lightGray);
                cell.setColspan(6);
                cell.setPaddingBottom(6);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("Employee Details", fontTableHdr));
                cell.setColspan(2);
                cell.setBackgroundColor(red);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(5);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("Bank & Attendance Details", fontTableHdr));
                cell.setBackgroundColor(red);
                cell.setColspan(2);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(5);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("PAN / UAN / ESI / AADHAAR Details", fontTableHdr));
                cell.setBackgroundColor(red);
                cell.setColspan(2);
                cell.setPaddingBottom(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" Emp. #", fontTableSideHdr));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                cell.setPaddingBottom(5);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(employee.getEmpId(), fontContent));
                cell.setBackgroundColor(lightGray);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" Bank Name", fontTableSideHdr));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(employee.getBankname(), fontContent));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" PAN #", fontTableSideHdr));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(employee.getPanno(), fontContent));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" Emp. Name", fontTableSideHdr));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(5);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(employee.getEmpName(), fontContent));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" Account #", fontTableSideHdr));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(employee.getAccnum(), fontContent));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" UAN #", fontTableSideHdr));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(employee.getUan(), fontContent));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" Designation", fontTableSideHdr));
                cell.setPaddingBottom(5);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(employee.getDesgination(), fontContent));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" Branch", fontTableSideHdr));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(employee.getBranch(), fontContent));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ESI #", fontTableSideHdr));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(employee.getEsi(), fontContent));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" DOB", fontTableSideHdr));
                cell.setPaddingBottom(5);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(employee.getDob(), fontContent));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" Days Paid", fontTableSideHdr));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(payroll_details.getPaiddays(), fontContent));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" AADHAAR #", fontTableSideHdr));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(employee.getAadhaar(), fontContent));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" Father’s Name ", fontTableSideHdr));
                cell.setPaddingBottom(5);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(employee.getFathername(), fontContent));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" Paid Date ", fontTableSideHdr));
                cell.setPaddingBottom(5);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("31/08/2018", fontContent));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", fontTableSideHdr));

                cell.setColspan(2);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" DOJ ", fontTableSideHdr));
                cell.setPaddingBottom(5);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(employee.getDoj(), fontContent));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" Mode of PYMT", fontTableSideHdr));
                cell.setPaddingBottom(5);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(employee.getPymt_Mode(), fontContent));
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(lightGray);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(" ", fontTableSideHdr));

                cell.setColspan(2);
                table.addCell(cell);
                document.add(table);


                PdfPTable table2 = new PdfPTable(7);
                table2.setWidthPercentage(100);
                table2.setWidths(new float[]{1.2f, 0.8f, 0.8f, 1.2f, 0.8f, 1.2f, 0.8f});
                table2.setSpacingBefore(15);
                Paragraph p;

                cell = new PdfPCell(new Phrase("Salary details", fontTableHdr));
                cell.setBackgroundColor(red);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(5);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase("Fixed (INR)", fontTableHdr));
                cell.setBackgroundColor(red);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(5);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase("Earnings (INR)", fontTableHdr));
                cell.setBackgroundColor(red);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                cell.setPaddingBottom(5);
                table2.addCell(cell);

                cell = new PdfPCell(new Phrase("Additional Benefits", fontTableHdr));
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(0);
                cell.setBackgroundColor(red);
                cell.setPaddingBottom(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase("Amount (INR)", fontTableHdr));
                cell.setBackgroundColor(red);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(5);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase("Deductions", fontTableHdr));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(red);
                cell.setPaddingBottom(5);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase("Amount (INR)", fontTableHdr));
                cell.setBackgroundColor(red);
                cell.setPaddingBottom(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.addCell(cell);

                earning = new ArrayList<>();
                ded = new ArrayList<>();
                bene = new ArrayList<>();
                tempTO temp = new tempTO();


                payroll_details = this.payrollRepository.findByEmpId(employee.getEmpId());
                if (payroll_details.getBasicpay() > 0) {
                    temp.setKey("Basic Pay");
                    System.out.println(payroll_details.getBasicpay() + " " + new DecimalFormat("##.##").format(payroll_details.getBasicpay()) + " " +
                            String.format("%.2f", payroll_details.getBasicpay()));

                    temp.setValue(String.format("%.2f", payroll_details.getBasicpay()));
                    earning.add(temp);

                }
                if (payroll_details.getHra() > 0) {
                    temp = new tempTO();
                    temp.setKey("HRA");
                    temp.setValue(String.format("%.2f", payroll_details.getHra()));
                    earning.add(temp);
                }
                if (payroll_details.getConveyance() > 0) {
                    temp = new tempTO();
                    temp.setKey("Conveyance");
                    temp.setValue(String.format("%.2f", payroll_details.getConveyance()));
                    earning.add(temp);
                }
                if (payroll_details.getMedical() > 0) {
                    temp = new tempTO();
                    temp.setKey("Medical");
                    temp.setValue(String.format("%.2f", payroll_details.getMedical()));
                    earning.add(temp);
                }
                if (payroll_details.getOthers() > 0) {
                    temp = new tempTO();
                    temp.setKey("Others");
                    temp.setValue(String.format("%.2f", payroll_details.getOthers()));
                    earning.add(temp);
                }
                if (payroll_details.getFuelAllowance() > 0) {
                    temp = new tempTO();
                    temp.setKey("Fuel Allowance");
                    temp.setValue(String.format("%.2f", payroll_details.getFuelAllowance()));
                    earning.add(temp);
                }
                if (payroll_details.getIncentive() > 0) {
                    temp = new tempTO();
                    temp.setKey("Incentive");
                    temp.setValue(String.format("%.2f", payroll_details.getIncentive()));
                    earning.add(temp);
                }
                if (payroll_details.getPfemployer() > 0) {
                    temp = new tempTO();
                    temp.setKey("PF Employer");
                    temp.setValue(String.format("%.2f", payroll_details.getPfemployer()));
                    bene.add(temp);
                }
                if (payroll_details.getEsiemployer() > 0) {
                    temp = new tempTO();
                    temp.setKey("ESI Employer");
                    temp.setValue(String.format("%.2f", payroll_details.getEsiemployer()));
                    bene.add(temp);
                }
                if (payroll_details.getFoodallowance() > 0) {
                    temp = new tempTO();
                    temp.setKey("Food Allowance");
                    temp.setValue(String.format("%.2f", payroll_details.getFoodallowance()));
                    bene.add(temp);
                }
                if (payroll_details.getAccomodation() > 0) {
                    temp = new tempTO();
                    temp.setKey("Accommodation");
                    temp.setValue(String.format("%.2f", payroll_details.getAccomodation()));
                    bene.add(temp);
                }
                if (payroll_details.getFestivalbonus() > 0) {
                    temp = new tempTO();
                    temp.setKey("Festival Bonus");
                    temp.setValue(String.format("%.2f", payroll_details.getFestivalbonus()));
                    bene.add(temp);
                }
                if (payroll_details.getServicereward() > 0) {
                    temp = new tempTO();
                    temp.setKey("Service Reward");
                    temp.setValue(String.format("%.2f", payroll_details.getServicereward()));
                    bene.add(temp);
                }
                if (payroll_details.getLwfEmployer() > 0) {
                    temp = new tempTO();
                    temp.setKey("LWF Employer");
                    temp.setValue(String.format("%.2f", payroll_details.getLwfEmployer()));
                    bene.add(temp);
                }
                if (payroll_details.getPfemployee() > 0) {
                    temp = new tempTO();
                    temp.setKey("PF Employee");
                    temp.setValue(String.format("%.2f", payroll_details.getPfemployee()));
                    ded.add(temp);
                }
                if (payroll_details.getEsiemployee() > 0) {
                    temp = new tempTO();
                    temp.setKey("ESI Employee");
                    temp.setValue(String.format("%.2f", payroll_details.getEsiemployee()));
                    ded.add(temp);
                }
                if (payroll_details.getTaxonemplmnt() > 0) {
                    temp = new tempTO();
                    temp.setKey("Tax on Employment");
                    temp.setValue(String.format("%.2f", payroll_details.getTaxonemplmnt()));
                    ded.add(temp);
                }
                if (payroll_details.getLwfEmployee() > 0) {
                    temp = new tempTO();
                    temp.setKey("LWF Employee");
                    temp.setValue(String.format("%.2f", payroll_details.getLwfEmployee()));
                    ded.add(temp);
                }
                if (payroll_details.getTds() > 0) {
                    temp = new tempTO();
                    temp.setKey("TDS");
                    temp.setValue(String.format("%.2f", payroll_details.getTds()));
                    ded.add(temp);
                }
                if (payroll_details.getSalaryadvance() > 0) {
                    temp = new tempTO();
                    temp.setKey("Salary Advace");
                    temp.setValue(String.format("%.2f", payroll_details.getSalaryadvance()));
                    ded.add(temp);
                }

                // New code added
                int dataRow = 0;
                dataRow = Math.max(earning.size(), ded.size());
                dataRow = Math.max(dataRow, bene.size());
                Double payData = 0.00;

                //bene.forEach(yy -> {System.out.println("key " + yy.getKey() + " value " + yy.getValue() + " ");});
                Double totalEarning = 0.0;
                for (int j = 0; j < dataRow; j++) {
                    if (j < earning.size()) {
                        cell = new PdfPCell(new Phrase(earning.get(j).getKey(), fontContent));
                        cell.setUseVariableBorders(true);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                        cell.setBorderWidthRight(0);
                        cell.setBorderWidthTop(0);
                        cell.setPaddingBottom(4);
                        table2.addCell(cell);
                        cell = new PdfPCell(new Phrase(earning.get(j).getValue() + "  ", fontContent));
                        cell.setUseVariableBorders(true);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                        cell.setBorderWidthTop(0);
                        cell.setPaddingBottom(4);
                        table2.addCell(cell);
                        payData = Double.parseDouble(earning.get(j).getValue()) * Integer.parseInt(payroll_details.getPaiddays()) / Integer.parseInt(workDays);
                        totalEarning = totalEarning + payData;
                        //payData = Math.ceil(payData);
                        cell = new PdfPCell(new Phrase(String.format("%.2f",payData)+ "  ", fontContent));
                        cell.setUseVariableBorders(true);
                        cell.setBorderWidthLeft(0);
                        cell.setBorderWidthTop(0);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                        cell.setPaddingBottom(4);
                        table2.addCell(cell);

                    } else {
                        cell = new PdfPCell(new Phrase(" "));
                        cell.setBorderWidthBottom(0);
                        cell.setBorderWidthTop(0);
                        cell.setColspan(3);
                        table2.addCell(cell);
                    }
                    if (j < bene.size()) {

                        cell = new PdfPCell(new Phrase(" " + bene.get(j).getKey() + " ", fontContent));
                        cell.setUseVariableBorders(true);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                        cell.setBorderWidthTop(0);
                        cell.setBorderWidthLeft(0);
                        cell.setBorderWidthRight(0);
                        cell.setPaddingBottom(4);
                        table2.addCell(cell);
                        cell = new PdfPCell(new Phrase(bene.get(j).getValue() + "  ", fontContent));
                        cell.setUseVariableBorders(true);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setPaddingBottom(4);
                        cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                        cell.setBorderWidthTop(0);
                        cell.setBorderWidthRight(0);
                        table2.addCell(cell);

                    } else {
                        cell = new PdfPCell(new Phrase(" "));
                        cell.setBorderWidthBottom(0);
                        cell.setBorderWidthTop(0);
                        cell.setBorderWidthRight(0);
                        cell.setBorderWidthLeft(0);
                        cell.setColspan(2);
                        table2.addCell(cell);
                    }


                    if (j < ded.size()) {
                        cell = new PdfPCell(new Phrase(" " + ded.get(j).getKey() + " ", fontContent));
                        cell.setUseVariableBorders(true);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                        cell.setPaddingBottom(4);
                        cell.setBorderWidthTop(0);
                        table2.addCell(cell);
                        cell = new PdfPCell(new Phrase(ded.get(j).getValue() + " ", fontContent));
                        cell.setUseVariableBorders(true);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        cell.setPaddingBottom(4);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorderWidthLeft(0);
                        cell.setBorderWidthTop(0);
                        cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                        table2.addCell(cell);

                    } else {
                        cell = new PdfPCell(new Phrase(" "));
                        cell.setBorderWidthBottom(0);
                        cell.setBorderWidthTop(0);
                        cell.setColspan(2);
                        table2.addCell(cell);
                    }
                }

                for (int x = 0; x < 14 - dataRow; x++) {
                    cell = new PdfPCell(new Phrase(" "));
                    cell.setBorderWidthBottom(0);
                    cell.setBorderWidthTop(0);
                    cell.setColspan(3);
                    table2.addCell(cell);

                    cell = new PdfPCell(new Phrase(" "));
                    cell.setBorderWidthBottom(0);
                    cell.setBorderWidthLeft(0);
                    cell.setBorderWidthTop(0);
                    cell.setBorderWidthRight(0);
                    cell.setColspan(2);
                    table2.addCell(cell);

                    cell = new PdfPCell(new Phrase(" "));
                    cell.setBorderWidthBottom(0);
                    cell.setBorderWidthTop(0);
                    cell.setColspan(2);
                    table2.addCell(cell);
                }


                cell = new PdfPCell(new Phrase("Total", fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setPadding(4);

                table2.addCell(cell);

                Double totalFixedAmt = payroll_details.getBasicpay() + payroll_details.getHra() + payroll_details.getConveyance() + payroll_details.getMedical() + payroll_details.getOthers() + payroll_details.getFuelAllowance() + payroll_details.getIncentive();
                cell = new PdfPCell(new Phrase(String.format("%.2f",totalFixedAmt), fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPadding(4);
                cell.setPaddingBottom(5);
                table2.addCell(cell);

               // totalEarning = Math.ceil(totalEarning);
                cell = new PdfPCell(new Phrase(String.format("%.2f",totalEarning), fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPadding(4);
                cell.setPaddingBottom(5);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase("Total Addl. Benefits ", fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setPadding(4);
                table2.addCell(cell);

                Double totalBenefits = 0.0;
                Double tempBenefits = 0.0;
                for (int i = 0; i < bene.size(); i++) {

                    tempBenefits = Double.parseDouble(bene.get(i).getValue());
                    totalBenefits = totalBenefits + tempBenefits;
                //    totalBenefits = Math.ceil(totalBenefits);

                }

                cell = new PdfPCell(new Phrase(String.format("%.2f",totalBenefits), fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPadding(4);
                cell.setPaddingBottom(5);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase("Total Deductions", fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setPadding(4);

                Double totalDeductions = 0.0;
                Double tempDed = 0.0;
                for (int i = 0; i < ded.size(); i++) {

                    tempDed = Double.parseDouble(ded.get(i).getValue());
                    totalDeductions = totalDeductions + tempDed;
                  //  totalDeductions = Math.ceil(totalDeductions);

                }
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(String.format("%.2f",totalDeductions), fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPadding(4);
                table2.addCell(cell);


                document.add(table2);

                PdfPTable table3 = new PdfPTable(2);
                table3.setWidths(new int[]{5, 1});
                table3.setWidthPercentage(100);
                table3.setSpacingBefore(15);
                cell = new PdfPCell(new Phrase("CTC Per Month (Total Earnings + Total Additional Benefits) ", fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setPadding(4);
                table3.addCell(cell);

                Double totalCTC = 0.0;
                totalCTC = totalEarning + totalBenefits;
                totalCTC = Math.ceil(totalCTC);
                cell = new PdfPCell(new Phrase(String.format("%.2f",totalCTC), fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPadding(4);
                table3.addCell(cell);
                cell = new PdfPCell(new Phrase("Fixed Gross Salary ", fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setPadding(4);

                table3.addCell(cell);
                cell = new PdfPCell(new Phrase(String.format("%.2f",Math.ceil(totalFixedAmt)), fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPadding(4);
                table3.addCell(cell);

                cell = new PdfPCell(new Phrase("Net Salary ", fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setPadding(4);
                table3.addCell(cell);

                Double totalNetSalary = 0.0;
                totalNetSalary = totalEarning - totalDeductions;
               // totalNetSalary = Math.ceil(totalNetSalary);
                cell = new PdfPCell(new Phrase(String.format("%.2f",Math.ceil(totalNetSalary)), fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPadding(4);
                table3.addCell(cell);

                document.add(table3);


                document.add(Chunk.NEWLINE);
                document.add(new Phrase(" Note : Festival Bonus and Service Rewards are paid annually\n\n", new Font(Font.FontFamily.TIMES_ROMAN, 8, 0)));
                document.add(Chunk.NEWLINE);
                Paragraph preface = new Paragraph("***** This is Computer generated statement and hence do not require a Signature *****",
                        new Font(Font.FontFamily.TIMES_ROMAN, 8, 0));
                preface.setAlignment(Element.ALIGN_CENTER);

                document.add(preface);
                document.close();
                file.close();
                // Inserting Data to Payroll Archives

                MongoClient mongo = new MongoClient("localhost", 27017);
                DB db = mongo.getDB("treffer");
                DBCollection collection = db.getCollection("Payslip_Archives");

                ///Delete All documents before running example again
                //WriteResult result = collection.remove(new BasicDBObject());
                //System.out.println(result.toString());


                BasicDBObject documentValue = new BasicDBObject();
                documentValue.put("empno",employee.getEmpId() );
                documentValue.put("empname", employee.getEmpName());
                documentValue.put("panno", employee.getPanno());

                for(int j=0;j<earning.size();j++)
                {
                    documentValue.put(earning.get(j).getKey(),earning.get(j).getValue());

                }
                for(int j=0;j<bene.size();j++)
                {
                    documentValue.put(bene.get(j).getKey(),bene.get(j).getValue());

                }
                for(int j=0;j<ded.size();j++)
                {
                    documentValue.put(ded.get(j).getKey(),ded.get(j).getValue());

                }

                documentValue.put("CTC",totalCTC );
                documentValue.put("fixedgross", totalFixedAmt);
                documentValue.put("netsalary ", totalNetSalary);

                String[] monthName = { "January", "February", "March", "April", "May", "June", "July",
                        "August", "September", "October", "November", "December" };

                Calendar cal = Calendar.getInstance();
                String month = monthName[cal.get(Calendar.MONTH)];
                documentValue.put("month ", month);


                collection.insert(documentValue);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "hi";
    }


}
