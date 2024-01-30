package com.cass.data;

import java.sql.Date;
import java.sql.Timestamp;

import com.vaadin.flow.component.html.H6;

public class AttendanceRecordsEntity {
    private int id, rowNumber;
    private String indexNumber, className, programeName;
    private String fullname;
    private String attendanceValue;
    private Date attendanceDate;
    private Timestamp dateRecorded;
    int present, abscent, totalAttendance, excused;
    private H6 presentLabel = new H6();
    private H6 abscentLabel = new H6();
    private H6 totalAttendanceLabel = new H6();
    private H6 excusedLabel = new H6();

    public AttendanceRecordsEntity() {
    }

    public AttendanceRecordsEntity(int id, String indexNumber, String fullname,int present, int abscent, int totalAttendance, int excused) {
        this.id = id;
        this.indexNumber = indexNumber;
        this.fullname = fullname;
        this.present = present;
        this.abscent = abscent;
        this.excused = excused;
        this.totalAttendance = totalAttendance;
        styleComponent();
    }

    private void styleComponent() {
        presentLabel.getElement().getStyle().set("color", "green");
        abscentLabel.getElement().getStyle().set("color", "red");
        totalAttendanceLabel.getElement().getStyle().set("color", "blue");
        excusedLabel.getElement().getStyle().set("color", "orange");

        presentLabel.setText(String.valueOf(present));
        abscentLabel.setText(String.valueOf(abscent));
        totalAttendanceLabel.setText(String.valueOf(totalAttendance));
        excusedLabel.setText(String.valueOf(excused));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(String indexNumber) {
        this.indexNumber = indexNumber;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getProgrameName() {
        return programeName;
    }

    public void setProgrameName(String programeName) {
        this.programeName = programeName;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAttendanceValue() {
        return attendanceValue;
    }

    public void setAttendanceValue(String attendanceValue) {
        this.attendanceValue = attendanceValue;
    }

    public Date getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(Date attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public Timestamp getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(Timestamp dateRecorded) {
        this.dateRecorded = dateRecorded;
    }

    public int getPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
    }

    public int getAbscent() {
        return abscent;
    }

    public void setAbscent(int abscent) {
        this.abscent = abscent;
    }

    public int getTotalAttendance() {
        return totalAttendance;
    }

    public void setTotalAttendance(int totalAttendance) {
        this.totalAttendance = totalAttendance;
    }

    public int getExcused() {
        return excused;
    }

    public void setExcused(int excused) {
        this.excused = excused;
    }

    public H6 getPresentLabel() {
        return presentLabel;
    }

    public void setPresentLabel(H6 presentLabel) {
        this.presentLabel = presentLabel;
    }

    public H6 getAbscentLabel() {
        return abscentLabel;
    }

    public void setAbscentLabel(H6 abscentLabel) {
        this.abscentLabel = abscentLabel;
    }

    public H6 getTotalAttendanceLabel() {
        return totalAttendanceLabel;
    }

    public void setTotalAttendanceLabel(H6 totalAttendanceLabel) {
        this.totalAttendanceLabel = totalAttendanceLabel;
    }

    public H6 getExcusedLabel() {
        return excusedLabel;
    }

    public void setExcusedLabel(H6 excusedLabel) {
        this.excusedLabel = excusedLabel;
    }

    

}// end of class...
