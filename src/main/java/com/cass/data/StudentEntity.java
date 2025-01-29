package com.cass.data;

import java.sql.Timestamp;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;

public class StudentEntity {
    private int id;
    private String indexNumber, fullName, programme;
    private String studentClass, department;
    private byte status;
    private Timestamp dateAdded, dateUpdated;
    private RadioButtonGroup<String> attendanceButton = new RadioButtonGroup<>();
    private ComboBox<String> attendanceSelector = new ComboBox<>();
    private String yearGroup;
    private String level, section;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public StudentEntity() {
    }
    
    
    public StudentEntity(int id, String indexNumber, String fullName, String studentClass) {
        this.id = id;
        this.indexNumber = indexNumber;
        this.fullName = fullName;
        this.studentClass = studentClass;
        attendanceSelector.setItems("present", "abscent", "excused");
        attendanceSelector.addClassName("radio-buttons");
        this.attendanceButton.setItems("P", "A", "excused");
        attendanceButton.setClassName("radio-buttons");
    }


    public StudentEntity(int id, String indexNumber, String fullName, String programe, String studentClass,
            String department, byte status, Timestamp dateAdded, Timestamp dateUpdated) {
        this.id = id;
        this.indexNumber = indexNumber;
        this.fullName = fullName;
        this.programme = programe;
        this.studentClass = studentClass;
        this.department = department;
        this.status = status;
        this.dateAdded = dateAdded;
        this.dateUpdated = dateUpdated;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getIndexNumber() {
        return indexNumber;
    }
    public void setIndexNumber(String indexNumber) {
        this.indexNumber = indexNumber;
    }
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getProgramme() {
        return programme;
    }
    public void setProgramme(String programme) {
        this.programme = programme;
    }
    public String getStudentClass() {
        return studentClass;
    }
    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public byte getStatus() {
        return status;
    }
    public void setStatus(byte status) {
        this.status = status;
    }
    public Timestamp getDateAdded() {
        return dateAdded;
    }
    public void setDateAdded(Timestamp dateAdded) {
        this.dateAdded = dateAdded;
    }
    public Timestamp getDateUpdated() {
        return dateUpdated;
    }
    public void setDateUpdated(Timestamp dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

	public RadioButtonGroup<String> getAttendanceButton() {
		return attendanceButton;
	}

    public String getYearGroup() {
        return yearGroup;
    }

    public void setYearGroup(String yearGroup) {
        this.yearGroup = yearGroup;
    }

    public void setAttendanceButton(RadioButtonGroup<String> attendanceButton) {
		this.attendanceButton = attendanceButton;
	}


	public ComboBox<String> getAttendanceSelector() {
		return attendanceSelector;
	}


	public void setAttendanceSelector(ComboBox<String> attendanceSelector) {
		this.attendanceSelector = attendanceSelector;
	}

    
}
