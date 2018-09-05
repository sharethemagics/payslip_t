package com.treffer.payslip;

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

import com.itextpdf.text.*;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class sampleJava {

    public PdfPCell createCell(String content, float borderWidth, int colspan, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(content));
        cell.setBorderWidth(borderWidth);
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    public static void main(String args[]) {

        sampleJava s= new sampleJava();
        try {

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream("out/sample.pdf"));
                document.open();
                PdfPTable table = new PdfPTable(5);
                table.setWidths(new int[]{1, 2, 1, 1, 1});
                table.addCell(s.createCell("SKU", 2, 1, Element.ALIGN_LEFT));
                table.addCell(s.createCell("Description", 2, 1, Element.ALIGN_LEFT));
                table.addCell(s.createCell("Unit Price", 2, 1, Element.ALIGN_LEFT));
                table.addCell(s.createCell("Quantity", 2, 1, Element.ALIGN_LEFT));
                table.addCell(s.createCell("Extension", 2, 1, Element.ALIGN_LEFT));
                String[][] data = {
                        {"ABC123", "The descriptive text may be more than one line and the text should wrap automatically", "$5.00", "10", "$50.00"},
                        {"QRS557", "Another description", "$100.00", "15", "$1,500.00"},
                        {"XYZ999", "Some stuff", "$1.00", "2", "$2.00"}
                };
                for (String[] row : data) {
                    table.addCell(s.createCell(row[0], 1, 1, Element.ALIGN_LEFT));
                    table.addCell(s.createCell(row[1], 1, 1, Element.ALIGN_LEFT));
                    table.addCell(s.createCell(row[2], 1, 1, Element.ALIGN_RIGHT));
                    table.addCell(s.createCell(row[3], 1, 1, Element.ALIGN_RIGHT));
                    table.addCell(s.createCell(row[4], 1, 1, Element.ALIGN_RIGHT));
                }
                table.addCell(s.createCell("Totals", 2, 4, Element.ALIGN_LEFT));
                table.addCell(s.createCell("$1,552.00", 2, 1, Element.ALIGN_RIGHT));
                document.add(table);
                document.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
