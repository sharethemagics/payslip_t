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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

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
            fontTableHeading =  new Font(fontBold, 12, Font.BOLD, BaseColor.BLACK);
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
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {


        List<Employee> emp = this.employeeRepository.findAll();
        /*emp.forEach(ee-> {
            System.out.print(ee.getEmpName());
        });
*/
        String pdfName;



        Payroll_details payroll_details = new Payroll_details();
        payroll_details = this.payrollRepository.findByEmpId("TREF / 001 / 2016");
        System.out.println(payroll_details.getEmpId() + " test ");

       ArrayList<tempTO> earning;
       ArrayList<tempTO> ded ;
       ArrayList<tempTO> bene ;

          /*  Employee test;
            test = this.employeeRepository.findByEmpId("TREF / 001 / 2016");
            System.out.println(test);*/

        //employees.forEach(employee -> System.out.println(employee.getEmpName()));

        try {
            for (Employee employee : this.employeeRepository.findAll()) {
              /*  earning = new ArrayList<>();
                ded = new ArrayList<>();
                bene = new ArrayList<>();
                tempTO temp = new tempTO();*/
                pdfName = employee.getEmpName();
                payroll_details = this.payrollRepository.findByEmpId(employee.getEmpId());

                // New code added

                LinkedHashMap <String,Double> fixedPayDtls= new LinkedHashMap <String,Double>();
                HashMap<String, Double> earningsmap = new HashMap<String, Double>();

                if(payroll_details.getBasicpay() !=null && payroll_details.getBasicpay() >0.0)
                {
                    fixedPayDtls.put("Basic Pay",payroll_details.getBasicpay());
                    fixedPayDtls.put("basicpay_e", (payroll_details.getBasicpay() - ((payroll_details.getPaiddays() * payroll_details.getBasicpay()) / 100)));
                }


                if(payroll_details.getHra() !=null && payroll_details.getHra() >0.0)
                {
                    fixedPayDtls.put("HRA",payroll_details.getHra());
                    fixedPayDtls.put("hra_e", (payroll_details.getHra() - ((payroll_details.getPaiddays() * payroll_details.getHra()) / 100)));
                }

                if(payroll_details.getConveyance() !=null && payroll_details.getConveyance() >0.0)
                {
                    fixedPayDtls.put("Conveyance",payroll_details.getConveyance());
                    fixedPayDtls.put("conveyance_e", (payroll_details.getConveyance() - ((payroll_details.getPaiddays() * payroll_details.getConveyance()) / 100)));
                }

                if(payroll_details.getMedical() !=null && payroll_details.getMedical() >0.0)
                {
                    fixedPayDtls.put("Medical",payroll_details.getMedical());
                    fixedPayDtls.put("medical_e", (payroll_details.getMedical() - ((payroll_details.getPaiddays() * payroll_details.getMedical()) / 100)));
                }

                HashMap<String,Double> additionalBenefits= new HashMap<String,Double>();
                HashMap<String, Double> deductions = new HashMap<String, Double>();

                if(payroll_details.getPfemployer() != null && payroll_details.getPfemployer() !=0.0)
                {
                    fixedPayDtls.put("pf_employer",payroll_details.getPfemployer());
                }
                if(payroll_details.getEsiemployer() != null && payroll_details.getEsiemployer() !=0.0)
                {
                    fixedPayDtls.put("ESI_employer",payroll_details.getEsiemployer());
                }
                if(payroll_details.getFoodallowance() != null && payroll_details.getFoodallowance() !=0.0)
                {
                    fixedPayDtls.put("food_Allow",payroll_details.getFoodallowance());
                }
                if(payroll_details.getAccomodation() != null && payroll_details.getAccomodation() !=0.0)
                {
                    fixedPayDtls.put("Accomodation",payroll_details.getAccomodation());
                }


                if(payroll_details.getPfemployee() != null && payroll_details.getPfemployee() !=0.0)
                {
                    fixedPayDtls.put("pf_employee",payroll_details.getPfemployee());
                }
                if(payroll_details.getEsiemployee() != null && payroll_details.getEsiemployee() !=0.0)
                {
                    fixedPayDtls.put("ESI_employee",payroll_details.getEsiemployee());
                }
                if(payroll_details.getTaxonemplmnt() != null && payroll_details.getTaxonemplmnt() !=0.0)
                {
                    fixedPayDtls.put("Tax on Employment",payroll_details.getTaxonemplmnt());
                }
                if(payroll_details.getTds() != null && payroll_details.getTds() !=0.0)
                {
                    fixedPayDtls.put("TDS",payroll_details.getTds());
                }

                int maxRows=0;
                if(fixedPayDtls.size() > additionalBenefits.size())
                {
                    if(fixedPayDtls.size() > deductions.size())
                    {
                        maxRows = fixedPayDtls.size();
                    }
                    else
                    {
                        maxRows = deductions.size();
                    }
                }
                else if (additionalBenefits.size() >=deductions.size())
                {
                    maxRows = additionalBenefits.size();
                }

                System.out.println("maximum fixed " +fixedPayDtls.size());
                System.out.println("maximum add " +additionalBenefits.size());
                System.out.println("maximum ded " +deductions.size());
                System.out.println("maximum rows " +maxRows);

                OutputStream file = new FileOutputStream(new File("out/"+ pdfName+".pdf"));
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
                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getPaiddays()), fontContent));
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
                cell = new PdfPCell(new Phrase(employee.getPaidate(), fontContent));
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


                // Generating a Set of entries
                Set set = fixedPayDtls.entrySet();

                // Displaying elements of LinkedHashMap
                Iterator iterator = set.iterator();
                while(iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry)iterator.next();
                    System.out.print("Key is: "+ entry.getKey() +
                            "& Value is: "+entry.getValue()+"\n");


                        System.out.println("fixed key vale" + entry.getKey());
                        System.out.println("fixed value" + entry.getValue());
                        cell = new PdfPCell((new Phrase(String.valueOf(entry.getKey()), fontContent)));

                        cell.setUseVariableBorders(true);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        cell.setPaddingBottom(5);
                        cell.setBorderWidthTop(0);
                        cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                        table2.addCell(cell);


                        cell = new PdfPCell((new Phrase(String.valueOf(entry.getValue()), fontContent)));
                        cell.setUseVariableBorders(true);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                        cell.setPaddingBottom(5);
                        cell.setBorderWidthTop(0);
                        cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                        table2.addCell(cell);

                    }
                                  /*  System.out.println("in 540");
                                    System.out.println("earnings key vale" + entry.getKey());
                                    System.out.println("earnings value" + entry.getValue());
                                    cell = new PdfPCell((new Phrase(String.valueOf(entry.getValue()), fontContent)));
                                    cell.setUseVariableBorders(true);
                                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    cell.setPaddingBottom(5);
                                    cell.setBorderWidthTop(0);
                                    cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                                    table2.addCell(cell);


                                    System.out.println("in 554");
                                    System.out.println("additional key vale" + entry.getKey());
                                    System.out.println("additional value" + entry.getValue());
                                    cell = new PdfPCell((new Phrase(entry.getKey(), fontContent)));
                                    cell.setUseVariableBorders(true);
                                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    cell.setPaddingBottom(5);
                                    cell.setBorderWidthTop(0);
                                    cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                                    table2.addCell(cell);

                                    cell = new PdfPCell((new Phrase(String.valueOf(entry.getValue()), fontContent)));
                                    cell.setUseVariableBorders(true);
                                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    cell.setPaddingBottom(5);
                                    cell.setBorderWidthTop(0);
                                    cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                                    table2.addCell(cell);


                                    System.out.println("in 575");
                                    System.out.println("ded key vale" + entry.getKey());
                                    System.out.println("ded value" + entry.getValue());
                                    cell = new PdfPCell((new Phrase(entry.getKey(), fontContent)));
                                    cell.setUseVariableBorders(true);
                                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    cell.setPaddingBottom(5);
                                    cell.setBorderWidthTop(0);
                                    cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                                    table2.addCell(cell);

                                    cell = new PdfPCell((new Phrase(String.valueOf(entry.getValue()), fontContent)));
                                    cell.setUseVariableBorders(true);
                                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                                    cell.setPaddingBottom(5);
                                    cell.setBorderWidthTop(0);
                                    cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                                    table2.addCell(cell);

                                }*/




                /*cell = new PdfPCell(new Phrase(" Basic Pay", fontContent));
                cell.setUseVariableBorders(true);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(5);
                cell.setBorderWidthTop(0);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                System.out.println("before assigning value in pdf");
                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getBasicpay()), fontContent));
                cell.setUseVariableBorders(true);
                cell.setPaddingRight(3);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                System.out.println(((26 * payroll_details.getBasicpay()) / 100));

                HashMap<String, Double> earningsmap = new HashMap<String, Double>();


                earningsmap.put("basicpay_e", (payroll_details.getBasicpay() - ((payroll_details.getPaiddays() * payroll_details.getBasicpay()) / 100)));
                System.out.println("calculated basic pay earnings " + (payroll_details.getBasicpay() - ((26 * payroll_details.getBasicpay()) / 100)));
                cell = new PdfPCell(new Phrase(String.valueOf((payroll_details.getBasicpay() - ((payroll_details.getPaiddays() * payroll_details.getBasicpay()) / 100))), fontContent));
                cell.setUseVariableBorders(true);
                cell.setPaddingRight(3);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(" PF (Employer)", fontContent));
                cell.setUseVariableBorders(true);
                cell.setBorderWidthRight(0);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getPfemployer()), fontContent));
                cell.setPaddingRight(3);
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthRight(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(" PF (Employee)", fontContent));
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthRight(0);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getPfemployee()), fontContent));
                cell.setPaddingRight(3);
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                //cell.setBorderWidthLeft(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);

                cell = new PdfPCell(new Phrase(" HRA", fontContent));
                cell.setUseVariableBorders(true);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(5);
                cell.setBorderWidthTop(0);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getHra()), fontContent));
                cell.setUseVariableBorders(true);
                cell.setPaddingRight(3);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);


                earningsmap.put("hra_e", (payroll_details.getHra() - ((payroll_details.getPaiddays() * payroll_details.getHra()) / 100)));
                System.out.println("calculated hra earnings " + (payroll_details.getHra() - ((payroll_details.getPaiddays() * payroll_details.getHra()) / 100)));
                cell = new PdfPCell(new Phrase(String.valueOf((payroll_details.getHra() - ((payroll_details.getPaiddays() * payroll_details.getHra()) / 100))), fontContent));

                cell.setUseVariableBorders(true);
                cell.setPaddingRight(3);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(" ESI (Employer)", fontContent));
                cell.setUseVariableBorders(true);
                cell.setBorderWidthRight(0);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getEsiemployer()), fontContent));
                cell.setPaddingRight(3);
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthRight(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(" ESI (Employee)", fontContent));
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthRight(0);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getEsiemployee()), fontContent));
                cell.setPaddingRight(3);
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                //cell.setBorderWidthLeft(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);

                cell = new PdfPCell(new Phrase(" Conveyance", fontContent));
                cell.setUseVariableBorders(true);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(5);
                cell.setBorderWidthTop(0);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getConveyance()), fontContent));
                cell.setUseVariableBorders(true);
                cell.setPaddingRight(3);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);

                earningsmap.put("conveyance_e", (payroll_details.getConveyance() - ((payroll_details.getPaiddays() * payroll_details.getConveyance()) / 100)));
                System.out.println("calculated conveyance earnings " + (payroll_details.getConveyance() - ((payroll_details.getPaiddays() * payroll_details.getConveyance()) / 100)));
                cell = new PdfPCell(new Phrase(String.valueOf((payroll_details.getConveyance() - ((payroll_details.getPaiddays() * payroll_details.getConveyance()) / 100))), fontContent));

                cell.setUseVariableBorders(true);
                cell.setPaddingRight(3);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(" Food Allowance", fontContent));
                cell.setUseVariableBorders(true);
                cell.setBorderWidthRight(0);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getFoodallowance()), fontContent));
                cell.setPaddingRight(3);
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthRight(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(" Tax on Employment", fontContent));
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthRight(0);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getTaxonemplmnt()), fontContent));
                cell.setPaddingRight(3);
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                //cell.setBorderWidthLeft(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);


                   cell = new PdfPCell(new Phrase(" Medical", fontContent));
                    cell.setUseVariableBorders(true);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setPaddingBottom(5);
                    cell.setBorderWidthTop(0);
                    cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                    table2.addCell(cell);
                    cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getMedical()), fontContent));
                    cell.setUseVariableBorders(true);
                    cell.setPaddingRight(3);
                    cell.setBorderWidthTop(0);
                    cell.setBorderWidthLeft(0);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                    table2.addCell(cell);
                    earningsmap.put("medical_e", (payroll_details.getMedical() - ((payroll_details.getPaiddays() * payroll_details.getMedical()) / 100)));
                    System.out.println("calculated getMedical earnings " + (payroll_details.getMedical() - ((payroll_details.getPaiddays() * payroll_details.getMedical()) / 100)));
                    cell = new PdfPCell(new Phrase(String.valueOf((payroll_details.getMedical() - ((payroll_details.getPaiddays() * payroll_details.getMedical()) / 100))), fontContent));

                    cell.setUseVariableBorders(true);
                    cell.setPaddingRight(3);
                    cell.setBorderWidthTop(0);
                    cell.setBorderWidthLeft(0);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                    table2.addCell(cell);


                cell = new PdfPCell(new Phrase(" Accommodation", fontContent));
                cell.setUseVariableBorders(true);
                cell.setBorderWidthRight(0);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getAccomodation()), fontContent));
                cell.setPaddingRight(3);
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthRight(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(" Labour Welfare Fund", fontContent));
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthRight(0);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getLwf()), fontContent));
                cell.setPaddingRight(3);
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                //cell.setBorderWidthLeft(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);

                cell = new PdfPCell(new Phrase(" Others", fontContent));
                cell.setUseVariableBorders(true);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(5);
                cell.setBorderWidthTop(0);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getOthers()), fontContent));
                cell.setUseVariableBorders(true);
                cell.setPaddingRight(3);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                earningsmap.put("others_e", (payroll_details.getOthers() - ((payroll_details.getPaiddays() * payroll_details.getOthers()) / 100)));
                System.out.println("calculated getOthers earnings " + (payroll_details.getOthers() - ((payroll_details.getPaiddays() * payroll_details.getOthers()) / 100)));
                cell = new PdfPCell(new Phrase(String.valueOf((payroll_details.getOthers() - ((payroll_details.getPaiddays() * payroll_details.getOthers()) / 100))), fontContent));

                cell.setUseVariableBorders(true);
                cell.setPaddingRight(3);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(" Festival Bonus", fontContent));
                cell.setUseVariableBorders(true);
                cell.setBorderWidthRight(0);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getFestivalbonus()), fontContent));
                cell.setPaddingRight(3);
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthRight(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(" TDS ", fontContent));
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthRight(0);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getTds()), fontContent));
                cell.setPaddingRight(3);
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                //cell.setBorderWidthLeft(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);

                cell = new PdfPCell(new Phrase(" Fuel Allowance", fontContent));
                cell.setUseVariableBorders(true);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(5);
                cell.setBorderWidthTop(0);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getFuelAllowance()), fontContent));
                cell.setUseVariableBorders(true);
                cell.setPaddingRight(3);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);

                earningsmap.put("fuel_e", (payroll_details.getFuelAllowance() - ((payroll_details.getPaiddays() * payroll_details.getFuelAllowance()) / 100)));
                System.out.println("calculated getFuelAllowance earnings " + (payroll_details.getFuelAllowance() - ((payroll_details.getPaiddays() * payroll_details.getFuelAllowance()) / 100)));
                cell = new PdfPCell(new Phrase(String.valueOf((payroll_details.getFuelAllowance() - ((payroll_details.getPaiddays() * payroll_details.getFuelAllowance()) / 100))), fontContent));

                cell.setUseVariableBorders(true);
                cell.setPaddingRight(3);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(" Service Reward", fontContent));
                cell.setUseVariableBorders(true);
                cell.setBorderWidthRight(0);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getServicereward()), fontContent));
                cell.setPaddingRight(3);
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthRight(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(" Salary Advance ", fontContent));
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthRight(0);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getSalaryadvance()), fontContent));
                cell.setPaddingRight(3);
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                //cell.setBorderWidthLeft(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                if (payroll_details.getIncentive() != null && payroll_details.getIncentive() != 0.0) {
                    cell = new PdfPCell(new Phrase(" Incentive", fontContent));
                    cell.setUseVariableBorders(true);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setPaddingBottom(5);
                    cell.setBorderWidthTop(0);
                    cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                    table2.addCell(cell);
                    cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getIncentive()), fontContent));
                    cell.setUseVariableBorders(true);
                    cell.setPaddingRight(3);
                    cell.setBorderWidthTop(0);
                    cell.setBorderWidthLeft(0);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                    table2.addCell(cell);
                    earningsmap.put("incentive_e", (payroll_details.getIncentive() - ((payroll_details.getPaiddays() * payroll_details.getIncentive()) / 100)));
                    System.out.println("calculated getIncentive earnings " + (payroll_details.getIncentive() - ((payroll_details.getPaiddays() * payroll_details.getIncentive()) / 100)));
                    cell = new PdfPCell(new Phrase(String.valueOf((payroll_details.getIncentive() - ((payroll_details.getPaiddays() * payroll_details.getIncentive()) / 100))), fontContent));

                    cell.setUseVariableBorders(true);
                    cell.setPaddingRight(3);
                    cell.setBorderWidthTop(0);
                    cell.setBorderWidthLeft(0);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                    table2.addCell(cell);
                } else {
                    cell = new PdfPCell(new Phrase(" "));
                    cell.setBorderWidthBottom(0);
                    cell.setBorderWidthTop(0);
                    cell.setColspan(3);
                    table2.addCell(cell);
                }
                cell = new PdfPCell(new Phrase(" Labour Welfare Fund", fontContent));
                cell.setUseVariableBorders(true);
                cell.setBorderWidthRight(0);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase(String.valueOf(payroll_details.getLwf()), fontContent));
                cell.setPaddingRight(3);
                cell.setUseVariableBorders(true);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthRight(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
                table2.addCell(cell);*/


                cell = new PdfPCell(new Phrase(" "));
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthTop(0);
                cell.setColspan(2);
                table2.addCell(cell);



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

                cell = new PdfPCell(new Phrase("Total", fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setPadding(4);

                table2.addCell(cell);

                Double totalFixedAmt = payroll_details.getBasicpay() + payroll_details.getHra() + payroll_details.getConveyance() + payroll_details.getMedical() + payroll_details.getOthers() + payroll_details.getFuelAllowance() + payroll_details.getIncentive();
                cell = new PdfPCell(new Phrase(String.valueOf(totalFixedAmt), fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPadding(4);
                cell.setPaddingBottom(5);
                table2.addCell(cell);

                Double totalearnings = 0.0;
                for (HashMap.Entry<String, Double> entry : earningsmap.entrySet()) {

                    String key = entry.getKey();
                    System.out.println("key value in map" + key);
                    System.out.println("result value before" + entry.getValue());
                    totalearnings += entry.getValue();
                    System.out.println("value" + entry.getValue());
                    System.out.println("result value after addition" + totalearnings);

                }

                //System.out.println("Final result value : " + totalearnings);

                cell = new PdfPCell(new Phrase(String.valueOf(totalearnings), fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPadding(4);
                cell.setPaddingBottom(5);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase("Total Addl. Benefits ", fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setPadding(4);

                table2.addCell(cell);
                cell = new PdfPCell(new Phrase("6633.00", fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPadding(4);
                cell.setPaddingBottom(5);
                table2.addCell(cell);
                cell = new PdfPCell(new Phrase("Total Deductions", fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setPadding(4);

                table2.addCell(cell);
                cell = new PdfPCell(new Phrase("1754.00", fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPadding(4);
                table2.addCell(cell);


                document.add(table2);

                // PdfPTable table4 = new PdfPTable(6);
                //table4.setWidthPercentage(100);

                PdfPTable table3 = new PdfPTable(2);
                table3.setWidths(new int[]{5, 1});
                table3.setWidthPercentage(100);
                table3.setSpacingBefore(15);
                cell = new PdfPCell(new Phrase("CTC Per Month (Total Earnings + Total Additional Benefits) ", fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setPadding(4);

                table3.addCell(cell);
                cell = new PdfPCell(new Phrase("27233.00 ", fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPadding(4);
                table3.addCell(cell);
                cell = new PdfPCell(new Phrase("Fixed Gross Salary ", fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setPadding(4);

                table3.addCell(cell);
                cell = new PdfPCell(new Phrase("20600.00 ", fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPadding(4);
                table3.addCell(cell);

                cell = new PdfPCell(new Phrase("Net Salary ", fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setPadding(4);
                table3.addCell(cell);
                cell = new PdfPCell(new Phrase("18846.00 ", fontTableSideHdr));
                cell.setBackgroundColor(lightGray);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPadding(4);
                table3.addCell(cell);

                document.add(table3);

           /* PdfPTable table4 = new PdfPTable(2);
            table4.setWidths(new int[]{2, 4});
            table4.setWidthPercentage(100);
            table4.setSpacingBefore(15);
            table4.setSpacingAfter(5);
            cell = new PdfPCell(new Phrase("Amount in words ", fontTableSideHdr));
            cell.setBackgroundColor(lightGray);
            cell.setPadding(4);
            cell.setPaddingBottom(5);
            table4.addCell(cell);
            cell = new PdfPCell(new Phrase(convertToIndianCurrency("18846.00"), fontTableSideHdr));
            cell.setBackgroundColor(lightGray);

            cell.setPadding(4);
            cell.setPaddingBottom(5);
            table4.addCell(cell);


            document.add(table4);*/
                document.add(Chunk.NEWLINE);
                document.add(new Phrase(" Note : Festival Bonus and Service Rewards are paid annually\n\n", new Font(Font.FontFamily.TIMES_ROMAN, 8, 0)));
                document.add(Chunk.NEWLINE);
                Paragraph preface = new Paragraph("***** This is Computer generated statement and hence do not require a Signature *****",
                        new Font(Font.FontFamily.TIMES_ROMAN, 8, 0));
                preface.setAlignment(Element.ALIGN_CENTER);

                document.add(preface);
                document.close();
                file.close();


            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "hi";
    }

}
