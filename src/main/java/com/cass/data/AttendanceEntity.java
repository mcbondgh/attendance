package com.cass.data;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

public class AttendanceEntity {
    int id,rowNumber;
    String indexNumber;
    String className, programeName;
    String attendanceValue;
    Date attendanceDate;
    Timestamp dateRecorded;

    
	public AttendanceEntity() {
	}


	public AttendanceEntity(int id, int rowNumber, String indexNumber, String className, String programeName,
			String attendanceValue, Date attendanceDate, Timestamp dateRecorded) {
		this.id = id;
		this.rowNumber   = rowNumber  ;
		this.indexNumber = indexNumber;
		this.className = className;
		this.programeName = programeName;
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


	public String getIndexNumber() {
		return indexNumber;
	}


	public void setIndexNumber(String indexNumber) {
		this.indexNumber = indexNumber;
	}


	public String getclassName() {
		return className;
	}


	public void setclassName(String className) {
		this.className = className;
	}


	public String getprogrameName() {
		return programeName;
	}


	public void setprogrameName(String programeName) {
		this.programeName = programeName;
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


public boolean isAttendanceTaken() {
        return attendanceDate == null ? false : true;
    }
    


}//end of class...
