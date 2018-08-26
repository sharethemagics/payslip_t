package com.treffer.payslip.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.treffer.payslip.dao.EmployeeRepository;
import com.treffer.payslip.to.Employee;
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
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/treffer")
public class controller {
    public static BaseFont fontReg;
    public static BaseFont fontBold;
    public static Font fontContent;
    public static Font fontTableHdr;
    public static Font fontTableSideHdr;
    public static BaseColor lightGray;
    public static BaseColor white;
    public static BaseColor red;
    private final AtomicLong counter = new AtomicLong();
    String IMG1 = "src/main/resources/logo.jpg";
    String FONT = "Calibri.ttf";
    @Autowired
    private EmployeeRepository employeeRepository;

    {
        try {
            fontReg = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            fontBold = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            fontContent = new Font(fontReg, 10);
            fontTableHdr = new Font(fontBold, 10, Font.BOLD, BaseColor.WHITE);
            fontTableSideHdr = new Font(fontBold, 9, Font.BOLD, BaseColor.BLACK);
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
        System.out.print("entering");
        List<Employee> employees = this.employeeRepository.findAll();
        Employee test;
        test = this.employeeRepository.findByEmpId(1.0);
        System.out.println(test);
        employees.forEach(employee -> System.out.println(employee.getEmpName()));
        System.out.println("exiting");
        try {

            OutputStream file = new FileOutputStream(new File("out/PDF_Java4s.pdf"));
            Document document = new Document(PageSize.A4);
            System.out.print(document.getPageSize());
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

            cell = new PdfPCell(new Phrase("Payslip (Form-T) for the month of August 2018", fontTableSideHdr));
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
            cell = new PdfPCell(new Phrase("PAN / UAN / ESI Details", fontTableHdr));
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
            cell = new PdfPCell(new Phrase(" TREF / 012 / 2016", fontContent));
            cell.setBackgroundColor(lightGray);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" Bank Name", fontTableSideHdr));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" TamilNad Mercantile Bank", fontContent));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" PAN #", fontTableSideHdr));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" DFCPK6775Q", fontContent));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" Emp. Name", fontTableSideHdr));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setPaddingBottom(5);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" Mr. Karthikeyan B", fontContent));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" Account #", fontTableSideHdr));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" 058100050309613", fontContent));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" UAN #", fontTableSideHdr));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" 101171917898", fontContent));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" Designation", fontTableSideHdr));
            cell.setPaddingBottom(5);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" Team Leader - QC", fontContent));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" Branch", fontTableSideHdr));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" Podanur, Coimbatore", fontContent));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" ESI #", fontTableSideHdr));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" 5607044139", fontContent));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" DOJ", fontTableSideHdr));
            cell.setPaddingBottom(5);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" 01-Oct-2016", fontContent));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" Days Paid", fontTableSideHdr));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(" 30", fontContent));
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("", fontTableSideHdr));
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("", fontContent));
            cell.setBackgroundColor(lightGray);
            table.addCell(cell);
            document.add(table);


            PdfPTable table2 = new PdfPTable(6);
            table2.setWidthPercentage(100);
            table2.setSpacingBefore(15);
            Paragraph p;
            cell = new PdfPCell(new Phrase("Earnings", fontTableHdr));
            cell.setBackgroundColor(red);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPaddingBottom(5);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("Amount (INR)", fontTableHdr));
            cell.setBackgroundColor(red);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPaddingBottom(5);

            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("Additional Benefits", fontTableHdr));
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

            cell = new PdfPCell(new Phrase(" Basic Pay", fontContent));
            cell.setUseVariableBorders(true);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setPaddingBottom(5);
            cell.setBorderWidthTop(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("11760.00 ", fontContent));
            cell.setUseVariableBorders(true);
            cell.setPaddingRight(3);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthLeft(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthRight(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase(" PF (Employer)", fontContent));
            cell.setUseVariableBorders(true);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthTop(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("1411.00 ", fontContent));
            cell.setPaddingRight(3);
            cell.setUseVariableBorders(true);
            cell.setBorderWidthTop(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase(" PF (Employee)", fontContent));
            cell.setUseVariableBorders(true);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("1411.00 ", fontContent));
            cell.setPaddingRight(3);
            cell.setUseVariableBorders(true);
            cell.setBorderWidthTop(0);
            //cell.setBorderWidthLeft(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase(" HRA", fontContent));
            cell.setUseVariableBorders(true);
            cell.setPaddingBottom(5);
            cell.setBorderWidthTop(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("5880.00 ", fontContent));
            cell.setPaddingRight(3);
            cell.setUseVariableBorders(true);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthLeft(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthRight(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase(" ESI (Employer)", fontContent));
            cell.setUseVariableBorders(true);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthTop(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("931.00 ", fontContent));
            cell.setPaddingRight(3);
            cell.setUseVariableBorders(true);
            cell.setBorderWidthTop(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase(" ESI (Employee)", fontContent));
            cell.setUseVariableBorders(true);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("343.00 ", fontContent));
            cell.setPaddingRight(3);
            cell.setUseVariableBorders(true);
            cell.setBorderWidthTop(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);


            cell = new PdfPCell(new Phrase(" Conveyance", fontContent));
            cell.setUseVariableBorders(true);
            cell.setBorderWidthTop(0);
            cell.setPaddingBottom(5);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("500.00 ", fontContent));
            cell.setPaddingRight(3);
            cell.setUseVariableBorders(true);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthLeft(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthRight(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase(" Food Allowance", fontContent));
            cell.setUseVariableBorders(true);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthTop(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("2000.00 ", fontContent));
            cell.setPaddingRight(3);
            cell.setUseVariableBorders(true);
            cell.setBorderWidthTop(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase(" Loss of Pay", fontContent));
            cell.setUseVariableBorders(true);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("0.00 ", fontContent));
            cell.setPaddingRight(3);
            cell.setUseVariableBorders(true);
            cell.setBorderWidthTop(0);

            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase(" Medical ", fontContent));
            cell.setUseVariableBorders(true);
            cell.setPaddingBottom(5);
            cell.setBorderWidthTop(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("1000.00 ", fontContent));
            cell.setPaddingRight(3);
            cell.setUseVariableBorders(true);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthLeft(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthRight(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase(" Tax on employment", fontContent));
            cell.setUseVariableBorders(true);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthTop(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("208.00 ", fontContent));
            cell.setPaddingRight(3);
            cell.setUseVariableBorders(true);
            cell.setBorderWidthTop(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase(" Salary Advance", fontContent));
            cell.setUseVariableBorders(true);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("0.00 ", fontContent));
            cell.setPaddingRight(3);
            cell.setUseVariableBorders(true);
            cell.setBorderWidthTop(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);


            cell = new PdfPCell(new Phrase(" Fuel Allowance", fontContent));
            cell.setUseVariableBorders(true);
            cell.setBorderWidthTop(0);
            cell.setPaddingBottom(5);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("1000.00 ", fontContent));
            cell.setPaddingRight(3);
            cell.setUseVariableBorders(true);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthLeft(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthRight(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase(" Festival Bonus", fontContent));
            cell.setUseVariableBorders(true);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthTop(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("833.00 ", fontContent));
            cell.setPaddingRight(3);
            cell.setUseVariableBorders(true);
            cell.setBorderWidthTop(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("", fontContent));
            cell.setUseVariableBorders(true);
            cell.setBorderWidth(0);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("", fontContent));
            cell.setUseVariableBorders(true);
            cell.setBorderWidthLeft(0);

            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase(" Others ", fontContent));
            cell.setUseVariableBorders(true);
            cell.setPaddingBottom(5);
            cell.setBorderWidthTop(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("1000.00 ", fontContent));
            cell.setPaddingRight(3);
            cell.setUseVariableBorders(true);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthLeft(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthRight(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase(" Service Rewards", fontContent));
            cell.setUseVariableBorders(true);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthTop(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("1250.00 ", fontContent));
            cell.setPaddingRight(3);
            cell.setUseVariableBorders(true);
            cell.setBorderWidthTop(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase(" ", fontContent));
            cell.setBorderWidth(0);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase(" ", fontContent));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table2.addCell(cell);


            cell = new PdfPCell(new Phrase(" Incentives ", fontContent));
            cell.setUseVariableBorders(true);
            cell.setPaddingBottom(5);
            cell.setBorderWidthTop(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("0.00 ", fontContent));
            cell.setPaddingRight(3);
            cell.setUseVariableBorders(true);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthLeft(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthRight(0);
            cell.setBorderColorBottom(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            cell.setBorderWidthRight(0);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            cell.setBorderWidthRight(0);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            cell.setBorderWidthRight(0);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            cell.setBorderWidthRight(0);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            cell.setBorderWidthRight(0);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            cell.setBorderWidthRight(0);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthBottom(0);
            cell.setColspan(2);
            table2.addCell(cell);

            cell = new PdfPCell(new Phrase(" "));

            cell.setBorderWidthTop(0);
            cell.setColspan(2);
            cell.setBorderWidthBottom(0);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase(" "));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthLeft(0);
            cell.setColspan(2);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("Total", fontTableSideHdr));
            cell.setBackgroundColor(lightGray);
            cell.setPadding(4);

            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("20600.00", fontTableSideHdr));
            cell.setBackgroundColor(lightGray);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setPadding(4);
            cell.setPaddingBottom(5);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("Total", fontTableSideHdr));
            cell.setBackgroundColor(lightGray);
            cell.setPadding(4);

            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("6633.00", fontTableSideHdr));
            cell.setBackgroundColor(lightGray);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setPadding(4);
            cell.setPaddingBottom(5);
            table2.addCell(cell);
            cell = new PdfPCell(new Phrase("Total", fontTableSideHdr));
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

            cell = new PdfPCell(new Phrase("Take Home Pay (Total Earnings - Total Deductions) ", fontTableSideHdr));
            cell.setBackgroundColor(lightGray);
            cell.setPadding(4);
            table3.addCell(cell);
            cell = new PdfPCell(new Phrase("18846.00 ", fontTableSideHdr));
            cell.setBackgroundColor(lightGray);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setPadding(4);
            table3.addCell(cell);

            document.add(table3);

            PdfPTable table4 = new PdfPTable(2);
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


            document.add(table4);
            Paragraph preface = new Paragraph("***** This is Computer generated statement and hence do not require a Signature *****",
                    new Font(Font.FontFamily.TIMES_ROMAN, 8, 0));
            preface.setAlignment(Element.ALIGN_CENTER);

            document.add(preface);
            document.close();
            file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "hi";
    }

}
