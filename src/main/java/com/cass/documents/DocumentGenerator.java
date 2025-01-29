package com.cass.documents;

import ar.com.fdvs.dj.domain.constants.Border;
import com.cass.data.ActivitiesEntity;
import com.cass.data.AttendanceRecordsEntity;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.vaadin.flow.component.grid.Grid;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.plutext.jaxb.xslfo.TableHeader;

import java.io.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.DoubleConsumer;

public class DocumentGenerator {

    public static InputStream generateAttendancePdf(String className, String programme, Grid<AttendanceRecordsEntity> dataTable) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            String date = LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
//            dataTable.getListDataView().getItems().forEach(item -> {
//                String format = item.getAttendanceDate().toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
//                attendanceDate.set(format);
//            });
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
//            table.addHeaderCell(new Cell(0,1).add(new Paragraph("DATE")));
//            table.addHeaderCell(new Cell(0,1).add(new Paragraph(attendanceDate.get())).setBold());
            table.addHeaderCell(new Cell(0,2).add(new Paragraph("PROGRAMME")).setBold());
            table.addHeaderCell(new Cell(0,3).add(new Paragraph(programme)).setBold());

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

    public static InputStream generateCSVFile(String className, String courseName, Grid<ActivitiesEntity> table) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try( Workbook workbook = new XSSFWorkbook()) {
            Sheet worksheet = workbook.createSheet("CLASS ASSESSMENT");
            String titleText = "CLASS: " + className.toUpperCase() + "    |   COURSE: " +courseName.toUpperCase();


            //CREATE SHEET HEADER COLUMNS
            Row titleRow = worksheet.createRow(0);
            Row subTitle = worksheet.createRow(1);

            //merge title row cells.
            titleRow.createCell(0).setCellValue("CLASS ASSESSMENT RECORDS");
            subTitle.createCell(0).setCellValue(titleText);

            worksheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
            worksheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));

            Row cellHeaders = worksheet.createRow(2);
            cellHeaders.createCell(0   ).setCellValue("ID");
            cellHeaders.createCell(1   ).setCellValue("INDEX NUMBER");
            cellHeaders.createCell(2   ).setCellValue("TOTAL SCORE");
            cellHeaders.createCell(3   ).setCellValue("MAXIMUM SCORE");
            cellHeaders.createCell(4   ).setCellValue("ACTIVITY COUNT");

            int tableSize = table.getListDataView().getItemCount();
            int rowCounter = 3;
            for (int i = 0; i < tableSize; i++) {
                Row row = worksheet.createRow(rowCounter++);
                ActivitiesEntity items = table.getListDataView().getItem(i);
                row.createCell(0).setCellValue(items.getId());
                row.createCell(1).setCellValue(items.getIndexNumber());
                row.createCell(2).setCellValue(items.getScore());
                row.createCell(3).setCellValue(items.getmaximumScore());
                row.createCell(4).setCellValue(items.getActivityCount());
            }
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }catch (Exception e){}

        return null;
    }

    public static InputStream generateActivityReportPDF(String className1, String program1, Grid<ActivitiesEntity> reportsTable) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter pdfWriter = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);

        Paragraph header = new Paragraph("CLASS ASSESSMENT RECORDS").setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER);
        Table table = new Table(5);
        table.useAllAvailableWidth();

        Paragraph className = new Paragraph("CLASS: " + className1.toUpperCase() + "    |   COURSE: " +program1.toUpperCase()).setBold();
//        Paragraph program = new Paragraph().setBold();
//        Paragraph semester = new Paragraph(semester1.toUpperCase() ).setBold();
//        Paragraph yearGroup = new Paragraph().setBold();
        Div container = new Div();
        container.add(className);
        container.setTextAlignment(TextAlignment.CENTER);
        container.setFontSize(10);
//        container.setHeight(1);
//        container.setMarginBottom(5);

        //set dynamic table size and set table columns
//        int tableSize = reportsTable.getColumns().size();
//        for (int i = 0; i < tableSize; i++) {
//            Paragraph items = new Paragraph(reportsTable.getColumns().get(i).getHeaderText()).setBold().setFontSize(10);
//            table.addHeaderCell(new Cell(0, 1).add(items));
//        }
        //table header columns and their respective names.
        table.addHeaderCell(new Cell(0, 1).add(new Paragraph("ID").setBold().setFontSize(10)));
        table.addHeaderCell(new Cell(0, 1).add(new Paragraph("INDEX NUMBER").setBold().setFontSize(10)));
        table.addHeaderCell(new Cell(0, 1).add(new Paragraph("TOTAL SCORE").setBold().setFontSize(10)));
        table.addHeaderCell(new Cell(0, 1).add(new Paragraph("MAXIMUM SCORE").setBold().setFontSize(10)));
        table.addHeaderCell(new Cell(0, 1).add(new Paragraph("ACTIVITY COUNT").setBold().setFontSize(10)));

        //set table values
        reportsTable.getListDataView().getItems().forEach(each -> {
            table.addCell(new Cell(0, 1).add(new Paragraph(String.valueOf(each.getId()))));
            table.addCell(new Cell(0, 1).add(new Paragraph(each.getIndexNumber())));
//            table.addCell(new Cell(0, 1).add(new Paragraph(each.getFullname())));
            table.addCell(new Cell(0, 1).add(new Paragraph(String.valueOf(each.getScore()))));
            table.addCell(new Cell(0, 1).add(new Paragraph(String.valueOf(each.getmaximumScore()))));
            table.addCell(new Cell(0, 1).add(new Paragraph(String.valueOf(each.getActivityCount()))));
        });

        Paragraph date = new Paragraph("Generated Date: "+LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)))
                .setFontSize(8).setBold().setItalic();
        document.add(header).add(container).add(new Paragraph()).add(table).add(new Paragraph().add(date));
        document.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

}//end of class..
