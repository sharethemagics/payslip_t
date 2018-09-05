package com.treffer.payslip;
import com.itextpdf.text.*;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;



public class payslipGen{
        public static BaseFont fontReg;
        public static BaseFont fontBold;
        public static Font fontContent;
        public static Font fontTableHdr;
        public static Font fontTableSideHdr;
        public static Font fontTableHeading;
        public static BaseColor lightGray;
        public static BaseColor white;
        public static BaseColor red;
         public static void main(String[] args) {
            Document doc = new Document();
             try {
                 fontContent = new Font(fontReg, 10);
                 fontTableHdr = new Font(fontBold, 9, Font.BOLD, BaseColor.WHITE);
                 fontTableSideHdr = new Font(fontBold, 9, Font.BOLD, BaseColor.BLACK);
                 fontTableHeading =  new Font(fontBold, 12, Font.BOLD, BaseColor.BLACK);
                 lightGray = WebColors.getRGBColor("#F5F5F5");
                 white = WebColors.getRGBColor("#ffffff");
                 red = WebColors.getRGBColor("#EF5350");
             } catch (Exception e) {
                 e.printStackTrace();
             }
            try {
                PdfWriter.getInstance(doc, new FileOutputStream("out/TableNestedDemo.pdf"));
                doc.open();

                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100);
                table.setWidths(new int[]{2,1,1});
                PdfPCell cell1 = new PdfPCell(new Phrase(""));
                PdfPCell cell2 = new PdfPCell(new Phrase(""));
                PdfPCell cell3 = new PdfPCell(new Phrase(""));

                PdfPTable nestedTable = new PdfPTable(3);
                nestedTable.setWidthPercentage(100);
                nestedTable.setSplitRows(true);

                nestedTable.addCell(new PdfPCell(new Phrase("Salary Details",fontTableHdr)));
                nestedTable.addCell(new PdfPCell(new Phrase("Nested 2",fontTableHdr)));
                nestedTable.addCell(new PdfPCell(new Phrase("Nested 3",fontTableHdr)));

                cell1.setBackgroundColor(red);
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell1.setPaddingBottom(5);
                cell1.addElement(nestedTable);

                PdfPTable nestedTable1 = new PdfPTable(2);
                nestedTable1.setWidthPercentage(100);
                nestedTable1.setWidths(new int[]{1,1});
                nestedTable1.addCell(new PdfPCell(new Phrase("Nested 1",fontTableHdr)));
                nestedTable1.addCell(new PdfPCell(new Phrase("Nested 2",fontTableHdr)));
                cell2.setBackgroundColor(red);
                cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell2.setPaddingBottom(5);
                cell2.addElement(nestedTable1);

                PdfPTable nestedTable2 = new PdfPTable(2);
                nestedTable2.setWidthPercentage(100);
                nestedTable2.setWidths(new int[]{1,1});
                nestedTable2.addCell(new PdfPCell(new Phrase("Nested 1",fontTableHdr)));
                nestedTable2.addCell(new PdfPCell(new Phrase("Nested 2",fontTableHdr)));
                cell3.setBackgroundColor(red);
                cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell3.setPaddingBottom(5);
                cell3.addElement(nestedTable2);



                table.addCell(cell1);
                table.addCell(cell2);
                table.addCell(cell3);
                doc.add(table);
            } catch (DocumentException | FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                doc.close();
            }
        }
    }
