package com.cass.documents;

import ar.com.fdvs.dj.domain.constants.Border;
import com.cass.data.AttendanceRecordsEntity;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.vaadin.flow.component.grid.Grid;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DocumentGenerator {

    public static InputStream generateAttendancePdf(String className, String programme, Grid<AttendanceRecordsEntity> dataTable) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            String date = LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
            AtomicReference<String> attendanceDate = new AtomicReference<>();
            dataTable.getListDataView().getItems().forEach(item -> {
                String format = item.getAttendanceDate().toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
                attendanceDate.set(format);
            });
            PdfWriter pdfWriter = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);

            Table table = new Table(5);
            table.useAllAvailableWidth();
            table.setAutoLayout();

            Paragraph heading = new Paragraph("ATTENDANCE REPORT");
            heading.setTextAlignment(TextAlignment.CENTER);
            heading.setBold();
            heading.setFontSize(17);
            //CREATE TABLE HEADER.
            table.addHeaderCell(new Cell(0,5).add(heading));
            table.addHeaderCell(new Cell(0, 2).add(new Paragraph("CLASS")).setFontSize(12).setBold());
            table.addHeaderCell(new Cell(0, 3).add(new Paragraph(className)).setFontSize(12).setBold());
            table.addHeaderCell(new Cell(0,1).add(new Paragraph("DATE")));
            table.addHeaderCell(new Cell(0,1).add(new Paragraph(attendanceDate.get())).setBold());
            table.addHeaderCell(new Cell(0,1).add(new Paragraph("PROGRAMME")));
            table.addHeaderCell(new Cell(0,2).add(new Paragraph(programme)).setBold());

            //set table content headers
            table.addHeaderCell(new Cell(0, 1).add(new Paragraph("NO.")).setFontSize(10).setBold());
            table.addHeaderCell(new Cell(0, 1).add(new Paragraph("INDEX NO.")).setFontSize(10).setBold());
            table.addHeaderCell(new Cell(0, 1).add(new Paragraph("PRESENT.")).setFontSize(10).setBold());
            table.addHeaderCell(new Cell(0, 1).add(new Paragraph("ABSENT.")).setFontSize(10).setBold());
            table.addHeaderCell(new Cell(0, 1).add(new Paragraph("TOTAL ATTENDANCE.")).setFontSize(10).setBold());

            //SET PDF TABLE DATA CONTENT
            dataTable.getListDataView().getItems().forEach(data -> {
                table.addCell(new Cell(0,  1).add(new Paragraph(String.valueOf(data.getId()))));
                table.addCell(new Cell(0,  1).add(new Paragraph(data.getIndexNumber())));
                table.addCell(new Cell(0,  1).add(new Paragraph(String.valueOf(data.getPresent()))));
                table.addCell(new Cell(0,  1).add(new Paragraph(String.valueOf(data.getAbscent()))));
                table.addCell(new Cell(0,  1).add(new Paragraph(String.valueOf(data.getTotalAttendance()))));
            });
            Paragraph footer = new Paragraph("Date Generated: "+ date).setFontSize(8).setBold().setItalic();
            document.add(table).add(footer);
            document.close();
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static InputStream generateCSVFile() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        return null;
    }

}//end of class..
