package com.cass.data;

import java.sql.Timestamp;

public class ProgrammesEntity {

    //id, programme_name, programme_type, department, status, date_created
    private int id;
    private String programme;
    private String type;
    private boolean status;
    private Timestamp dateCreated;
    private String department;

    public ProgrammesEntity() {
    }
    public ProgrammesEntity(int id, String programme, String type, boolean status, Timestamp dateCreated, String department) {
        this.id = id;
        this.programme = programme;
        this.type = type;
        this.status = status;
        this.dateCreated = dateCreated;
        this.department = department;
    }

    //getters and setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}//end of class
