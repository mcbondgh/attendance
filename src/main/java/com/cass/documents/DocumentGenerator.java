package com.cass.documents;

import ar.com.fdvs.dj.domain.constants.Border;
import com.cass.data.ActivitiesEntity;
import com.cass.data.AttendanceRecordsEntity;
import com.cass.data.StudentEntity;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.vaadin.flow.component.grid.Grid;
import org.apache.batik.css.engine.value.svg12.DeviceColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlgraphics.image.codec.png.PNGEncodeParam;
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

    public static InputStream generateAttendancePdf(LocalDate startDate, LocalDate endDate, String className, String course, String programme, Grid<AttendanceRecordsEntity> dataTable) {
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

            Table table = new Table(6);
            table.useAllAvailableWidth();
            table.setAutoLayout();

            String dateDiv = "FROM: " + startDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                    + "   |   TO: " + endDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
            Paragraph dateRange = new Paragraph(dateDiv).setFontSize(12).setTextAlignment(TextAlignment.CENTER).setBold();
            Paragraph heading = new Paragraph("ATTENDANCE REPORT");
            heading.setTextAlignment(TextAlignment.CENTER);
            heading.setBold();
            heading.setFontSize(17);
            //CREATE TABLE HEADER.
            table.addHeaderCell(new Cell(0,6).add(heading));
            table.addHeaderCell(new Cell(0,2).add(new Paragraph("DATE RANGE")).setFontSize(12).setBold());
            table.addHeaderCell(new Cell(0,4).add(dateRange).setFontSize(12).setBold());
            table.addHeaderCell(new Cell(0, 2).add(new Paragraph("CLASS")).setFontSize(12).setBold());
            table.addHeaderCell(new Cell(0, 4).add(new Paragraph(className).setTextAlignment(TextAlignment.CENTER)).setFontSize(12).setBold());
            table.addHeaderCell(new Cell(0,2).add(new Paragraph("COURSE")));
            table.addHeaderCell(new Cell(0,4).add(new Paragraph(course)).setBold()).setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell(new Cell(0,2).add(new Paragraph("PROGRAMME")).setBold());
            table.addHeaderCell(new Cell(0,4).add(new Paragraph(programme).setTextAlignment(TextAlignment.CENTER)).setBold());

            //set table content headers
            table.addHeaderCell(new Cell(0, 1).add(new Paragraph("NO.")).setFontSize(10).setBold());
            table.addHeaderCell(new Cell(0, 1).add(new Paragraph("INDEX NO.")).setFontSize(10).setBold());
            table.addHeaderCell(new Cell(0, 1).add(new Paragraph("LEVEL")).setFontSize(10).setBold());
            table.addHeaderCell(new Cell(0, 1).add(new Paragraph("PRESENT.")).setFontSize(10).setBold());
            table.addHeaderCell(new Cell(0, 1).add(new Paragraph("ABSENT.")).setFontSize(10).setBold());
            table.addHeaderCell(new Cell(0, 1).add(new Paragraph("TOTAL ATTENDANCE.")).setFontSize(10).setBold());

            //SET PDF TABLE DATA CONTENT
            dataTable.getListDataView().getItems().forEach(data -> {
                table.addCell(new Cell(0,  1).add(new Paragraph(String.valueOf(data.getId()))));
                table.addCell(new Cell(0,  1).add(new Paragraph(data.getIndexNumber())));
                table.addCell(new Cell(0,  1).add(new Paragraph(data.getLevel())));
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

    public static InputStream generateStudentList(String programme, Grid<StudentEntity> table) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try( Workbook workbook = new XSSFWorkbook()) {
            Sheet worksheet = workbook.createSheet("CLASS LIST");

            //CREATE SHEET HEADER COLUMNS
            Row titleRow = worksheet.createRow(0);

            //merge title row cells.
            titleRow.createCell(0).setCellValue(programme + " CLASS LIST");

            worksheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            Row cellHeaders = worksheet.createRow(2);
            cellHeaders.createCell(0   ).setCellValue("INDEX NUMBER");
            cellHeaders.createCell(1   ).setCellValue("FULL NAME");
            cellHeaders.createCell(2   ).setCellValue("YEAR");
            cellHeaders.createCell(3  ).setCellValue("LEVEL");
            cellHeaders.createCell(4).setCellValue("GROUP");
            cellHeaders.createCell(5).setCellValue("STATUS");

            int tableSize = table.getListDataView().getItemCount();
            int rowCounter = 3;
            for (int i = 0; i < tableSize; i++) {
                Row row = worksheet.createRow(rowCounter++);
                StudentEntity items = table.getListDataView().getItem(i);
                row.createCell(0).setCellValue(items.getIndexNumber());
                row.createCell(1).setCellValue(items.getFullName());
                row.createCell(2).setCellValue(items.getYearGroup());
                row.createCell(3).setCellValue(items.getLevel());
                row.createCell(4).setCellValue(items.getSection());
                row.createCell(5).setCellValue(items.getStatus() == 1 ? "ACTIVE" : "INACTIVE");
            }
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static InputStream generateActivityReportPDF(String className1, String program1, Grid<ActivitiesEntity> reportsTable, String activityType, String section) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter pdfWriter = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);

        Paragraph header = new Paragraph("CLASS ASSESSMENT RECORDS").setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER);
        Table table = new Table(5);
        table.useAllAvailableWidth();

        Paragraph className = new Paragraph("Programme: " + className1.toUpperCase() + "  |  Course: " + program1.toUpperCase() ).setBold();
        Paragraph activityType1 = new Paragraph("Section: " + section +  "  |  Activity Type: " + activityType.toUpperCase() ).setBold();
//        Paragraph program = new Paragraph().setBold();
//        Paragraph semester = new Paragraph(semester1.toUpperCase() ).setBold();
//        Paragraph yearGroup = new Paragraph().setBold();
        Div container = new Div();
        container.add(className);
        container.add(activityType1);
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
