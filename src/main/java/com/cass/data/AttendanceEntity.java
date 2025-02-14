package com.cass.data;

import java.sql.Date;
import java.sql.Timestamp;

public class AttendanceEntity {
    int id,rowNumber;
    String indexNumber;
    String className, programme, course;
    String attendanceValue;
    Date attendanceDate;
    Timestamp dateRecorded;
	private String yearGroup, level;

    
	public AttendanceEntity() {
	}


	public AttendanceEntity(int id, int rowNumber, String indexNumber, String className, String programme,
			String attendanceValue, Date attendanceDate, Timestamp dateRecorded) {
		this.id = id;
		this.rowNumber   = rowNumber  ;
		this.indexNumber = indexNumber;
		this.className = className;
		this.programme = programme;
		this.attendanceValue = attendanceValue;
		this.attendanceDate = attendanceDate;
		this.dateRecorded = dateRecorded;
        
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

	public String getYearGroup() {
		return yearGroup;
	}

	public void setYearGroup(String yearGroup) {
		this.yearGroup = yearGroup;
	}

	public String getProgramme() {
		return programme;
	}

	public void setProgramme(String programme) {
		this.programme = programme;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getIndexNumber() {
		return indexNumber;
	}


	public void setIndexNumber(String indexNumber) {
		this.indexNumber = indexNumber;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getclassName() {
		return className;
	}


	public void setclassName(String className) {
		this.className = className;
	}

	public String getAttendanceValue() {
		return attendanceValue;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
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


public boolean isAttendanceTaken() {
        return attendanceDate == null ? false : true;
    }
    


}//end of class...
